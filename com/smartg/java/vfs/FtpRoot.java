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
