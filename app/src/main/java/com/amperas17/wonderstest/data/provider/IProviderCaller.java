package com.amperas17.wonderstest.data.provider;


public interface IProviderCaller<T> {
    void onProviderCallSuccess(T t);
    void onProviderCallError(Throwable th);
}
