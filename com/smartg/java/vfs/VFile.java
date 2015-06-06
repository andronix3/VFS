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
