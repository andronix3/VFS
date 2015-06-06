package com.imagero.java.vfs;

import java.util.Calendar;

public class Comparators {

    public static enum SortType {
	ByName, ByDate, BySize
    }

    static class ByDateComparator implements IComparator<VFile> {
	Calendar c0 = Calendar.getInstance();
	Calendar c1 = Calendar.getInstance();

	private Order order = Order.Ascending;

	public IComparator.Order getOrder() {
	    return order;
	}

	public void setOrder(IComparator.Order order) {
	    this.order = order;
	}

	public int compare(VFile f0, VFile f1) {
	    int cmp = cmp(f0, f1);
	    return order == Order.Ascending ? -cmp : cmp;
	}

	private int cmp(VFile f0, VFile f1) {
	    c0.setTimeInMillis(f0.lastModified());
	    c1.setTimeInMillis(f1.lastModified());

	    if (f0.isDirectory() && f1.isDirectory()) {
		return c0.compareTo(c1);
	    } else if (!f0.isDirectory() && !f1.isDirectory()) {
		return c0.compareTo(c1);
	    } else if (!f0.isDirectory() && f1.isDirectory()) {
		return 1;
	    } else if (f0.isDirectory() && !f1.isDirectory()) {
		return -1;
	    }
	    return 0;
	}
    }

    static class ByNameComparator implements IComparator<VFile> {

	private Order order = Order.Ascending;

	public IComparator.Order getOrder() {
	    return order;
	}

	public void setOrder(IComparator.Order order) {
	    this.order = order;
	}

	public int compare(VFile f0, VFile f1) {
	    int cmp = cmp(f0, f1);
	    return order == Order.Ascending ? -cmp : cmp;
	}

	private int cmp(VFile f0, VFile f1) {
	    if (f0.isDirectory() && f1.isDirectory()) {
		return f0.getName().toUpperCase().compareTo(f1.getName().toUpperCase());
	    } else if (!f0.isDirectory() && !f1.isDirectory()) {
		return f0.getName().toUpperCase().compareTo(f1.getName().toUpperCase());
	    } else if (!f0.isDirectory() && f1.isDirectory()) {
		return 1;
	    } else if (f0.isDirectory() && !f1.isDirectory()) {
		return -1;
	    }
	    return 0;
	}
    }

    static class BySizeComparator implements IComparator<VFile> {

	private Order order = Order.Ascending;

	public IComparator.Order getOrder() {
	    return order;
	}

	public void setOrder(IComparator.Order order) {
	    this.order = order;
	}

	public int compare(VFile f0, VFile f1) {
	    int cmp = cmp(f0, f1);
	    return order == Order.Ascending ? -cmp : cmp;
	}

	private int cmp(VFile f0, VFile f1) {
	    if (f0.isDirectory() && f1.isDirectory()) {
		return f0.getName().toUpperCase().compareTo(f1.getName().toUpperCase());
	    } else if (!f0.isDirectory() && !f1.isDirectory()) {
		return (f0.length() > f1.length())? 1 : (f0.length() == f1.length())? 0 : - 1;
	    } else if (!f0.isDirectory() && f1.isDirectory()) {
		return 1;
	    } else if (f0.isDirectory() && !f1.isDirectory()) {
		return -1;
	    }
	    return 0;
	}
    }
}
