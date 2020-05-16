package com.example.materialdesign;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class BiharFragment extends Fragment {

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();


    /** URL to the api for Bihar Data*/
    private static final String CURRENT_COVID_BIHAR = "https://api.covid19india.org/state_district_wise.json";


    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    CovidBihar task = new CovidBihar();
    task.execute();

        View rootView = inflater.inflate(R.layout.bihar_fragment, container, false);



        return rootView;
    }


    private void updateUi(DataBihar currentCovid){


        TextView mumC =null;
        TextView rncC =null;
        TextView bgp = null;
        TextView patna = null;
        TextView dhn = null;
        TextView bksc = null;
        TextView hzb = null;


        try {
            mumC= (TextView) getView().findViewById(R.id.mumC);
            mumC.setText(Integer.toString(currentCovid.mumbai));

            rncC= (TextView) getView().findViewById(R.id.rncC);
            rncC.setText(Integer.toString(currentCovid.rnc));

            bgp= (TextView) getView().findViewById(R.id.bgpC);
            bgp.setText(Integer.toString(currentCovid.bgp));

            patna= (TextView) getView().findViewById(R.id.patnaC);
            patna.setText(Integer.toString(currentCovid.patna));

            rncC= (TextView) getView().findViewById(R.id.rncC);
            rncC.setText(Integer.toString(currentCovid.rnc));

            dhn= (TextView) getView().findViewById(R.id.dhnC);
            dhn.setText(Integer.toString(currentCovid.dhn));

            bksc= (TextView) getView().findViewById(R.id.bkC);
            bksc.setText(Integer.toString(currentCovid.bokaro));

            hzb= (TextView) getView().findViewById(R.id.hzbC);
            hzb.setText(Integer.toString(currentCovid.hzb));


        }catch (NullPointerException ignored){}


    }


    private class CovidBihar extends AsyncTask<URL,Void,DataBihar> {

        @Override
        protected DataBihar doInBackground(URL... urls) {

            URL  url = createUrl(CURRENT_COVID_BIHAR);

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            DataBihar currentCovid =extractJsonData(jsonResponse);



            return currentCovid;
        }

        @Override
        protected void onPostExecute(DataBihar biharCovidInfo) {
            if(biharCovidInfo == null)
                return;

            updateUi(biharCovidInfo);
        }


    }

    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {
            // TODO: Handle the exception
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private DataBihar extractJsonData(String jsonResponse){

        try{

            JSONObject baseObject = new JSONObject(jsonResponse);

            JSONObject biharData = baseObject.getJSONObject("Bihar");
            JSONObject districtData = biharData.getJSONObject("districtData");

            JSONObject jharkhandData = baseObject.getJSONObject("Jharkhand");
            JSONObject jhdistrictData = jharkhandData.getJSONObject("districtData");


            JSONObject mahData = baseObject.getJSONObject("Maharashtra");
            JSONObject mhdistrictData = mahData.getJSONObject("districtData");





            /** BIHAR JSON DATA **/
            JSONObject bgpData = districtData.getJSONObject("Bhagalpur");
            int bgpC = bgpData.getInt("confirmed");
            JSONObject patnaData = districtData.getJSONObject("Patna");
            int pC = patnaData.getInt("confirmed");



            /**Jharkhand JSON DATA**/
            JSONObject rncData = jhdistrictData.getJSONObject("Ranchi");
            int rncC = rncData.getInt("confirmed");

            JSONObject hzbData = jhdistrictData.getJSONObject("Hazaribagh");
            int hzbC = hzbData.getInt("confirmed");

            JSONObject dhnData = jhdistrictData.getJSONObject("Dhanbad");
            int dhnC = dhnData.getInt("confirmed");

            JSONObject bkData = jhdistrictData.getJSONObject("Bokaro");
            int bkC = bkData.getInt("confirmed");


            /**Mumbai Data**/
            JSONObject mumData = mhdistrictData.getJSONObject("Mumbai");
            int mumC = mumData.getInt("confirmed");



            return new DataBihar(bgpC,pC,rncC,hzbC,bkC,dhnC,mumC);

        }catch(JSONException e){
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);

        }
        return null;
    }
}

