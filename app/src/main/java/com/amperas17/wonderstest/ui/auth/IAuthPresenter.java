package com.amperas17.wonderstest.ui.auth;


public interface IAuthPresenter {
    void verifyFieldsAndAuth(String login, String password);

    void cancelLoading();

    void onDestroy();
}
