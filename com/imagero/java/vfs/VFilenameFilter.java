package com.imagero.java.vfs;

/**
 * Filename filter for VFile(s)
 * @author Andrey Kuznetsov
 */
public interface VFilenameFilter {
    public boolean accept(VFile dir, String name);
}
