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
package com.smartg.java.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import com.imagero.java.util.Debug;
import com.smartg.java.vfs.JFile;
import com.smartg.java.vfs.VFile;
import com.smartg.java.vfs.VFilenameFilter;

/**
 * FileProcessor allows to process all files in given source directory and
 * (optional) save result(s) in destination directory. Abstract method
 * StringBuffer process(File f) should be implemented.
 * 
 * The result is only saved if destination directory and returned StringBuffer
 * are both not null.
 * 
 * There are two ways of processing - single-threaded and multi-threaded.
 * 
 * For single-threaded processing pass FileProcesor as argument to Thread
 * constructor or add it as task to ThreadManager.
 * 
 * For multi-threaded processing do following: FileProcessor fp; ThreadManager
 * tm = new ThreadManager(<numberOfThreads>); tm.add(fp.getJobs());
 * 
 * It is possible to abort or break processing. If processing was aborted it
 * can't be continued. If processing was breaked it can be continued just by
 * passing processor to Thread.
 * 
 * In multi-threaded mode use ThreadManager to pause/abort processing.
 * 
 * @author Andrey Kuznetsov
 */
public abstract class FileProcessor implements Runnable, FileVisitor<Path> {

    final VFile root;
    final VFile dstD;
    VFilenameFilter filter;
    int maxDepth = Integer.MAX_VALUE;

    private volatile boolean aborted;

    /**
     * create new FileProcessor
     * 
     * @param srcD
     *            source directory
     * @param dstD
     *            destination directory
     */
    public FileProcessor(VFile srcD, VFile dstD) {
	this(srcD, dstD, null);
    }

    /**
     * create new FileProcessor
     * 
     * @param srcD
     *            source directory
     * @param dstD
     *            destination directory
     * @param filter
     *            filename filter (should return true for directories if
     *            recursive processing is required)
     */
    public FileProcessor(VFile srcD, VFile dstD, VFilenameFilter filter) {
	root = srcD;
	this.dstD = dstD;
	this.filter = filter;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	if (aborted) {
	    return FileVisitResult.TERMINATE;
	}
	return FileVisitResult.CONTINUE;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
	File f = path.toFile();
	JFile dir = new JFile(f.getParentFile());
	String name = f.getName();
	if(!filter.accept(dir, name)) {
	    return FileVisitResult.CONTINUE;
	}
	StringBuffer sb = process(path);
	if (dstD != null && sb != null) {
	    save(path, sb);
	}
	if (aborted) {
	    return FileVisitResult.TERMINATE;
	}
	return FileVisitResult.CONTINUE;
    }

    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
	if (aborted) {
	    return FileVisitResult.TERMINATE;
	}
	return FileVisitResult.CONTINUE;
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	if (aborted) {
	    return FileVisitResult.TERMINATE;
	}
	return FileVisitResult.CONTINUE;
    }

    public void run() {
	walk();
    }

    public void walk() {
	aborted = false;
	try {
	    Files.walkFileTree(root.toPath(), EnumSet.noneOf(FileVisitOption.class), maxDepth, this);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    public void abortJob() {
	aborted = true;
    }

    public void breakJob() {
	aborted = true;
    }

    public abstract StringBuffer process(Path f);

    private void save(Path f, final StringBuffer sb) {
	String s = sb.toString();
	save(f, s);
    }

    private void save(Path f, String s) {
	VFile dest = getDestinationFile(f);
	dest.getParentFile().mkdirs();

	BufferedWriter writer = null;
	try {
	    writer = new BufferedWriter(new OutputStreamWriter(dest.getOutputStream()));
	    writer.write(s);
	    writer.close();
	} catch (IOException ex) {
	    Debug.print(ex);
	}
    }

    protected VFile getDestinationFile(Path f) {
	int length = root.getAbsolutePath().length();
	String path = f.toAbsolutePath().toString().substring(length);
	String[] pp = path.split("/");
	VFile dest = dstD.get(pp[0]);
	for (int i = 1; i < pp.length; i++) {
	    if (pp[i].length() > 0) {
		dest = dest.get(pp[i]);
	    }
	}
	return dest;
    }
}
