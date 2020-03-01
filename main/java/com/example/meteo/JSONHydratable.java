package com.example.meteo;

import org.json.JSONObject;

public interface JSONHydratable {
    void hydrate(JSONObject data);
}
