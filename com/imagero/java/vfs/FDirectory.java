package com.imagero.java.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.Icon;

import com.smartg.java.util.ArrayEnumeration;
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
	return new ArrayEnumeration<String>(list());
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
