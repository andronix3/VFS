package com.smartg.java.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Enumeration;

import javax.swing.Icon;

/**
 * @author Andrey Kuznetsov
 * The most important design changes (compared to File) is that
 * we let VFile object implementation to decide which type to use, 
 * instead of using fixed types: 
 * 1. Creating of children with get(String name) instead of File(String, String)
 * 2. Creating of InputStream with getInputStream() instead of new FileInputStream(File)
 * 3. Creating of OutputStream with getOutputStream() instead of new FileOutputStream(File)
 */
public interface VFile {
    String getName();
    String getDisplayName();
    void setDisplayName(String displayName) throws IOException;
    String getParent();
    VFile getParentFile();
    String getAbsolutePath();
    boolean isDirectory();
    boolean isHidden();
    void setHidden(boolean hidden) throws IOException;
    String [] list();
    VFile [] listFiles();
    String [] list(VFilenameFilter filter);
    VFile [] listFiles(VFilenameFilter filter);
    Enumeration<String> names() throws IOException;
    Enumeration<VFile> files();
    VFile get(String s);
    boolean exists();
    int getPos();
    void setPos(int pos) throws IOException;
    void create() throws IOException;
    boolean rename(String name);
    boolean delete();
    boolean mkdir();
    boolean mkdirs();
    boolean rmdir(boolean rf);
    long lastModified();
    long length();
    public Icon getIcon();
    public String getProtocolName();
    public void writeFile(InputStream in) throws IOException;
    public Path toPath();

    InputStream getInputStream() throws IOException;
    OutputStream getOutputStream() throws IOException;
}
