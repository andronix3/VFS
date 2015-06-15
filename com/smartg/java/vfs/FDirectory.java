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

import javax.swing.Icon;

import com.smartg.java.util.ArrayIterator;
import com.smartg.java.util.SafeIterator;

public class FDirectory implements VFile {

    ArrayList<VFile> files = new ArrayList<VFile>();
    Path path;
    Icon icon;
    String name;
    String displayName;

    public FDirectory(Path path, String name) {
	this.path = path;
	this.name = name;
    }

    public FDirectory(VFile[] files, Path path, String name) {
	this.path = path;
	this.name = name;
	for (VFile f : files) {
	    this.files.add(f);
	}
    }

    public void add(VFile f) {
	files.add(f);
    }

    public void setIcon(Icon icon) {
	this.icon = icon;
    }

    public void create() throws IOException {

    }

    public boolean delete() {
	return false;
    }

    public boolean exists() {
	return false;
    }

    public Enumeration<VFile> files() {
	return new SafeIterator<VFile>(files.iterator());
    }

    public VFile get(String s) {
	for (VFile f : files) {
	    if (f.getName().equals(s)) {
		return f;
	    }
	}
	return null;
    }

    public Path toPath() {
	return path;
    }

    public String getAbsolutePath() {
	return path.toAbsolutePath().toString();
    }

    public String getDisplayName() {
	if (displayName != null) {
	    return displayName;
	}
	return name;
    }

    public Icon getIcon() {
	return icon;
    }

    public InputStream getInputStream() throws IOException {
	return null;
    }

    public String getName() {
	return name;
    }

    public OutputStream getOutputStream() throws IOException {
	return null;
    }

    public String getParent() {
	return null;
    }

    public VFile getParentFile() {
	return null;
    }

    public int getPos() {
	return 0;
    }

    public String getProtocolName() {
	return null;
    }

    public boolean isDirectory() {
	return true;
    }

    public boolean isHidden() {
	return false;
    }

    public long lastModified() {
	return 0;
    }

    public long length() {
	return 0;
    }

    public String[] list() {
	String[] list = new String[files.size()];
	int p = 0;
	for (VFile f : files) {
	    list[p++] = f.getName();
	}
	return list;
    }

    public String[] list(VFilenameFilter filter) {
	if (filter != null) {
	    ArrayList<String> list = new ArrayList<String>();
	    for (VFile f : files) {
		String name = f.getName();
		if (filter.accept(f.getParentFile(), name)) {
		    list.add(name);
		}
	    }
	    return list.toArray(new String[list.size()]);
	}
	return list();
    }

    @Override
    public String toString() {
	return getDisplayName();
    }

    public VFile[] listFiles() {
	return files.toArray(new VFile[files.size()]);
    }

    public VFile[] listFiles(VFilenameFilter filter) {
	if (filter != null) {
	    ArrayList<VFile> list = new ArrayList<VFile>();
	    for (VFile f : files) {
		if (filter.accept(f.getParentFile(), f.getName())) {
		    list.add(f);
		}
	    }
	    return list.toArray(new VFile[list.size()]);
	}
	return listFiles();
    }

    public boolean mkdir() {
	return false;
    }

    public boolean mkdirs() {
	return false;
    }

    public Enumeration<String> names() throws IOException {
//	return new SafeEnumerationWrapper<String>(Arrays.asList(list()).iterator());
	return new ArrayIterator<String>(list());
    }

    public boolean rename(String name) {
	return false;
    }

    public boolean rmdir(boolean rf) {
	return false;
    }

    public void setDisplayName(String displayName) throws IOException {
	this.displayName = displayName;
    }

    public void setHidden(boolean hidden) throws IOException {

    }

    public void setPos(int pos) throws IOException {

    }

    public void writeFile(InputStream in) throws IOException {

    }
}
