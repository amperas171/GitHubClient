package com.amperas17.wonderstest.ui.splash;


import android.os.Handler;

import com.amperas17.wonderstest.data.model.pojo.User;
import com.amperas17.wonderstest.data.provider.IProviderCaller;
import com.amperas17.wonderstest.data.provider.UserProvider;

import java.lang.ref.WeakReference;

public class SplashPresenter implements ISplashPresenter, IProviderCaller<User> {
    private static final int DELAY = 3000;

    private WeakReference<ISplashView> viewRef;
    private UserProvider userProvider;
    private Handler handler;

    public SplashPresenter(ISplashView view) {
        this.viewRef = new WeakReference<>(view);
        this.userProvider = new UserProvider(this);
        this.handler = new Handler();
    }

    public void onCreate() {
        handler.postDelayed(checkUserRunnable, DELAY);
    }

    private void checkUser() {
        userProvider.checkIfUserExist();
    }

    @Override
    public void onProviderCallSuccess(User user) {
        if (user != null) {
            ISplashView view = viewRef.get();
            if (view != null)
                view.openUserInfoActivity(user);
        } else {
            ISplashView view = viewRef.get();
            if (view != null)
                view.openAuthActivity();
        }
    }

    @Override
    public void onProviderCallError(Throwable th) {
        ISplashView view = viewRef.get();
        if (view != null)
            view.showError(th);
    }

    @Override
    public void onDestroy() {
        userProvider.close();
        handler.removeCallbacks(checkUserRunnable);
    }

    private Runnable checkUserRunnable = new Runnable() {
        @Override
        public void run() {
            checkUser();
        }
    };
}
