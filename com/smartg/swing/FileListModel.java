package com.smartg.swing;

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import com.smartg.java.util.ThreadManager;
import com.smartg.java.vfs.Comparators.SortType;
import com.smartg.java.vfs.IComparator;
import com.smartg.java.vfs.IComparator.Order;
import com.smartg.java.vfs.VFile;
import com.smartg.java.vfs.VFilenameFilter;
import com.smartg.swing.FileList.VParentFile;

public class FileListModel extends SilentListModel<VFile> {

    private static final long serialVersionUID = -4441346545401823943L;
    VParentFile parent;
    VFilenameFilter filenameFilter;

    IComparator<VFile> comparator;
    SortType sortType = SortType.ByDate;

    Executor tm = new ThreadManager();

    ActionListener listener;

    Runnable applyChanges = new Runnable() {
	public void run() {
	    applyChanges();
	}
    };

    Runnable refresh = new Runnable() {
	public void run() {
	    refresh();
	}
    };

    public void removeActionListener(ActionListener l) {
	listener = AWTEventMulticaster.remove(listener, l);
    }

    public void addActionListener(ActionListener l) {
	listener = AWTEventMulticaster.add(listener, l);
    }

    void fireActionEvent(ActionEvent e) {
	if (listener != null) {
	    listener.actionPerformed(e);
	}
    }

    public FileListModel(IComparator<VFile> comparator) {
	this.comparator = comparator;
    }

    public VFilenameFilter getFilenameFilter() {
	return filenameFilter;
    }

    public void setFilenameFilter(VFilenameFilter filenameFilter) {
	this.filenameFilter = filenameFilter;
    }

    public IComparator<VFile> getComparator() {
	return comparator;
    }

    public void setComparator(IComparator<VFile> comparator) {
	this.comparator = comparator;
	tm.execute(applyChanges);
    }

    public void setOrder(Order order) {
	this.comparator.setOrder(order);
	tm.execute(applyChanges);
    }

    public Order getOrder() {
	return comparator.getOrder();
    }

    public VFile getParent() {
	if (parent != null) {
	    return parent.getFile();
	}
	return null;
    }

    public void setParent(final VFile parent) {
	if (parent != null) {
	    this.parent = new VParentFile(parent);
	    tm.execute(refresh);
	}
    }
    
    private void refresh() {
	if (parent != null) {
	    final VFile[] list;
	    if (filenameFilter == null) {
		list = parent.listFiles();
	    } else {
		list = parent.listFiles(filenameFilter);
	    }
	    Arrays.sort(list, comparator);
	    List<VFile> asList = Arrays.asList(list);
	    set(asList);
	}
	fireActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Update Finished"));
    }

    private void applyChanges() {
	int size = getSize();
	VFile[] list = new VFile[size - 1];
	list = toArray(list);

	Arrays.sort(list, comparator);
	List<VFile> asList = Arrays.asList(list);
	set(asList);
	fireActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Update Finished"));
    }

    @Override
    public VFile getElementAt(int index) {
	if (index == 0) {
	    return parent;
	}
	return super.getElementAt(index - 1);
    }

    @Override
    public int getSize() {
	return super.getSize() + 1;
    }

    // @Override
    // public void add(int index, VFile element) {
    // if(index > 0) {
    // super.add(index - 1, element);
    // }
    // }
}