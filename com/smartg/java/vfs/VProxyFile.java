package com.smartg.java.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Enumeration;

import javax.swing.Icon;

/**
 * VProxyFile just redirects all requests to supplied VFile
 * 
 * @author andrey
 * 
 */
public abstract class VProxyFile implements VFile {


    private final VFile file;

    public VFile getFile() {
        return file;
    }

    public Enumeration<VFile> files() {
	return file.files();
    }

    public Enumeration<String> names() throws IOException {
	return file.names();
    }

    protected VProxyFile(VFile file) {
	if (file == null) {
	    throw new NullPointerException();
	}
	this.file = file;
    }

    public void writeFile(InputStream in) throws IOException {
	file.writeFile(in);
    }
    
    public VFile get(String s) {
	return file.get(s);
    }

    public String getAbsolutePath() {
	return file.getAbsolutePath();
    }

    public String getDisplayName() {
	return file.getDisplayName();
    }

    public String getName() {
	return file.getName();
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
	return file.isDirectory();
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
	return file.rmdir(rf);
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

    public Icon getIcon() {
	return file.getIcon();
    }

    public String getProtocolName() {
	return file.getProtocolName();
    }

    public void create() throws IOException {
	file.create();
    }

    public boolean delete() {
	return file.delete();
    }

    public boolean exists() {
	return file.exists();
    }

    public InputStream getInputStream() throws IOException {
	return file.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
	return file.getOutputStream();
    }

    public String[] list() {
	return file.list();
    }

    public String[] list(VFilenameFilter filter) {
	return file.list(filter);
    }

    public VFile[] listFiles() {
	return file.listFiles();
    }

    public VFile[] listFiles(VFilenameFilter filter) {
	return file.listFiles(filter);
    }

    public Path toPath() {
	return file.toPath();
    }
}