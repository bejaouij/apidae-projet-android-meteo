package com.example.meteo;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class RequestApiTask extends AsyncTask {
    private HttpURLConnection connection;

    public RequestApiTask(HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            connection.connect();
        } catch (IOException e) {
            Log.d("EzW-debug", "Failed to connect to the API");
            Log.d("EzW-debug", e.getMessage());
        }

        int response = -1;

        try {
            response = connection.getResponseCode();
        } catch (IOException e) {
            Log.d("EzW-debug", "Failed to retrieve response code");
            e.getMessage();
        }

        InputStream inputStream = null;

        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            Log.d("EzW-debug", "Failed to retrieve data");
            e.getMessage();
        }

        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();

        try {
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
        } catch (IOException e) {
            Log.d("EzW-debug", "Failed to convert raw data in string");
            e.getMessage();
        }

        connection.disconnect();

        JSONObject weatherData = null;

        try {
            weatherData = new JSONObject(total.toString());
        } catch (JSONException e) {
            Log.d("EzW-debug", "Failed to convert string data in JSON object");
            Log.d("EzW-debug", e.toString());
            e.printStackTrace();
        }

        return weatherData;
    }

    /**
     * Entry point for the async process start
     *
     * @param object
     */
    public void execute(JSONHydratable object) {
        JSONObject data = (JSONObject) this.doInBackground(null);

        object.hydrate(data);
    }
}
