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

import java.util.Calendar;

public class Comparators {

    public static enum SortType {
	ByName, ByDate, BySize
    }

    public static class ByDateComparator implements IComparator<VFile> {
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

    public static class ByNameComparator implements IComparator<VFile> {

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

    public static class BySizeComparator implements IComparator<VFile> {

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
