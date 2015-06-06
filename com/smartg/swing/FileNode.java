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
package com.smartg.swing;

import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import com.smartg.java.vfs.VFile;
import com.smartg.java.vfs.VFilenameFilter;

/**
 * FileNode - create tree view of file system. Creation of children is deferred.
 * 
 * @author Andrey Kuznetsov
 */
public class FileNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 4757431690605972444L;
    public final VFile file;
    private NodeUpdater nodeUpdater;

    VFilenameFilter filter;

    Icon icon;
    boolean needUpdate = true;

    public FileNode(VFile f) {
	this(f, null);
    }

    public FileNode(VFile f, VFilenameFilter filter) {
	super(f, f.isDirectory());
	this.file = f;
	this.filter = filter;
	if (f.isDirectory()) {
	    needUpdate = true;
	}
    }

    public void setAllowsChildren(boolean allows) {
    }

    public void removeFromParent() {
    }

    public int indexOf(VFile f) {
	int childCount = getChildCount();
	for (int i = 0; i < childCount; i++) {
	    FileNode node = (FileNode) getChildAt(i);
	    if (f.equals(node.file)) {
		return i;
	    }
	}
	return -1;
    }

    public boolean isLeaf() {
	return !file.isDirectory();
    }

    public boolean add(final VFile f) {
	add(new FileNode(f));
	return true;
    }

    public boolean isNeedUpdate() {
	return needUpdate;
    }

    public void invalidate() {
	if (file.isDirectory()) {
	    this.needUpdate = true;
	    nodeUpdater = null;
	}
    }

    public Icon getIcon() {
	if (icon == null) {
	    icon = file.getIcon();
	}
	return icon;
    }

    public void update() {
	getNodeUpdater();
	while(nodeUpdater.hasMoreElements()) {
	    add(new FileNode(nodeUpdater.nextElement()));
	}
	needUpdate = false;
    }

    public void setIcon(Icon icon) {
	this.icon = icon;
    }

    public void setNeedUpdate(boolean needUpdate) {
	this.needUpdate = needUpdate;
    }

    public Enumeration<VFile> getNodeUpdater() {
	if (needUpdate) {
	    if (nodeUpdater == null) {
		nodeUpdater = new NodeUpdater();
	    }
	}
	return nodeUpdater;
    }

    public VFilenameFilter getFilter() {
	return filter;
    }

    public void setFilter(VFilenameFilter filter) {
	this.filter = filter;
    }

    @Override
    public boolean getAllowsChildren() {
	return file.isDirectory();
    }

    class NodeUpdater implements Enumeration<VFile> {
	Enumeration<VFile> files;

	public boolean hasMoreElements() {
	    if (!needUpdate) {
		return false;
	    }
	    if (!file.isDirectory()) {
		return false;
	    }
	    if (files == null) {
		files = file.files();
	    }
	    return files.hasMoreElements();
	}

	public VFile nextElement() {
	    if (!needUpdate) {
		return null;
	    }
	    if (!file.isDirectory()) {
		return null;
	    }

	    if (files == null) {
		files = file.files();
	    }
	    return files.nextElement();
	}
    }
}
