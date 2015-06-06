package com.imagero.java.vfs;

import java.util.Comparator;

public interface IComparator<T> extends Comparator<T> {

    enum Order {
	Ascending, Descending
    }

    void setOrder(Order order);

    Order getOrder();
}