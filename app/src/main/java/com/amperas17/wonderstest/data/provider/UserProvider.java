package com.amperas17.wonderstest.data.provider;


import android.util.Base64;

import com.amperas17.wonderstest.data.cache.UserCache;
import com.amperas17.wonderstest.data.loader.UserLoader;
import com.amperas17.wonderstest.data.model.pojo.User;

import java.lang.ref.WeakReference;

public class UserProvider implements UserLoader.IUserLoaderCaller, UserCache.ICachedUserCaller {

    private WeakReference<IProviderCaller> callerRef;
    private UserCache userCache;
    private UserLoader userLoader;

    public UserProvider(IProviderCaller caller) {
        callerRef = new WeakReference<>(caller);
        userCache = new UserCache(this);
        userLoader = new UserLoader(this);
    }

    public void checkIfUserExist() {
        userCache.getUser();
    }

    public void getUser(String login, String password) {
        userLoader.getData(getAuthHeader(login, password));
    }

    @Override
    public void onLoadUser(User user, String authHeader) {
        user.setAuthHeader(authHeader);
        if (callerRef.get() != null) {
            callerRef.get().onProviderCallSuccess(user);
        }
        userCache.setUser(user);
    }

    @Override
    public void onLoadUserError(Throwable th) {
        if (callerRef.get() != null) {
            callerRef.get().onProviderCallError(th);
        }
    }

    @Override
    public void onGetUser(User user) {
        if (callerRef.get() != null) {
            callerRef.get().onProviderCallSuccess(user);
        }
    }

    private String getAuthHeader(String login, String password) {
        return "Basic " + Base64.encodeToString((login + ":" + password).getBytes(), Base64.DEFAULT).replace("\n", "");
    }

    public void close() {
        userCache.close();
        userLoader.cancel();
    }

    public interface IProviderCaller {
        void onProviderCallSuccess(User user);

        void onProviderCallError(Throwable th);
    }
}
