package com.example.voteboat.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Candidate")
public class Candidate extends ParseObject {

    String name;
    String party;
    String websiteUrl;

    public static final String KEY_NAME="name";
    public static final String KEY_PARTY="party";
    public static final String KEY_WEBSITE_URL="url";


    public static Candidate fromJsonObject(JSONObject jsonObject) throws JSONException {
        Candidate candidate = new Candidate();
        candidate.put(KEY_NAME, jsonObject.getString("name"));
        candidate.put(KEY_PARTY, jsonObject.getString("party"));
        candidate.put(KEY_WEBSITE_URL, checkifExistsAndAdd("candidateUrl", jsonObject));
        return candidate;
    }

    private static String checkifExistsAndAdd(String field, JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(field))
            return jsonObject.getString(field);
        else
            return "";
    }


    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }
}
