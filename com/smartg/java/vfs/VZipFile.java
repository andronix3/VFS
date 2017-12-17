/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of imagero Andrey Kuznetsov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.smartg.java.vfs;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.Icon;

import com.smartg.java.util.ArrayIterator;
import com.smartg.java.util.SafeIterator;

public class VZipFile implements VFile {

	private VFile file;

	private Map<String, VFile> map = new HashMap<>();
	private ArrayList<VFile> children = new ArrayList<>();

	volatile boolean initialized;

	public VZipFile(VFile file) {
		this.file = file;
	}

	private void add(String[] path, int zipIndex, long size) {
		if (path.length == 0) {
			return;
		}
		String key = path[0];

		if (path.length == 1) {
			VZipFileEntry entry = (VZipFileEntry) map.get(key);
			if (entry == null) {
				entry = new VZipFileEntry(this, this, key, zipIndex, size);
				String name = entry.getName().toUpperCase();
				if (name.endsWith(".ZIP") || name.endsWith(".JAR")) {
					VZipFile zip = new VZipFile(entry);
					map.put(key, zip);
					children.add(zip);
				} else {
					map.put(key, entry);
					children.add(entry);
				}
			} else {
				entry.setSize(size);
				entry.setZipEntryIndex(zipIndex);
			}
		} else {
			VZipFileEntry entry = (VZipFileEntry) map.get(path[0]);
			if (entry == null) {
				entry = new VZipFileEntry(this, this, key, -1, -1);
				map.put(key, entry);
				children.add(entry);
			}
			entry.add(path, 1, zipIndex, size);
		}
	}

	synchronized void init() {
		initialized = true;
		ZipInputStream zip = null;
		try {
			zip = new ZipInputStream(getInputStream());
			ZipEntry entry = null;
			int zipIndex = 0;
			while (true) {
				if (zip.available() == 0) {
					break;
				}
				entry = zip.getNextEntry();
				if (entry == null) {
					break;
				}
				String name = entry.getName();
				String[] path = name.split("/");
				System.out.println(name + " " + zipIndex);
				add(path, zipIndex++, entry.getSize());
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (zip != null) {
				try {
					zip.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public void create() throws IOException {
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	@Override
	public VFile get(String s) {
		if (!initialized) {
			init();
		}
		return map.get(s);
	}

	InputStream getZipEntryInputStream(int zipEntryIndex) throws IOException {
		return new ZipEntryInputStream(getInputStream(), zipEntryIndex);
	}

	@Override
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}

	@Override
	public Path toPath() {
		return file.toPath();
	}

	@Override
	public String getDisplayName() {
		return file.getDisplayName();
	}

	@Override
	public Icon getIcon() {
		return file.getIcon();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return file.getInputStream();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return file.getOutputStream();
	}

	@Override
	public String getParent() {
		return file.getParent();
	}

	@Override
	public VFile getParentFile() {
		return file.getParentFile();
	}

	@Override
	public int getPos() {
		return file.getPos();
	}

	@Override
	public boolean isDirectory() {
		return true;
	}

	@Override
	public boolean isHidden() {
		return file.isHidden();
	}

	@Override
	public long lastModified() {
		return file.lastModified();
	}

	@Override
	public long length() {
		return file.length();
	}

	@Override
	public String[] list() {
		if (!initialized) {
			init();
		}
		VFile[] entries = new VFile[children.size()];
		children.toArray(entries);
		String[] names = new String[entries.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = entries[i].getName();
		}
		return names;
	}

	@Override
	public String[] list(VFilenameFilter filter) {
		if (!initialized) {
			init();
		}
		ArrayList<VFile> v = new ArrayList<>();
		Enumeration<VFile> elements = new SafeIterator<>(children.iterator());
		while (elements.hasMoreElements()) {
			VFile f = elements.nextElement();
			if (filter.accept(f.getParentFile(), f.getName())) {
				v.add(f);
			}
		}
		VFile[] entries = new VFile[v.size()];
		v.toArray(entries);
		String[] names = new String[entries.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = entries[i].getName();
		}
		return names;
	}

	@Override
	public VFile[] listFiles() {
		if (!initialized) {
			init();
		}
		VFile[] entries = new VFile[children.size()];
		children.toArray(entries);
		return entries;
	}

	@Override
	public VFile[] listFiles(VFilenameFilter filter) {
		if (!initialized) {
			init();
		}
		ArrayList<VFile> v = new ArrayList<>();
		Enumeration<VFile> elements = new SafeIterator<>(children.iterator());
		while (elements.hasMoreElements()) {
			VFile f = elements.nextElement();
			if (filter.accept(f.getParentFile(), f.getName())) {
				v.add(f);
			}
		}
		VFile[] entries = new VFile[v.size()];
		v.toArray(entries);
		return entries;
	}

	@Override
	public boolean mkdir() {
		return file.mkdir();
	}

	@Override
	public boolean mkdirs() {
		return file.mkdirs();
	}

	@Override
	public boolean rename(String name) {
		return file.rename(name);
	}

	@Override
	public boolean rmdir(boolean rf) {
		return false;
	}

	@Override
	public void setDisplayName(String displayName) throws IOException {
		file.setDisplayName(displayName);
	}

	@Override
	public void setHidden(boolean hidden) throws IOException {
		file.setHidden(hidden);
	}

	@Override
	public void setPos(int pos) throws IOException {
		file.setPos(pos);
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	static class ZipEntryInputStream extends FilterInputStream {
		protected ZipEntryInputStream(InputStream in, int zipEntryIndex) throws IOException {
			super(getZipEntryStream(in, zipEntryIndex));
		}
	}

	static InputStream getZipEntryStream(InputStream in, int zipEntryIndex) throws IOException {
		ZipInputStream zip = new ZipInputStream(in);
		int index = 0;
		while (index++ <= zipEntryIndex) {
			zip.getNextEntry();
		}
		return zip;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VZipFile) {
			VZipFile zip = (VZipFile) obj;
			return zip.getAbsolutePath().equals(getAbsolutePath());
		}
		return false;
	}

	@Override
	public String getProtocolName() {
		return "zip";
	}

	@Override
	public int hashCode() {
		return getAbsolutePath().toLowerCase(Locale.ENGLISH).hashCode() ^ 1234321;
	}

	@Override
	public void writeFile(InputStream in) throws IOException {

	}

	@Override
	public Enumeration<VFile> files() {
		return new ArrayIterator<>(listFiles());
	}

	@Override
	public Enumeration<String> names() throws IOException {
		return new ArrayIterator<>(list());
	}
}
