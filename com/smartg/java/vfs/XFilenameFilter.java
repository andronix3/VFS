package com.smartg.java.vfs;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Andrey Kuznetsov
 */
class XFilenameFilter implements FilenameFilter {
    VFilenameFilter filter;

    public XFilenameFilter(VFilenameFilter filter) {
        this.filter = filter;
    }

    public boolean accept(File dir, String name) {
        return filter.accept(new JFile(dir), name);
    }
}
