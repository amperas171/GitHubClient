package com.amperas17.wonderstest.data.provider;


import com.amperas17.wonderstest.data.cache.RepositoryCache;
import com.amperas17.wonderstest.data.loader.RepositoriesLoader;
import com.amperas17.wonderstest.data.model.pojo.Repository;
import com.amperas17.wonderstest.data.model.pojo.User;
import com.amperas17.wonderstest.data.model.realm.RealmRepository;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.realm.RealmResults;

public class RepositoriesProvider implements RepositoriesLoader.IRepositoriesLoaderCaller {

    private WeakReference<IProviderCaller> callerRef;
    private RepositoryCache repositoryCache;
    private RepositoriesLoader repositoriesLoader;

    private String userLogin;

    public RepositoriesProvider(IProviderCaller caller) {
        callerRef = new WeakReference<>(caller);
        repositoryCache = new RepositoryCache();
        repositoriesLoader = new RepositoriesLoader(this);
    }

    public void getRepositoriesAndUpdate(String authHeader, String login) {
        getCachedRepositories(login);
        repositoriesLoader.getData(authHeader, login);
        userLogin = login;
    }

    public void getRepositories(String login) {
        getCachedRepositories(login);
    }

    @Override
    public void onLoadRepositories(ArrayList<Repository> repositories) {
        if (repositories != null && !repositories.isEmpty()) {
            repositoryCache.setRepositories(repositories);
        }
        getCachedRepositories(userLogin);
    }

    @Override
    public void onLoadRepositoriesError(Throwable th) {
        if (callerRef.get() != null) {
            callerRef.get().onProviderCallError(th);
        }
    }

    private void getCachedRepositories(String login) {
        RealmResults<RealmRepository> repositories = repositoryCache.getRepositories(login);
        if (repositories != null && callerRef.get() != null) {
            callerRef.get().onProviderCallSuccess(repositories);
        }
    }

    public void close() {
        repositoryCache.close();
        repositoriesLoader.cancel();
    }

    public interface IProviderCaller {
        void onProviderCallSuccess(RealmResults<RealmRepository> repositories);

        void onProviderCallError(Throwable th);
    }
}
