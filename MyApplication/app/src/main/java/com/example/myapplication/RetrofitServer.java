package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RetrofitServer
{
    @GET
    Call<TokenResponse> getTokenResponse(@Url String url);


    @GET
    Call<UserResponse> getUserInfo(@Url String url, @Header("Authorization") String authorization);

    @POST
    Call<UserResponse> postUserPretty(@Url String url, @Header("Authorization") String authorization,
                                      @Header("Content-Type") String content,
                                      @Body SetUserPrettyNameRequest request);
}
