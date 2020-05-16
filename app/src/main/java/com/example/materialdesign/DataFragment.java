package com.example.materialdesign;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
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

public class DataFragment extends Fragment {

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();


    /** URL to the api for IndiaCovidInfo */
    private static final String CURRENT_COVID_INDIA = "https://corona.lmao.ninja/v2/countries/IND";

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.data_fragment, container, false);

        MoreInfo task = new MoreInfo();
        task.execute();




        return rootView;
    }




    private void updateUi(MoreInfoData currentCovid){


        BarChart mBarChartR = null;
        BarChart mBarChartL = null;

       try {
           mBarChartR = (BarChart) getView().findViewById(R.id.barchartR);
           mBarChartL = (BarChart) getView().findViewById(R.id.barchartL);
           mBarChartL.addBar(new BarModel(currentCovid.casesFoundToday, Color.parseColor("#0be881")));
           mBarChartL.addBar(new BarModel(currentCovid.deathsToday,  Color.parseColor("#ff3f34")));
           mBarChartL.addBar(new BarModel(currentCovid.casesPerM, Color.parseColor("#ffd32a")));
           mBarChartR.addBar(new BarModel(currentCovid.testsDone, Color.parseColor("#575fcf")));

           mBarChartR.startAnimation();
           mBarChartL.startAnimation();
       }catch(NullPointerException ignore){}




        TextView totalCases =null;

        TextView totalDeaths = null;

        TextView casesPM = null;

        TextView testsDone = null;

        try {
            totalCases = (TextView) getView().findViewById(R.id.casesFoundToday);
            totalCases.setText(Integer.toString(currentCovid.casesFoundToday));

            totalDeaths = (TextView) getView().findViewById(R.id.deathsToday);
            totalDeaths.setText(Integer.toString(currentCovid.deathsToday));

            casesPM = (TextView) getView().findViewById(R.id.casesPerM);
            casesPM.setText(Integer.toString(currentCovid.casesPerM));

            testsDone = (TextView) getView().findViewById(R.id.testsDone);
            testsDone.setText(Integer.toString(currentCovid.testsDone));

        }catch (NullPointerException ignore){}

    }


    private class MoreInfo extends AsyncTask<URL,Void,MoreInfoData> {

        @Override
        protected MoreInfoData doInBackground(URL... urls) {

            URL  url = createUrl(CURRENT_COVID_INDIA);

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            MoreInfoData currentCovid =extractJsonData(jsonResponse);



            return currentCovid;
        }

        @Override
        protected void onPostExecute(MoreInfoData d) {
            if(d == null)
                return;

            updateUi(d);
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

    private MoreInfoData extractJsonData(String jsonResponse){

        try{

            JSONObject baseObject = new JSONObject(jsonResponse);

            int caseFoundToday = baseObject.getInt("todayCases");
            int deathsToday = baseObject.getInt("todayDeaths");
            int casesPerM = baseObject.getInt("casesPerOneMillion");
            int testsDone = baseObject.getInt("tests");


            return new MoreInfoData(caseFoundToday,deathsToday,casesPerM,testsDone);

        }catch(JSONException e){
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);

        }
        return null;
    }

}

