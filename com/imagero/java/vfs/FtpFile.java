package com.imagero.java.vfs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.Icon;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.smartg.java.util.ArrayEnumeration;

/**
 * VFile implementation for FTP (File Transfer Protocol).
 * 
 * @author andrey
 * 
 */
public class FtpFile implements VFile {

    FTPClient ftpClient;
    FTPFile ftpFile;

    FTPFile[] children;
    FtpFile parent;
    
    String name;

    public static VFile get(FTPClient ftpClient, String path) {
	return new FtpRoot(ftpClient, path).resolve(path);
    }

    /**
     * @param path  
     */
    public FtpFile(FTPClient ftpClient, String path) {
	this.ftpClient = ftpClient;
    }

    public FtpFile(FtpFile parent, String name) {
	if (parent == null) {
	    throw new NullPointerException();
	}
	this.parent = parent;
	this.ftpClient = parent.ftpClient;
	this.ftpFile = parent.getFtpFile(name);
	this.name = name;
    }

    public void create() throws IOException {
	if (exists()) {
	    return;
	}
	byte[] buf = new byte[0];
	ByteArrayInputStream bais = new ByteArrayInputStream(buf);
	ftpClient.storeFile(getAbsolutePath(), bais);
    }

    public boolean delete() {
	try {
	    if (!isDirectory()) {
		return ftpClient.deleteFile(getAbsolutePath());
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return false;
    }

    public boolean exists() {
	if(ftpFile == null) {
	    return false;
	}
	if (ftpFile.isDirectory()) {
	    return true;
	} else if (ftpFile.isFile()) {
	    return true;
	} else if (ftpFile.isSymbolicLink()) {
	    return true;
	}
	return false;
    }

    public VFile get(String s) {
	if (isDirectory()) {
//	    s = s.toUpperCase();
	    VFile vFile = new FtpFile(this, s);

	    if (s.endsWith(".ZIP") || s.endsWith(".JAR")) {
		return new VZipFile(vFile);
	    }
	    return vFile;
	}
	return null;
    }

    public String getAbsolutePath() {
	if (parent != null) {
	    return parent.getAbsolutePath() + "/" + getName();
	}
	return "/" + getName();
    }
    
    /**
     * TODO implement
     */
    public Path toPath() {
	return null;
    }

    public String getDisplayName() {
	return getName();
    }

    public String toString() {
	return getDisplayName();
    }

    public InputStream getInputStream() throws IOException {
	return ftpClient.retrieveFileStream(getAbsolutePath());
    }

    public String getName() {
	if(ftpFile != null) {
	    return ftpFile.getName();
	}
	return name;
    }

    public OutputStream getOutputStream() throws IOException {
	String absolutePath = getAbsolutePath();
	OutputStream storeFileStream = ftpClient.storeFileStream(absolutePath);
	if(storeFileStream == null) {
	    byte [] data = new byte[4];
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ftpClient.storeFile(absolutePath, in);
	    storeFileStream = ftpClient.storeFileStream(absolutePath);
	}
	return storeFileStream;
    }

    public String getParent() {
	return getParentFile().getAbsolutePath();
    }

    public VFile getParentFile() {
	return parent;
    }

    public int getPos() {
	if (parent != null) {
	    String[] list = parent.list();
	    for (int i = 0; i < list.length; i++) {
		if (list[i].equals(getName())) {
		    return i;
		}
	    }
	}
	return 0;
    }

    public boolean isDirectory() {
	if(ftpFile != null) {
	    return ftpFile.isDirectory();
	}
	return false;
    }

    public boolean isHidden() {
	return false;
    }

    public long lastModified() {
	if(ftpFile != null) {
	    return ftpFile.getTimestamp().getTimeInMillis();
	}
	return 0L;
    }

    public long length() {
	if (isDirectory()) {
	    return 0;
	}
	return ftpFile.getSize();
    }

    public String[] list() {
	if (isDirectory()) {
	    FTPFile[] files = getChildren();
	    if (files == null) {
		return null;
	    }
	    String[] res = new String[files.length];
	    for (int i = 0; i < res.length; i++) {
		res[i] = files[i].getName();
	    }
	    return res;
	}
	return null;
    }

    private FTPFile[] getChildren() {
	if (children == null) {
	    try {
		children = ftpClient.listFiles(getAbsolutePath());
	    } catch (IOException e) {
		e.printStackTrace();
		children = new FTPFile[0];
	    }
	}
	return children;
    }

    private FTPFile getFtpFile(String name) {
	FTPFile[] children = getChildren();
	if (children != null) {
	    for (int i = 0; i < children.length; i++) {
		String name2 = children[i].getName();
		if (name2.equals(name)) {
		    return children[i];
		}
	    }
	}
	return null;
    }

    public String[] list(VFilenameFilter filter) {
	if (isDirectory()) {
	    FTPFile[] children = getChildren();
	    ArrayList<String> v = new ArrayList<String>();
	    for (int i = 0; i < children.length; i++) {
		String name = children[i].getName();
		if (filter.accept(this, name)) {
		    v.add(name);
		}
	    }
	    String[] res = new String[v.size()];
	    v.toArray(res);
	    return res;
	}
	return null;
    }

    public VFile[] listFiles() {
	if (isDirectory()) {
	    FTPFile[] children = getChildren();
	    VFile[] res = new VFile[children.length];
	    for (int i = 0; i < res.length; i++) {
		res[i] = new FtpFile(this, children[i].getName());
	    }
	    return res;
	}
	return null;
    }

    public VFile[] listFiles(VFilenameFilter filter) {
	if (isDirectory()) {
	    String[] list = list(filter);
	    VFile[] res = new VFile[list.length];
	    for (int i = 0; i < res.length; i++) {
		res[i] = new FtpFile(this, list[i]);
	    }
	    return res;
	}
	return null;
    }
    
//    public String getPath

    public boolean mkdir() {
	if (!exists()) {
	    try {
		String absolutePath = parent.getAbsolutePath();
		@SuppressWarnings("unused")
		boolean cwd = parent.ftpClient.changeWorkingDirectory(absolutePath);
		boolean makeDirectory = parent.ftpClient.makeDirectory(name);
		parent.children = null;
		this.ftpFile = parent.getFtpFile(name);
		return makeDirectory;
	    } catch (IOException e) {
		e.printStackTrace();
		return false;
	    }
	}
	return false;
    }

    public boolean mkdirs() {
	if (!exists()) {
	    getParentFile().mkdirs();
	}
	return mkdir();
    }

    public boolean rename(String name) {
	try {
	    boolean b = ftpClient.rename(getAbsolutePath(), getParent() + "/" + name);
	    if (b) {
		parent.children = null;
		ftpFile = getFtpFile(name);
	    }
	    return b;
	} catch (IOException e) {
	    e.printStackTrace();
	    return false;
	}
    }

    public boolean rmdir(boolean rf) {
	if (isDirectory()) {
	    try {
		if (!rf) {
		    if (getChildren().length == 0) {
			return ftpClient.deleteFile(getAbsolutePath());
		    }
		    return false;
		}
		return VfsUtil.deleteDirectory(this);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	return false;
    }

    public void setDisplayName(String displayName) throws IOException {
    }

    public void setHidden(boolean hidden) throws IOException {
    }

    public void setPos(int pos) throws IOException {
    }

    public Icon getIcon() {
	return null;
    }

    public String getProtocolName() {
	return "ftp";
    }

    public void writeFile(InputStream in) throws IOException {
	ftpClient.storeFile(getAbsolutePath(), in);
    }

    public Enumeration<VFile> files() {
	return new ArrayEnumeration<VFile>(listFiles());
    }

    public Enumeration<String> names() throws IOException {
	return new ArrayEnumeration<String>(list());
    }
}
