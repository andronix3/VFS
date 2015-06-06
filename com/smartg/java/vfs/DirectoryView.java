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
import java.util.ArrayList;

/**
 * DirectoryView - filtered directory, so list() returns only subset of children.
 * 
 * @author andrey
 * 
 */
public class DirectoryView extends VProxyFile implements VFile {

    String[] names;

    public DirectoryView(VFile dir, VFilenameFilter filter) {
	super(dir);
	if(filter == null) {
	    throw new NullPointerException();
	}
	if(!dir.isDirectory()) {
	    throw new IllegalArgumentException();
	}
	names = dir.list(filter);
    }
    
    public DirectoryView(VFile dir, int start, int count) {
	super(dir);
	if(!dir.isDirectory()) {
	    throw new IllegalArgumentException();
	}
	
	String[] list = dir.list();
	names = new String[count];
	System.arraycopy(list, start, names, 0, count);
    }
    
    public DirectoryView(VFile dir, String[] names) {
	super(dir);
	this.names = names;
    }

    protected DirectoryView(VFile dir) {
	super(dir);
    }

    private VFile[] createFiles(String[] list) {
	if (list == null) {
	    return new VFile[0];
	}
	VFile[] files = new VFile[list.length];
	for (int i = 0; i < files.length; i++) {
	    files[i] = getFile().get(list[i]);
	}
	return files;
    }

    public String[] list() {
	String[] list = new String[names.length];
	System.arraycopy(names, 0, list, 0, list.length);
	return list;
    }

    private String[] filter(VFilenameFilter filter, String[] names) {
	ArrayList<String> v = new ArrayList<String>();
	for (int i = 0; i < names.length; i++) {
	    if (filter.accept(this, names[i])) {
		v.add(names[i]);
	    }
	}
	String[] res = new String[v.size()];
	v.toArray(res);
	return res;
    }

    public String[] list(VFilenameFilter filter) {
	return filter(filter, names);
    }

    public VFile[] listFiles() {
	return createFiles(names);
    }

    public VFile[] listFiles(VFilenameFilter filter) {
	String[] list = filter(filter, names);
	return createFiles(list);
    }

    public InputStream getInputStream() throws IOException {
	return null;
    }

    public OutputStream getOutputStream() throws IOException {
	return null;
    }

    public void writeFile(InputStream in) throws IOException {
	
    }
}
