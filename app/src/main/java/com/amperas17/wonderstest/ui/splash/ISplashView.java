package com.amperas17.wonderstest.ui.splash;


import com.amperas17.wonderstest.data.model.pojo.User;

public interface ISplashView {
    void showError(Throwable th);
    void openAuthActivity();
    void openUserInfoActivity(User user);
}
