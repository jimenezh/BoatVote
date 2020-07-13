package com.example.voteboat.models;

import java.util.Date;

public class Poll {

    String location;
    String dartesOpen;
    Date openTime;
    Date closeTime;

    public String getLocation() {
        return location;
    }

    public String getDartesOpen() {
        return dartesOpen;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }
}
