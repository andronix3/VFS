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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import com.smartg.java.util.ArrayEnumeration;
import com.smartg.java.util.SafeIterator;

public class VZipFileEntry implements VFile {

    static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();

    VFile parent;
    int zipEntryIndex;
    String name;
    VZipFile root;
    long size;

    Hashtable<String, VFile> ht = new Hashtable<String, VFile>();
    ArrayList<VFile> children = new ArrayList<VFile>();

    VZipFileEntry(VZipFile root, VFile parent, String name, int zipEntryIndex, long size) {
	this.root = root;
	this.parent = parent;
	this.name = name;
	this.zipEntryIndex = zipEntryIndex;
	this.size = size;
    }

    public void create() throws IOException {

    }

    public boolean delete() {
	return false;
    }

    public boolean exists() {
	return true;
    }

    public VFile get(String s) {
	return ht.get(s);
    }

    void add(String[] path, int pathIndex, int zipIndex, long size) {
	if (path.length == 0 || pathIndex >= path.length) {
	    return;
	}
	String key = path[pathIndex];

	if (pathIndex == path.length - 1) {
	    VZipFileEntry entry = (VZipFileEntry) ht.get(key);
	    if (entry == null) {
		entry = new VZipFileEntry(root, this, key, zipIndex, size);
		String name = entry.getName().toUpperCase();
		if (name.endsWith(".ZIP") || name.endsWith(".JAR")) {
		    VZipFile zip = new VZipFile(entry);
		    ht.put(key, zip);
		    children.add(zip);
		} else {
		    ht.put(key, entry);
		    children.add(entry);
		}
	    } else {
		entry.setSize(size);
		entry.setZipEntryIndex(zipIndex);
	    }
	} else {
	    VZipFileEntry entry = (VZipFileEntry) ht.get(path[pathIndex]);
	    if (entry == null) {
		entry = new VZipFileEntry(root, this, key, -1, -1);
		ht.put(key, entry);
		children.add(entry);
	    }
	    entry.add(path, pathIndex + 1, zipIndex, size);
	}
    }

    void setZipEntryIndex(int index) {
	this.zipEntryIndex = index;
    }

    void setSize(long size2) {
	this.size = size2;
    }

    public String getAbsolutePath() {
	if (parent != null) {
	    return parent.getAbsolutePath() + "/" + getName();
	}
	return "/" + getName();
    }
    
    public Path toPath() {
	return null;
    }

    public String getDisplayName() {
	return getName();
    }

    public Icon getIcon() {
	if (isDirectory()) {
	    VFile parentFile = root.getParentFile();
	    if (parentFile instanceof JFile) {
		JFile jFile = (JFile) parentFile;
		return fileSystemView.getSystemIcon(jFile.f);
	    }
	}
	return null;
    }

    public InputStream getInputStream() throws IOException {
	return root.getZipEntryInputStream(zipEntryIndex);
    }

    public String getName() {
	return name;
    }

    public OutputStream getOutputStream() throws IOException {
	return null;
    }

    public String getParent() {
	return parent.getName();
    }

    public VFile getParentFile() {
	return parent;
    }

    public int getPos() {
	return 0;
    }

    public boolean isDirectory() {
	return children.size() != 0;
    }

    public boolean isHidden() {
	return false;
    }

    public long lastModified() {
	return root.lastModified();
    }

    public long length() {
	return size;
    }

    public String[] list() {
	VFile[] entries = new VFile[children.size()];
	children.toArray(entries);
	String[] names = new String[entries.length];
	for (int i = 0; i < names.length; i++) {
	    names[i] = entries[i].getName();
	}
	return names;
    }

    public String[] list(VFilenameFilter filter) {
	ArrayList<VFile> v = new ArrayList<VFile>();
	Enumeration<VFile> elements = new SafeIterator<VFile>(children.iterator());
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

    public VFile[] listFiles() {
	VFile[] entries = new VFile[children.size()];
	children.toArray(entries);
	return entries;
    }

    public VFile[] listFiles(VFilenameFilter filter) {
	ArrayList<VFile> v = new ArrayList<VFile>();
	Enumeration<VFile> elements = new SafeIterator<VFile>(children.iterator());
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

    public boolean mkdir() {
	return false;
    }

    public boolean mkdirs() {
	return false;
    }

    public boolean rename(String name) {
	return false;
    }

    public boolean rmdir(boolean rf) {
	return false;
    }

    public void setDisplayName(String displayName) throws IOException {

    }

    public void setHidden(boolean hidden) throws IOException {

    }

    public void setPos(int pos) throws IOException {

    }

    public String toString() {
	return getDisplayName();
    }

    public boolean equals(Object obj) {
	if (obj instanceof VZipFileEntry) {
	    VZipFileEntry entry = (VZipFileEntry) obj;
	    if (root.equals(entry.root)) {
		return zipEntryIndex == entry.zipEntryIndex;
	    }
	}
	return false;
    }

    public String getProtocolName() {
	return "zip";
    }

    @Override
    public int hashCode() {
	return name.hashCode() * zipEntryIndex;
    }

    public void writeFile(InputStream in) throws IOException {
	
    }
    
    public Enumeration<VFile> files() {
	return new ArrayEnumeration<VFile>(listFiles());
    }

    public Enumeration<String> names() throws IOException {
	return new ArrayEnumeration<String>(list());
    }
}
