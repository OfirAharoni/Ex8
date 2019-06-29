package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    public static final int TOKEN_REQUEST_ID = 0;
    public static final String REQUEST_ID = "request_id";
    public static final String DEF_VALUE = "0";
    public static final String TOKEN_KEY = "token";
    public static final String SHARED_PREFERENCES = "shared preferences";
    public static final int PRETTY_POST_ID = 1;
    private EditText user_input;
    public static EditText pretty_et;
    public static Button pretty_btn;
    public static User user;
    public static ImageView imageView;
    public static TextView welcome_tv;
    public static ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_input = findViewById(R.id.input_et);
        welcome_tv = findViewById(R.id.welcome_tv);
        pretty_et = findViewById(R.id.pretty_et);
        pretty_btn = findViewById(R.id.pretty_btn);
        imageView = findViewById(R.id.image_v);
        loading = findViewById(R.id.loading);

        pretty_btn.setEnabled(false);
        pretty_et.setEnabled(false);
        loading.setVisibility(View.GONE);

    }

    /**
     * This function is used by the button in the main activity in order to send the
     * user name to the server to retrieve a token.
     * @param v
     */
    public void send_btn_action(View v)
    {
        String username = user_input.getText().toString();
        if (!username.equals(""))
        {
            MainActivity.loading.setVisibility(View.VISIBLE);
            Log.i("button", username);
            user = new User(username);
            applyWorker(TOKEN_REQUEST_ID);

        }
    }


    /**
     * This function is used by the button in the main activity in order to send the
     * user name to the server to retrieve a token.
     * @param v
     */
    public void post_btn_action(View v)
    {
        String pretty_name = user_input.getText().toString();
        if (!pretty_name.equals(""))
        {
            MainActivity.loading.setVisibility(View.VISIBLE);
            applyWorker(PRETTY_POST_ID);
        }
    }

    private void applyWorker(int id)
    {
        // Create the Data object:
        Data myData = new Data.Builder().putInt(REQUEST_ID, id).build();
        // WorkRequest for connection to the server
        OneTimeWorkRequest ConnectionWorkRequest = new
                OneTimeWorkRequest.Builder(ConnectionWorker.class)
                .setInputData(myData)
                .build();
        // Schedule the WorkRequest
        WorkManager.getInstance().enqueue(ConnectionWorkRequest);
    }

    /**
     * This function save the user's token with sharedPreferences.
     */
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, user.getToken());
        editor.apply();
    }

    /**
     * This function load data from sharedPreferences in other to obtain
     * previous input from the user.
     */
    private void loadData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String load_token = sharedPreferences.getString(TOKEN_KEY, DEF_VALUE);
    }
}
