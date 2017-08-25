package com.amperas17.wonderstest.ui;


public interface AdapterItemClickListener<T> {
    void onItemClick(T item);
    void onItemLongClick(T item);
}