package com.example.materialdesign;

public class MoreInfoData {

    public final int casesFoundToday;
    public final int deathsToday;
    public final int casesPerM;
    public final int testsDone;

    MoreInfoData(int cft,int dt,int cpm,int td){
        casesFoundToday = cft;
        deathsToday = dt;
        casesPerM = cpm;
        testsDone = td;
    }
}
