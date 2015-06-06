/**
 * 
 */
package com.smartg.java.vfs;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;


/**
 * SilentListModel offers better performance for bulk add/remove operations.
 * It is also possible to switch it to silent mode (no events are generated during silent mode).
 * Moreover it ensures that events are delivered to listeners only in EDT.
 * @author andrey
 *
 * @param <E>
 */
public class SilentListModel<E> extends AbstractListModel<E> {

    private static final long serialVersionUID = -203823341416077219L;

    private ArrayList<E> list = new ArrayList<E>();

    boolean silent;
    int i0, i1;

    public boolean isSilent() {
	return silent;
    }

    public void addElement(E o) {
	int index = list.size();
	list.add(o);
	fireIntervalAdded(this, index, index);
    }

    public void setSilent(boolean silent) {
	this.silent = silent;
//	System.out.println("silent " + silent);
	if (silent) {
	    i0 = Integer.MAX_VALUE;
	    i1 = 0;
	}
    }

    public void send() {
	if(i0 != Integer.MAX_VALUE) {
	    super.fireContentsChanged(this, i0, i1);
	}
//	System.out.println("refresh " + i0 + " " + i1);
	i0 = Integer.MAX_VALUE;
	i1 = 0;
    }

    @Override
    protected void fireContentsChanged(final Object source, final int index0, final int index1) {

	int minIndex = Math.min(index0, index1);
	int maxIndex = Math.max(index0, index1);

	if (!silent) {
	    if (SwingUtilities.isEventDispatchThread()) {
		super.fireContentsChanged(source, index0, index1);
	    } else {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
			SilentListModel.super.fireContentsChanged(source, index0, index1);
		    }
		});

	    }
	} else {
	    i0 = Math.min(minIndex, i0);
	    i1 = Math.max(maxIndex, i1);
	}
    }

    @Override
    protected void fireIntervalAdded(final Object source, final int index0, final int index1) {
	if (!silent) {
	    if (SwingUtilities.isEventDispatchThread()) {
		super.fireIntervalAdded(source, index0, index1);
	    } else {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
			SilentListModel.super.fireIntervalAdded(source, index0, index1);
		    }
		});
	    }
	} else {
	    int minIndex = Math.min(index0, index1);
	    int maxIndex = Math.max(index0, index1);

	    if (i0 > minIndex) {
		i0 = minIndex;
	    }
	    if (i1 < maxIndex) {
		i1 = maxIndex;
	    } else {
		i1 += maxIndex - minIndex + 1;
	    }
	}
    }

    @Override
    protected void fireIntervalRemoved(final Object source, final int index0, final int index1) {
	if (!silent) {
	    if (SwingUtilities.isEventDispatchThread()) {
		super.fireIntervalRemoved(source, index0, index1);
	    } else {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
			SilentListModel.super.fireIntervalRemoved(source, index0, index1);
		    }
		});
	    }
	} else {
	    if (i0 > index0) {
		i0 = index0;
	    }
	    if (i1 < index1) {
		i1 = index1;
	    } else {
		i1 -= index1 - index0 + 1;
	    }
	    if (i0 > i1) {
		i0 = i1;
	    }
	}
    }

    public E getElementAt(int index) {
	return list.get(index);
    }

    public int getSize() {
	return list.size();
    }
    
    

    public void add(int index, E element) {
	list.add(index, element);
	fireIntervalAdded(this, index, index);
    }

    /**
     * Clear this model first and than add elements from given Collection to it.
     * @param c
     */
    public void set(Collection<E> c) {
	list.clear();
	list.addAll(c);
	int newSize = list.size();

	fireContentsChanged(this, 0, newSize);
    }
    
    /**
     * Add elements from given Collection to this model
     * @param c
     */
    public void add(Collection<E> c) {
	list.addAll(c);
	int newSize = list.size();

	fireContentsChanged(this, 0, newSize);
    }
    
    /**
     * Remove all elements contained in given Collection from this model.
     * @param c Collection
     */
    public void remove(Collection<E> c) {
	list.removeAll(c);
	int newSize = list.size();

	fireContentsChanged(this, 0, newSize);
    }
    
    public void clear() {
	int size = list.size() -1;
	list.clear();
	fireIntervalRemoved(this, 0, size);
    }

    
    public <T> T[] toArray(T [] a) {
	return list.toArray(a);
    }
}