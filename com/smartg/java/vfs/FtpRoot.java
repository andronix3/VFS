package com.smartg.java.vfs;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

/**
 * FtpRoot. Same as FtpFile, but ...
 * 
 * <pre>
 * 		isDirectory() returns true
 * 		lastModified() returns 0 
 * 		length() returns 0 
 * 		rename() returns false
 * 		getName() returns "/";
 * </pre>
 * 
 * @author andrey
 * 
 */
class FtpRoot extends FtpFile {

    protected String path;
    boolean exists;

    public FtpRoot(FTPClient ftpClient, String path) {
	super(ftpClient, path);
	try {
	    exists = ftpClient.changeWorkingDirectory(path);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }
    
    public VFile resolve(String path) {
	String[] split = path.split("/");
	VFile ftp = this;
	for(String s: split) {
	    if(s.length() > 0) {
		ftp = ftp.get(s);
	    }
	}
	return ftp;
    }

    public boolean exists() {
	return true;
    }

    public String getName() {
	return "/";
    }

    public boolean isDirectory() {
	return true;
    }

    public long lastModified() {
	return 0;
    }

    public long length() {
	return 0;
    }

    public boolean rename(String name) {
	return false;
    }
}
