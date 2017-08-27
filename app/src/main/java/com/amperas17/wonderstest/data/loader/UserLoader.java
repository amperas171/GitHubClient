package com.amperas17.wonderstest.data.loader;


import com.amperas17.wonderstest.App;
import com.amperas17.wonderstest.data.model.pojo.User;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoader {

    private WeakReference<IUserLoaderCaller> callerRef;
    private Call<User> call;

    public UserLoader(IUserLoaderCaller authCaller) {
        this.callerRef = new WeakReference<>(authCaller);
    }

    public void getData(final String authHeader) {
        call = App.getGitHubApi().getUser(authHeader);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (callerRef.get() != null)
                    callerRef.get().onLoadUser(response.body(), authHeader);
            }

            @Override
            public void onFailure(Call<User> call, Throwable th) {
                if (!call.isCanceled()) {
                    if (callerRef.get() != null)
                        callerRef.get().onLoadUserError(th);
                }
            }
        });
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
            call = null;
        }
    }

    public interface IUserLoaderCaller {
        void onLoadUser(User user, String authHeader);

        void onLoadUserError(Throwable th);
    }
}
