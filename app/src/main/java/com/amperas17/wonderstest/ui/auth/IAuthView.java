package com.amperas17.wonderstest.ui.auth;

import com.amperas17.wonderstest.data.model.pojo.User;

public interface IAuthView {
    void showLoginError();

    void showPasswordError();

    void showLoader();

    void hideLoader();

    void showAuthError(Throwable th);

    void openRepositoriesActivity(User user);
}
