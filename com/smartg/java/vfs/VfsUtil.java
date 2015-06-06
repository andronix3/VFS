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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

public class VfsUtil {

    /**
     * Read file and return it content as String
     * 
     * @param f
     *            VFile
     * @return String
     * @throws IOException
     */
    public static String readFile(VFile f) throws IOException {
	StringBuffer sb = new StringBuffer();
	UTF16_DetectInputStream udis = new UTF16_DetectInputStream(f.getInputStream());
	if (udis.isUTF) {
	    byte[] b = new byte[(int) Math.min(Integer.MAX_VALUE, f.length())];
	    readFully(udis, b, 0, b.length);
	    udis.close();
	    return new String(b, "UTF-16");
	}
	BufferedReader reader = new BufferedReader(new InputStreamReader(f.getInputStream()));
	try {
	    readFile(reader, sb);
	} finally {
	    reader.close();
	    udis.close();
	}
	return sb.toString();
    }

    static void readFully(InputStream in, byte b[], int off, int len) throws EOFException, IOException {
	int n = 0;
	do {
	    int count = in.read(b, off + n, len - n);
	    if (count <= 0) {
		throw new EOFException("" + (n > 0 ? n : 0));
	    }
	    n += count;
	} while (n < len);
    }

    static class UTF16_DetectInputStream extends FilterInputStream {

	byte[] buffer = new byte[1024];
	int length;
	int fp;
	public final boolean isUTF;
	boolean swapBytes;

	protected UTF16_DetectInputStream(InputStream in) throws IOException {
	    super(in);
	    length = in.read(buffer);
	    isUTF = ((((buffer[0] & 0xFF) == 0xFF) && ((buffer[1] & 0xFF) == 0xFE)) || (((buffer[0] & 0xFF) == 0xFE) && ((buffer[1] & 0xFF) == 0xFF)));
	}

	public int read() throws IOException {
	    if (fp >= length) {
		fill();
	    }
	    if (length < 0) {
		return -1;
	    }
	    return buffer[fp++] & 0xFF;
	}

	public int read(byte[] b, int off, int len) throws IOException {
	    if (fp >= length) {
		fill();
	    }
	    if (length < 0) {
		return -1;
	    }
	    int count = Math.min(len, length - fp);
	    System.arraycopy(buffer, fp, b, off, count);

	    fp += count;
	    return count;
	}

	private void fill() throws IOException {
	    fp = 0;
	    length = in.read(buffer);
	}
    }

    /**
     * Load contents of file in StringBuffer
     * 
     * @param f
     *            VFile
     * @return StringBuffer
     * @throws IOException
     */
    public static StringBuffer readFileSB(VFile f) throws IOException {
	BufferedReader reader = new BufferedReader(new InputStreamReader(f.getInputStream()));
	StringBuffer sb = new StringBuffer();
	try {
	    readFile(reader, sb);
	} finally {
	    reader.close();
	}
	return sb;
    }

    /**
     * Load contents of file in supplied StringBuffer
     * 
     * @param reader
     * @param sb
     *            StringBuffer
     * @throws IOException
     */
    public static void readFile(BufferedReader reader, StringBuffer sb) throws IOException {
	String line = null;
	do {
	    line = reader.readLine();
	    if (line != null) {
		sb.append(line);
		sb.append("\n");
	    }
	} while (line != null);
    }

    /**
     * Save String to VFile
     * 
     * @param s
     *            String
     * @param f
     *            VFile
     * @throws IOException
     */
    public static void writeFile(String s, VFile f) throws IOException {
	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(f.getOutputStream()));
	writer.write(s);
	writer.close();
    }

    public static boolean deleteDirectory(VFile dir) {
	if (dir.isDirectory()) {
	    VFile[] children = dir.listFiles();
	    for (int i = 0; i < children.length; i++) {
		if (children[i].isDirectory()) {
		    deleteDirectory(dir.get(children[i].getName()));
		} else {
		    children[i].delete();
		}
	    }
	}
	return dir.rmdir(false);
    }

    public static void copyDirectory(VFile src, VFile dst) {
	dst.mkdirs();
	VFile[] list = src.listFiles();

	for (VFile f : list) {
	    VFile fd = dst.get(f.getName());
	    if (f.isDirectory()) {
		copyDirectory(f, fd);
	    } else {
		copyFile(f, fd);
	    }
	}
    }

    public static boolean copyFile(VFile src, VFile dst) {
	if (src == null || dst == null) {
	    return false;
	}
	Logger l = Logger.getLogger("com.imagero.java.vfs");
	l.info("Copy file: " + src.getName() + " from " + src.getParent() + " to " + dst.getParent());
	try {
	    dst.writeFile(src.getInputStream());
	    return true;
	} catch (Throwable t) {
	    t.printStackTrace();
	}
	return false;
    }

    static void close(InputStream in) {
	if (in == null) {
	    return;
	}
	try {
	    in.close();
	} catch (IOException ex) {

	}
    }

    static void close(OutputStream out) {
	if (out == null) {
	    return;
	}
	try {
	    out.close();
	} catch (IOException ex) {

	}
    }

    public static String getProtocol(VFile file, String p) {
	VFile parentFile = file.getParentFile();
	String protocolName = file.getProtocolName();
	if (parentFile == null) {
	    return protocolName + ":///" + p;
	}
	if (!parentFile.getProtocolName().equals(protocolName)) {
	    return getProtocol(parentFile, protocolName + ":///" + p);
	}
	return getProtocol(parentFile, p);
    }
}
