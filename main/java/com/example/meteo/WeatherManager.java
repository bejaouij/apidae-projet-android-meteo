package com.example.meteo;

import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

import static android.provider.Settings.System.DATE_FORMAT;

public class WeatherManager extends Observable implements JSONHydratable {
    private static String EZW_DATE_FORMAT = "dd/MM HH:mm";
    private Date updatedAt;
    private int temperature;
    private String city = "Paris";
    private float latitude;
    private float longitude;
    private String apiBaseUrl = "https://www.prevision-meteo.ch/services/json/";
    private HttpURLConnection connection;
    private boolean isHydrating = false;
    private String weatherStateIconUri;

    public WeatherManager() {
        // TODO: 01/03/20 REPLACE BY ASYNC TASK AND REMOVE IMPORTED CLASS
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getFormattedUpdatedAt() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(EZW_DATE_FORMAT);
        String formattedUpdatedAt = dateFormat.format(this.updatedAt);

        return formattedUpdatedAt;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getWeatherStateIconUri() {
        return weatherStateIconUri;
    }

    public void init() {
        Uri uri = Uri.parse(this.apiBaseUrl).buildUpon().build();

        URL requestURL = null;

        try {
            requestURL = new URL(this.apiBaseUrl.toString() + this.city);
        } catch (MalformedURLException e) {
            Log.d("EzW-debug", "Invalid URL Instantiation");
            Log.d("EzW-debug", e.getMessage());
        }

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) requestURL.openConnection();
        } catch (IOException e) {
            Log.d("EzW-debug", "Invalid HTTP connection opening");
            Log.d("EzW-debug", e.getMessage());
        }

        connection.setReadTimeout(10000);
        connection.setConnectTimeout(10000);

        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            Log.d("EzW-debug", "Invalid Request Method");
            Log.d("EzW-debug", e.getMessage());
        }

        connection.setDoInput(true);

        this.connection = connection;

        this.update();
    }

    public void update() {
        RequestApiTask requestApiTask = new RequestApiTask(this.connection);

        this.isHydrating = true;
        requestApiTask.execute(this);
    }

    @Override
    public void hydrate(JSONObject data) {
        JSONObject cityInfo = null;
        JSONObject current_condition = null;

        try {
            cityInfo = data.getJSONObject("city_info");
            current_condition = data.getJSONObject("current_condition");

            this.city = cityInfo.getString("name");
            this.latitude = (float) cityInfo.getDouble("latitude");
            this.longitude = (float) cityInfo.getDouble("longitude");
            this.temperature = current_condition.getInt("tmp");
            this.weatherStateIconUri = current_condition.getString("icon_big");
        } catch (JSONException e) {
            Log.d("EzW-debug", "Unable to extract json data");
            Log.d("EzW-debug", e.getMessage());
        }

        this.updatedAt = new Date();
        this.isHydrating = false;

        setChanged();
        notifyObservers();
    }
}