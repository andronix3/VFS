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
	    files[i] = file.get(list[i]);
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
