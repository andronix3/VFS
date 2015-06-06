package com.imagero.java.io;

import com.imagero.java.vfs.VFile;

/**
 * Same as FileNode, but leafs are ignored (not added as children)
 * 
 * @author andrey
 * 
 */
public class DirectoryNode extends FileNode {

    private static final long serialVersionUID = 2863348667183642516L;
    private String displayName;

    public DirectoryNode(VFile f) {
	super(f);
    }

    public String toString() {
	if (displayName == null || displayName.isEmpty()) {
	    displayName = file.toString();
	}
	return displayName;
    }

    @Override
    public boolean add(VFile f) {
	if (f.isDirectory()) {
	    add(new DirectoryNode(f));
	    return true;
	}
	return false;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    public String getDisplayName() {
	return displayName;
    }
}
