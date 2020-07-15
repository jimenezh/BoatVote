package com.example.voteboat.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Candidate {

    String googleId;
    String name;
    String party;
    String websiteUrl;
    String parseElectionId;
    String googleElectionId;

    public static List<Candidate> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Candidate> candidates = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            candidates.add(Candidate.fromJsonObject(jsonArray.getJSONObject(i)));
        }
        return candidates;
    }

    public static Candidate fromJsonObject(JSONObject jsonObject) throws JSONException {
        Candidate candidate = new Candidate();
        candidate.name = jsonObject.getString("name");
        candidate.party = jsonObject.getString("party");
        candidate.websiteUrl = checkifExistsAndAdd("candidateUrl", jsonObject);
        return candidate;
    }

    private static String checkifExistsAndAdd(String field, JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(field))
            return jsonObject.getString(field);
        else
            return "";
    }

    public String getGoogleId() {
        return googleId;
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

    public String getParseElectionId() {
        return parseElectionId;
    }

    public String getGoogleElectionId() {
        return googleElectionId;
    }
}
