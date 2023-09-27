package com.datagen;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.URL;
import com.example.HotelCommon;

/**
 * Check if the user is valid
 */
public class GeoGen {
    public static void main(String[] args) {
        JsonObject response = new JsonObject();
        try {
            for (int dbi = 0; dbi <= 1; dbi++) {
                Database db = ClientBuilder.url(new URL(HotelCommon.COUCHDB_URL))
                    .username(HotelCommon.COUCHDB_USERNAME)
                    .password(HotelCommon.COUCHDB_PASSWORD)
                    .build().database("geo" + dbi, true);
                for (int i = 1; i <= 80; i++) {
                    JsonObject geoObj = new JsonObject();
                    geoObj.addProperty("_id", "" + i);
                    if (i == 1) {
                        geoObj.addProperty("Lat", new Double(37.7867));
                        geoObj.addProperty("Lon", new Double(-122.4112));
                    } else if (i == 2) {
                        geoObj.addProperty("Lat", new Double(37.7854));
                        geoObj.addProperty("Lon", new Double(-122.4005));
                    } else if (i == 3) {
                        geoObj.addProperty("Lat", new Double(37.7854));
                        geoObj.addProperty("Lon", new Double(-122.4071));
                    } else if (i == 4) {
                        geoObj.addProperty("Lat", new Double(37.7936));
                        geoObj.addProperty("Lon", new Double(-122.3930));
                    } else if (i == 5) {
                        geoObj.addProperty("Lat", new Double(37.7831));
                        geoObj.addProperty("Lon", new Double(-122.4181));
                    } else if (i == 6) {
                        geoObj.addProperty("Lat", new Double(37.7863));
                        geoObj.addProperty("Lon", new Double(-122.4015));
                    } else {
                        double lat = 37.7835 + i / 500.0 * 3;
                        double lon = -122.41 + i / 500.0 * 4;
                        geoObj.addProperty("Lat", lat);
                        geoObj.addProperty("Lon", lon);
                    }

                    Response rep = db.save(geoObj);
                    int code = rep.getStatusCode();
                    if ((code < 200) || (code >= 400)) {
                        System.out.println("code for geo " + i + " : code is: " + code);
                    }
                }
            }
        } catch(Exception e){
                System.out.println("Exception: " + e.toString());
        }
    }
}

