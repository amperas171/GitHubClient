package com.amperas17.wonderstest.data.provider;


import com.amperas17.wonderstest.App;
import com.amperas17.wonderstest.model.pojo.User;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthProvider {

    private WeakReference<IAuthCaller> callerRef;
    private Call<User> call;

    public AuthProvider(IAuthCaller authCaller) {
        this.callerRef = new WeakReference<>(authCaller);
    }

    public void getData(final String authHeader) {
        call = App.getGitHubApi().getUser(authHeader);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (callerRef.get() != null)
                    callerRef.get().onGetAuth(response.body(), authHeader);
            }

            @Override
            public void onFailure(Call<User> call, Throwable th) {
                if (!call.isCanceled()) {
                    if (callerRef.get() != null)
                        callerRef.get().onError(th);
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

    public interface IAuthCaller {
        void onGetAuth(User user, String authHeader);

        void onError(Throwable th);
    }
}
