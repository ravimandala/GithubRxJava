package com.codepath.githubrxjava;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class GitHubClient {

    private static final String GITHUB_BASE_URL = "https://api.github.com/";

    Map<String, Observable<List<GitHubRepo>>> starredRepos = new HashMap<>();

    private static GitHubClient instance;
    private GitHubService gitHubService;

    private GitHubClient() {
        final Gson gson =
            new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(GITHUB_BASE_URL)
                                                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                                        .addConverterFactory(GsonConverterFactory.create(gson))
                                                        .build();
        gitHubService = retrofit.create(GitHubService.class);
    }

    public static GitHubClient getInstance() {
        if (instance == null) {
            instance = new GitHubClient();
        }
        return instance;
    }

    public Observable<List<GitHubRepo>> getStarredRepos(Context context, @NonNull String userName) {
        if (starredRepos.containsKey(userName) == false) {
            Toast.makeText(context, "Fetching Observable for username: " + userName, Toast.LENGTH_SHORT).show();
            starredRepos.put(userName, gitHubService.getStarredRepositories(userName));
        } else {
            Toast.makeText(context, "Got the cached Observable", Toast.LENGTH_SHORT).show();
        }
        return starredRepos.get(userName);
    }
}