package com.smartg.java.vfs;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.Icon;

import com.smartg.java.util.ArrayEnumeration;
import com.smartg.java.util.SafeIterator;

public class VZipFile implements VFile {

    VFile file;

    Hashtable<String, VFile> ht = new Hashtable<String, VFile>();
    ArrayList<VFile> children = new ArrayList<VFile>();

    volatile boolean initialized;

    public VZipFile(VFile file) {
	this.file = file;
    }

    void add(String[] path, int zipIndex, long size) {
	if (path.length == 0) {
	    return;
	}
	String key = path[0];

	if (path.length == 1) {
	    VZipFileEntry entry = (VZipFileEntry) ht.get(key);
	    if (entry == null) {
		entry = new VZipFileEntry(this, this, key, zipIndex, size);
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
	    VZipFileEntry entry = (VZipFileEntry) ht.get(path[0]);
	    if (entry == null) {
		entry = new VZipFileEntry(this, this, key, -1, -1);
		ht.put(key, entry);
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
//		System.out.println(name + " " + zipIndex);
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

    public void create() throws IOException {
    }

    public boolean delete() {
	return false;
    }

    public boolean exists() {
	return file.exists();
    }

    public VFile get(String s) {
	if (!initialized) {
	    init();
	}
	return ht.get(s);
    }

    InputStream getZipEntryInputStream(int zipEntryIndex) throws IOException {
	return new ZipEntryInputStream(getInputStream(), zipEntryIndex);
    }

    public String getAbsolutePath() {
	return file.getAbsolutePath();
    }

    public Path toPath() {
	return file.toPath();
    }

    public String getDisplayName() {
	return file.getDisplayName();
    }

    public Icon getIcon() {
	return file.getIcon();
    }

    public InputStream getInputStream() throws IOException {
	return file.getInputStream();
    }

    public String getName() {
	return file.getName();
    }

    public OutputStream getOutputStream() throws IOException {
	return file.getOutputStream();
    }

    public String getParent() {
	return file.getParent();
    }

    public VFile getParentFile() {
	return file.getParentFile();
    }

    public int getPos() {
	return file.getPos();
    }

    public boolean isDirectory() {
	return true;
    }

    public boolean isHidden() {
	return file.isHidden();
    }

    public long lastModified() {
	return file.lastModified();
    }

    public long length() {
	return file.length();
    }

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

    public String[] list(VFilenameFilter filter) {
	if (!initialized) {
	    init();
	}
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
	if (!initialized) {
	    init();
	}
	VFile[] entries = new VFile[children.size()];
	children.toArray(entries);
	return entries;
    }

    public VFile[] listFiles(VFilenameFilter filter) {
	if (!initialized) {
	    init();
	}
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
	return file.mkdir();
    }

    public boolean mkdirs() {
	return file.mkdirs();
    }

    public boolean rename(String name) {
	return file.rename(name);
    }

    public boolean rmdir(boolean rf) {
	return false;
    }

    public void setDisplayName(String displayName) throws IOException {
	file.setDisplayName(displayName);
    }

    public void setHidden(boolean hidden) throws IOException {
	file.setHidden(hidden);
    }

    public void setPos(int pos) throws IOException {
	file.setPos(pos);
    }

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
    
    

    public boolean equals(Object obj) {
	if(obj instanceof VZipFile) {
	    VZipFile zip = (VZipFile) obj;
	    return zip.getAbsolutePath().equals(getAbsolutePath());
	}
	return false;
    }

    public String getProtocolName() {
	return "zip";
    }

    @Override
    public int hashCode() {
	return getAbsolutePath().toLowerCase(Locale.ENGLISH).hashCode() ^ 1234321;
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
