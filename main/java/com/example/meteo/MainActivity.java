package com.example.meteo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;

import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {
    static int WEATHER_STATE_ICON_SIZE = 300;

    private WeatherManager weatherManager = new WeatherManager();

    /**
     * Tell if the network is available
     *
     * @return true if the network is available or false if not
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isNetworkAvailable()) {
            this.weatherManager = new WeatherManager();
            this.weatherManager.addObserver(this);
            this.weatherManager.init();

        } else {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
        }

        Button changeCityButton = findViewById(R.id.change_city_button);

        changeCityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextInputEditText changeCityInputText = findViewById(R.id.change_city_text_input);
                String userCityChoice = changeCityInputText.getText().toString();

                if (userCityChoice == null || userCityChoice.equals("")) {
                    findViewById(R.id.invalid_city_warning_text).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.invalid_city_warning_text).setVisibility(View.INVISIBLE);

                    weatherManager.setCity(userCityChoice);
                    weatherManager.init();
                }
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {
        // Update temperature display
        TextView temperatureTextView = findViewById(R.id.temperature);
        String textTemperature = String.valueOf(this.weatherManager.getTemperature()) + getString(R.string.temperature_unit);
        temperatureTextView.setText(textTemperature);

        // Update city name display
        TextView cityTextView = findViewById(R.id.city);
        cityTextView.setText(this.weatherManager.getCity());

        // Update update date display
        TextView updatedAtTextView = findViewById(R.id.updated_at);
        updatedAtTextView.setText(this.weatherManager.getFormattedUpdatedAt());

        // Update weather icon display
        ImageView weatherIconImageView = findViewById(R.id.weather_icon);


        Picasso.with(this)
                .load(this.weatherManager.getWeatherStateIconUri())
                .into(weatherIconImageView);
    }
}
