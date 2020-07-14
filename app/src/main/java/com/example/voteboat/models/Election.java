package com.example.voteboat.models;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Election {
    String title;
    String googleId;
    String electionDate;
    List<Race> races;
    List<Poll> earlyPolls;
    List<Poll> electionDayPolls;
    List<Poll> absenteeBallotLocations;
    String registrationLink;
    String ocd_id;

    public List<Race> getRaces() {
        return races;
    }

    public static Election basicInformationFromJson(JSONObject json) throws JSONException {
        Election election = new Election();
        election.title = json.getString("name");
        election.electionDate = json.getString("electionDay");
        election.googleId = json.getString("id");
        election.ocd_id = json.getString("ocdDivisionId");

        return election;
    }

    public static Election fromJsonObject(JSONObject jsonObject) throws JSONException {
        JSONObject electionBasicInfo = jsonObject.getJSONObject("election");
        Election election = Election.basicInformationFromJson(electionBasicInfo);

        election.electionDayPolls = Poll.fromJsonArray(jsonObject.getJSONArray("pollingLocations"));
                new ArrayList<>();
        election.races = Race.fromJsonArray(jsonObject.getJSONArray("contests"));

        return election;

    }


    public String getOcd_id() {
        return ocd_id;
    }

    public String getTitle() {
        return title;
    }

    public Election() {
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getElectionDate() {
        return electionDate;
    }

    public List<Poll> getEarlyPolls() {
        return earlyPolls;
    }

    public List<Poll> getElectionDayPolls() {
        return electionDayPolls;
    }

    public List<Poll> getAbsenteeBallotLocations() {
        return absenteeBallotLocations;
    }

    public String getRegistrationLink() {
        return registrationLink;
    }
}
