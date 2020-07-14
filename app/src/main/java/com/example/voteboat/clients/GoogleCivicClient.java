package com.example.voteboat.clients;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;

import java.util.List;

import okhttp3.Headers;

public class GoogleCivicClient {
    public static final String TAG = "GoogleCivicClient";
    public static final String BASE_URL = "https://www.googleapis.com/civicinfo/v2/%s?key=%s";

    public static final String ELECTION_INFO_KEY = "elections";
    public static final String VOTER_INFO_KEY = "voterinfo";

    public static final String DUMMY_ADDRESS = "50 Redding Ridge Dr, Gaithersburg, MD 20878";
    AsyncHttpClient client;

    public GoogleCivicClient() {
        client = new AsyncHttpClient();
    }

    // Function to get elections
    public void getElections(String address){
        RequestParams params = new RequestParams();
//        params.put("address",DUMMY_ADDRESS );
        String url = String.format(BASE_URL, ELECTION_INFO_KEY, KEY);
        Log.i(TAG,"using url "+url);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Elections are: "+json.toString());

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG,"No elections "+response, throwable);
            }
        });
    }

}
