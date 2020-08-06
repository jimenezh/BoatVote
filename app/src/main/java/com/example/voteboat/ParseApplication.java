package com.example.voteboat;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.voteboat.models.Candidate;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.Race;
import com.example.voteboat.models.ToDoItem;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        ParseObject.registerSubclass(ToDoItem.class);
        ParseObject.registerSubclass(Election.class);
        ParseObject.registerSubclass(Race.class);
        ParseObject.registerSubclass(Candidate.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("vote-boat") // should correspond to APP_ID env variable
                .clientKey(null)  // set explicitly unless clientKey is explicitly configured on Parse server
                .clientBuilder(builder)
                .enableLocalDataStore()
                .server("https://vote-boat.herokuapp.com/parse/").build());


        // Push notifications set up
        ParseInstallation.getCurrentInstallation().saveInBackground();
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

    }
}
