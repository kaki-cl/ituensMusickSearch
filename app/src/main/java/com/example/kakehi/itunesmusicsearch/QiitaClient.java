package com.example.kakehi.itunesmusicsearch;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by atuski on 2018/03/01.
 */

public class QiitaClient {

    public interface QiitaService {
        @GET("/api/v2/items")
        Call<List<Repo>> listRepos(@Query("query") String query);
    }
}
