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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import com.imagero.uio.io.REO_FileInputStream;
import com.smartg.java.util.EmptyEnumeration;

/**
 * VFile wrapper for java.io.File.
 * 
 * @author Andrey Kuznetsov
 */
public class JFile implements VFile {

    static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();

    static enum FType {
	UNKNOWN, FILE, DIRECTORY
    }

    String displayName;
    int pos;

    protected File f;
    private FType type = FType.UNKNOWN;

    public JFile(File f) {
	this.f = f;
    }

    public JFile(VFile f, String s) {
	this(f.getAbsolutePath(), s);
    }

    public JFile(String pathname) {
	f = new File(pathname);
    }

    public JFile(String parent, String child) {
	f = new File(parent, child);
    }

    public JFile(File parent, String child) {
	f = new File(parent, child);
    }

    public boolean isDirectory() {
	if (type == FType.UNKNOWN) {
	    type = FType.FILE;
	    if (f.isDirectory()) {
		type = FType.DIRECTORY;
	    }
	    FileSystemView fsv = fileSystemView;
	    if (fsv.isDrive(f) || fsv.isDrive(f) || fsv.isFileSystemRoot(f) || fsv.isRoot(f) || !fsv.isFileSystem(f)) {
		type = FType.DIRECTORY;
	    }
	}
	return type == FType.DIRECTORY;
    }

    public boolean isHidden() {
	return f.isHidden();
    }

    public void setDisplayName(String displayName) throws IOException {
	this.displayName = displayName;
    }

    public void setHidden(boolean hidden) throws IOException {
    }

    public void setPos(int pos) throws IOException {
	this.pos = pos;
    }

    public InputStream getInputStream() throws IOException {
	return new REO_FileInputStream(f);
    }

    public OutputStream getOutputStream() throws IOException {
	return new FileOutputStream(f);
    }

    public VFile getParentFile() {
	if (f != null) {
	    File parentFile = f.getParentFile();
	    if (parentFile != null) {
		return new JFile(parentFile);
	    }
	    return null;
	}
	return null;
    }

    public VFile get(String s) {
	if (isDirectory()) {
	    JFile jFile = new JFile(f, s);
	    s = s.toUpperCase();
	    if (s.endsWith(".ZIP") || s.endsWith(".JAR")) {
		return new VZipFile(jFile);
	    }
	    return jFile;
	}
	return null;
    }

    public boolean exists() {
	return f.exists();
    }

    public int getPos() {
	return pos;
    }

    public String getDisplayName() {
	if (displayName != null) {
	    return displayName;
	}
	String systemDisplayName = fileSystemView.getSystemDisplayName(f);
	if (systemDisplayName == null || systemDisplayName.isEmpty()) {
	    return getAbsolutePath();
	}
	displayName = systemDisplayName;
	return systemDisplayName;
    }

    public JFile(URI uri) {
	f = new File(uri);
    }

    public String getName() {
	return f.getName();
    }

    public String getParent() {
	return f.getParent();
    }

    String absolutePath;

    public String getAbsolutePath() {
	if (absolutePath != null) {
	    return absolutePath;
	}
	if (fileSystemView.isDrive(f)) {
	    return f.getPath();
	}
	VFile parentFile = getParentFile();
	if (parentFile != null) {
	    absolutePath = parentFile.getAbsolutePath();
	    if (absolutePath.charAt(absolutePath.length() - 1) != File.separatorChar) {
		absolutePath += File.separator;
	    }
	    absolutePath += getDisplayName();
	    return absolutePath;
	}
	absolutePath = getDisplayName();
	return absolutePath;
    }

    public String[] list() {
	File[] list = fsvList();
	String[] s0 = new String[list.length];
	for (int i = 0; i < list.length; i++) {
	    s0[i] = list[i].getName();
	}
	return s0;
    }

    private File[] fsvList() {
	File[] list = new File[0];
	try {
	    // System.out.println(f);
	    list = fileSystemView.getFiles(f, false);
	} catch (Throwable ex) {
	    // ignore
	}
	return list;
    }

    public VFile[] listFiles() {
	File[] list = fsvList();
	return createFiles(list);
    }

    private VFile[] createFiles(File[] list) {
	if (list == null) {
	    return new VFile[0];
	}
	VFile[] files = new VFile[list.length];
	for (int i = 0; i < list.length; i++) {
	    VFile jFile = new JFile(list[i]);

	    String s = jFile.getName().toUpperCase();
	    if (s.endsWith(".ZIP") || s.endsWith(".JAR")) {
		jFile = new VZipFile(jFile);
	    }
	    files[i] = jFile;
	}
	return files;
    }

