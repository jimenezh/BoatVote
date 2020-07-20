package com.example.voteboat.clients;

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
    public static final String DUMMY_ADDRESS = "9239 Carlin Street, Detroit, Michigan";
    public static final int ID = 5003;
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

    public void voterInformationElections(String ocdId, String address, JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("address", DUMMY_ADDRESS);
        params.put("electionId", ID);
        String url = String.format(BASE_URL_CIVIC, VOTER_INFO_KEY, KEY);
        Log.i(TAG, "using url " + url);
        client.get(url, params, handler);
    }

    public void getRepresentatives(JsonHttpResponseHandler handler) {
        String url = String.format(BASE_URL_REPS, KEY);
        RequestParams params = new RequestParams();
        params.put("address", DUMMY_ADDRESS);
        client.get(url, params,handler);
    }
}
