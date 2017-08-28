package com.amperas17.wonderstest.ui.auth;

import com.amperas17.wonderstest.data.model.pojo.User;
import com.amperas17.wonderstest.data.provider.IProviderCaller;
import com.amperas17.wonderstest.data.provider.UserProvider;

import java.lang.ref.WeakReference;

public class AuthPresenter implements IAuthPresenter, IProviderCaller<User> {

    private WeakReference<IAuthView> viewRef;
    private UserProvider userProvider;

    public AuthPresenter(IAuthView view) {
        this.viewRef = new WeakReference<>(view);
        this.userProvider = new UserProvider(this);
    }

    @Override
    public void verifyFieldsAndAuth(String login, String password) {
        if (login.isEmpty()) {
            if (viewRef.get() != null)
                viewRef.get().showLoginError();
        } else if (password.isEmpty()) {
            if (viewRef.get() != null)
                viewRef.get().showPasswordError();
        } else {
            auth(login, password);
        }
    }

    public void auth(String login, String password) {
        if (viewRef.get() != null)
            viewRef.get().showLoader();
        userProvider.getUser(login, password);
    }

    @Override
    public void onProviderCallSuccess(User user) {
        if (viewRef.get() != null) {
            viewRef.get().hideLoader();
            viewRef.get().openRepositoriesActivity(user);
        }
    }

    @Override
    public void onProviderCallError(Throwable th) {
        if (viewRef.get() != null) {
            viewRef.get().hideLoader();
            viewRef.get().showAuthError(th);
        }
    }

    @Override
    public void cancelLoading() {
        userProvider.close();
    }

    @Override
    public void onDestroy() {
        userProvider.close();
    }
}
