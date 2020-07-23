package com.example.voteboat.clients;

import android.location.Address;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.voteboat.BuildConfig;


public class GoogleCivicClient {
    public static final String TAG = "GoogleCivicClient";
    public static final String BASE_URL_CIVIC = "https://www.googleapis.com/civicinfo/v2/%s?key=%s";
    public static final String BASE_URL_REPS = "https://www.googleapis.com/civicinfo/v2/representatives?key=%s";

    public static final String ELECTION_INFO_KEY = "elections";
    public static final String VOTER_INFO_KEY = "voterinfo";

    public static final String KEY = BuildConfig.GOOGLE_API_KEY;
    AsyncHttpClient client;

    public GoogleCivicClient() {
        client = new AsyncHttpClient();
    }

    // Function to get elections
    public void getElections(JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
//        params.put("address",DUMMY_ADDRESS );
        String url = String.format(BASE_URL_CIVIC, ELECTION_INFO_KEY, KEY);
        Log.i(TAG, "using url " + url);
        client.get(url, params, jsonHttpResponseHandler);
    }

    public void voterInformationElections(String googleId, String address, JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("address", address);
        params.put("electionId", googleId);
        String url = String.format(BASE_URL_CIVIC, VOTER_INFO_KEY, KEY);
        Log.i(TAG, "using url " + url);
        client.get(url, params, handler);
    }

    public void getRepresentatives(String address, JsonHttpResponseHandler handler) {
        String url = String.format(BASE_URL_REPS, KEY);
        RequestParams params = new RequestParams();
        params.put("address",address );
        client.get(url, params,handler);
    }
}
