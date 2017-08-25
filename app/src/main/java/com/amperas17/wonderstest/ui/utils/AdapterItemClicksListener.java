package com.amperas17.wonderstest.ui.utils;


public interface AdapterItemClicksListener<T> {
    void onItemClick(T item);
    void onItemLongClick(T item);
}