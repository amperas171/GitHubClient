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
            IAuthView view = viewRef.get();
            if (view != null)
                view.showLoginError();
        } else if (password.isEmpty()) {
            IAuthView view = viewRef.get();
            if (view != null)
                view.showPasswordError();
        } else {
            auth(login, password);
        }
    }

    public void auth(String login, String password) {
        IAuthView view = viewRef.get();
        if (view != null)
            view.showLoader();
        userProvider.getUser(login, password);
    }

    @Override
    public void onProviderCallSuccess(User user) {
        IAuthView view = viewRef.get();
        if (view != null) {
            view.hideLoader();
            view.openRepositoriesActivity(user);
        }
    }

    @Override
    public void onProviderCallError(Throwable th) {
        IAuthView view = viewRef.get();
        if (view != null) {
            view.hideLoader();
            view.showAuthError(th);
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