    private VFile[] createFiles(String[] list) {
	if (list == null) {
	    return new VFile[0];
	}
	VFile[] files = new VFile[list.length];
	for (int i = 0; i < files.length; i++) {
	    files[i] = get(list[i]);
	}
	return files;
    }

    public VFile[] listFiles(VFilenameFilter filter) {
	String[] list = list(filter);
	return createFiles(list);
    }

    private String[] filter(VFilenameFilter filter, File[] files) {
	ArrayList<String> v = new ArrayList<String>();
	for (int i = 0; i < files.length; i++) {
	    if (filter.accept(this, files[i].getName())) {
		v.add(files[i].getName());
	    }
	}
	String[] res = new String[v.size()];
	v.toArray(res);
	return res;
    }

    public String[] list(VFilenameFilter filter) {
	if (filter == null) {
	    return list();
	}
	File[] list = fsvList();
	return filter(filter, list);
    }

    public String toString() {
	return getDisplayName();
    }

    public int hashCode() {
	if (f == null) {
	    return 0;
	}
	return f.hashCode();
    }

    public boolean equals(Object obj) {
	if (obj instanceof JFile) {
	    JFile jf = (JFile) obj;
	    return jf.f.equals(f);
	}
	return false;
    }

    public void create() throws IOException {
	f.createNewFile();
    }

    public boolean rename(String name) {
	File dest = new File(f.getParentFile(), name);
	return f.renameTo(dest);
    }

    public boolean delete() {
	if (!f.exists()) {
	    return false;
	}

	if (!isDirectory()) {
	    boolean b = f.delete();

	    return b;
	}
	return false;
    }

    public boolean mkdir() {
	return f.mkdir();
    }

    public boolean mkdirs() {
	return f.mkdirs();
    }

    public boolean rmdir(boolean rf) {
	if (f.isDirectory()) {
	    if (rf) {
		return VfsUtil.deleteDirectory(this);
	    }
	    String[] list = f.list();
	    if (list == null || list.length == 0) {
		return f.delete();
	    }
	}
	return false;
    }

    public long lastModified() {
	return f.lastModified();
    }

    public long length() {
	if (f.isDirectory()) {
	    return 0;
	}
	return f.length();
    }

    public Icon getIcon() {
	return fileSystemView.getSystemIcon(f);
    }

    public String getProtocolName() {
	return "file";
    }

    public void writeFile(InputStream in) throws IOException {
	byte[] buf = new byte[2048];
	int read = 0;
	OutputStream out = null;
	try {
	    out = getOutputStream();
	    while ((read = in.read(buf)) > 0) {
		out.write(buf, 0, read);
	    }
	} finally {
	    VfsUtil.close(out);
	    // VfsUtil.close(in);
	}
    }

    public Enumeration<VFile> files() {
	DirectoryStream<Path> directoryStream = null;
	try {
	    directoryStream = Files.newDirectoryStream(f.toPath());
	    return new FilesEnumeration(directoryStream.iterator());
	} catch (IOException ex) {
	    // ex.printStackTrace();
	}
	return new EmptyEnumeration<VFile>();
    }

    public Enumeration<String> names() {
	DirectoryStream<Path> directoryStream = null;
	try {
	    directoryStream = Files.newDirectoryStream(f.toPath());
	    return new NamesEnumeration(directoryStream.iterator());
	} catch (IOException ex) {
	    // ex.printStackTrace();
	}
	return new EmptyEnumeration<String>();
    }

    static class FilesEnumeration implements Enumeration<VFile> {
	Iterator<Path> iterator;

	public FilesEnumeration(Iterator<Path> iterator) {
	    this.iterator = iterator;
	}

	public boolean hasMoreElements() {
	    return iterator.hasNext();
	}

	public VFile nextElement() {
	    Path next = iterator.next();
	    File f = next.toFile();
	    return new JFile(f);
	}
    }

    static class NamesEnumeration implements Enumeration<String> {
	Iterator<Path> iterator;

	public NamesEnumeration(Iterator<Path> iterator) {
	    this.iterator = iterator;
	}

	public boolean hasMoreElements() {
	    return iterator.hasNext();
	}

	public String nextElement() {
	    Path next = iterator.next();
	    return next.getFileName().toString();
	}
    }

    public Path toPath() {
	return f.toPath();
    }
}
