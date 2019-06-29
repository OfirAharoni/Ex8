package com.example.myapplication;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.bumptech.glide.Glide;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapplication.MainActivity.imageView;

public class ConnectionWorker extends Worker {
    public static final String CONTENT = "application/json";
    public static final String POST_PRETTY_URL = "http://hujipostpc2019.pythonanywhere.com/user/edit/";
    private final String SERVER_BASE_URL = "http://hujipostpc2019.pythonanywhere.com/users/";
    private final String SERVER_USER_INFO = "http://hujipostpc2019.pythonanywhere.com/user/";
    private final String SERVER_USER_IMAGE = "http://hujipostpc2019.pythonanywhere.com";

    public ConnectionWorker(@NonNull Context context, @NonNull WorkerParameters params)
    {
        super(context, params);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Result doWork() {
        int request_id = getInputData().getInt("request_id", -1);
        if (request_id == 0)
        // 0 - means request for token
        {
            String token_url = SERVER_BASE_URL + MainActivity.user.getUsername() + "/token/";
            get_token(token_url);

        }

        else if (request_id == 1)
        // 1 - means post for pretty
        {
            post_user_pretty(POST_PRETTY_URL);
        }
        // Indicate whether the task finished successfully with the Result
        return Result.success();
    }


    /**
     *  This function will perform a GET request to the server while using the user
     *  username in order to receive a token from the server.
     */
    private void get_token(String url)
    {
        // Build the request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    RetrofitServer retrofitServer = retrofit.create(RetrofitServer.class);

    Call<TokenResponse> call = retrofitServer.getTokenResponse(url);

    call.enqueue(new Callback<TokenResponse>() {
        @Override
        public void onResponse(Call<TokenResponse> call, retrofit2.Response<TokenResponse> response) {
            if (!response.isSuccessful())
            {
                // Could be 404 error...
                Log.i("onResponse", Integer.toString(response.code()));
            }
            else
            {
                if (response.body() != null) {
                    String token = response.body().data;
                    MainActivity.user.setToken(token);
                    Log.i("onResponse", token);

                    get_user_info(SERVER_USER_INFO);
                }
            }
        }

        @Override
        public void onFailure(Call<TokenResponse> call, Throwable t) {
            Log.i("onFailure", t.getMessage());
        }
    });
    }


    private void get_user_info(String url)
    {
        // Build the header
        String header = "token " + MainActivity.user.getToken();
        Log.i("header", header);
        Log.i("header", SERVER_USER_INFO);
        // Build the request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_USER_INFO)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServer retrofitServer = retrofit.create(RetrofitServer.class);

        Call<UserResponse> call = retrofitServer.getUserInfo(url, header);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful())
                {
                    // Could be 404 error...
                    Log.i("onResponse", Integer.toString(response.code()));
                }
                else
                {
                    if (response.body() != null) {
                        String image_url = response.body().data.image_url;
                        String pretty_name = response.body().data.pretty_name;
                        String user_name = response.body().data.username;

                        if (MainActivity.welcome_tv != null)
                        {
                            if (pretty_name == null ||pretty_name.equals(""))
                            {
                                MainActivity.welcome_tv.setText("Welcome, " + user_name);
                            }
                            else
                            {
                                MainActivity.welcome_tv.setText("Welcome, " + pretty_name);
                            }

                            // set image
                            String full_image_url = SERVER_USER_IMAGE + image_url;
                            Glide.with(getApplicationContext()).load(full_image_url).into(imageView);
                            // update pretty widget on UI
                            MainActivity.pretty_btn.setEnabled(true);
                            MainActivity.pretty_et.setEnabled(true);
                        }
                    }
                }
                MainActivity.loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                MainActivity.loading.setVisibility(View.GONE);
                Log.i("onFailure", t.getMessage());
            }
        });
    }


    private void post_user_pretty(String url)
    {
        // Build the header
        String header = "token " + MainActivity.user.getToken();
        String pretty_name = MainActivity.pretty_et.getText().toString();
        SetUserPrettyNameRequest setUserPrettyNameRequest = new SetUserPrettyNameRequest();
        setUserPrettyNameRequest.pretty_name = pretty_name;

        // Build the request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_USER_INFO)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServer retrofitServer = retrofit.create(RetrofitServer.class);

        Call<UserResponse> call = retrofitServer.postUserPretty(url, header, CONTENT, setUserPrettyNameRequest);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful())
                {
                    // Could be 404 error...
                    MainActivity.welcome_tv.setText("Sorry... Something bad happened");
                }
                else
                {
                    if (response.body() != null) {
                        String image_url = response.body().data.image_url;
                        String pretty_name = response.body().data.pretty_name;
                        String user_name = response.body().data.username;

                        if (MainActivity.welcome_tv != null)
                        {
                            if (pretty_name == null ||pretty_name.equals(""))
                            {
                                MainActivity.welcome_tv.setText("Welcome, " + user_name);
                            }
                            else
                            {
                                MainActivity.welcome_tv.setText("Welcome, " + pretty_name);
                            }

                        }
                    }
                }
                MainActivity.loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                MainActivity.loading.setVisibility(View.GONE);
                MainActivity.welcome_tv.setText("Sorry... Something bad happened");
            }
        });
    }

}