package com.jimmy.data.remote;

import com.jimmy.data.remote.api.SyncApi;

public class ApiManager {

    private static ApiManager instance;
    private final ApiClient apiClient;

    private ApiManager() {
        apiClient = new ApiClient();
    }

    public static ApiManager getInstance() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }

    public SyncApi getSyncApi() {
        return apiClient.createService(SyncApi.class);
    }
}

