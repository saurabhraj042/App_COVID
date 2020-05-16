package com.example.materialdesign;


/** @IndiaCovidInfo show the current Scenario of Covid-19 in India
 * */

public class HomeFragmentAPI_Setup {

    /** Variables that define this current state of COVID in India**/

    public final int totalCases;
    public final  int totalDeaths;
    public final int totalRecovered;


    /**
     * @param tc is the total cases till the current date got back from the api
     * @param td  is the total deaths till now
     * @param tr  is the total recovered
     * **/

    HomeFragmentAPI_Setup(int tc,int td,int tr){
        totalCases = tc;
        totalDeaths = td;
        totalRecovered = tr;
    }
}
