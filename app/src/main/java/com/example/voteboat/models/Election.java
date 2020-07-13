package com.example.voteboat.models;

import java.util.Date;
import java.util.List;

public class Election {
    String title;
    String googleId;
    Date electionDate;
    List<Candidate> candidates;
    List<Poll> earlyPolls;
    List<Poll> electionDayPolls;
    List<Poll> absenteeBallotLocations;
    String registrationLink;

    public String getTitle() {
        return title;
    }

    public Election(String title, Date electionDate) {
        this.title = title;
        this.electionDate = electionDate;
    }

    public String getGoogleId() {
        return googleId;
    }

    public Date getElectionDate() {
        return electionDate;
    }

    public List<Candidate> getCandidates() {
        return candidates;
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
