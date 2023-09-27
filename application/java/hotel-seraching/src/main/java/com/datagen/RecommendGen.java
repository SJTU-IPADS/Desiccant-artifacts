package com.datagen;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.URL;

/**
 * Check if the user is valid
 */
public class RecommendGen {
    public static void main(String[] args) {
        JsonObject response = new JsonObject();
        try {
            Database db = ClientBuilder.url(new URL("http://localhost:5984"))
                                       .username("whisk_admin")
                                       .password("some_passw0rd")
                                       .build().database("recommendation", true);
            for (int i = 1; i <= 80; i++) {
                JsonObject recObj = new JsonObject();
                recObj.addProperty("_id", "" + i);
                if (i == 1) {
                    recObj.addProperty("Lat", new Double(37.7867));
                    recObj.addProperty("Lon", new Double(-122.4112));
                    recObj.addProperty("Rate", new Double(109));
                    recObj.addProperty("Price", new Double(150));
                } else if (i == 2) {
                    recObj.addProperty("Lat", new Double(37.7854));
                    recObj.addProperty("Lon", new Double(-122.4005));
                    recObj.addProperty("Rate", new Double(139));
                    recObj.addProperty("Price", new Double(120));
                } else if (i == 3) {
                    recObj.addProperty("Lat", new Double(37.7854));
                    recObj.addProperty("Lon", new Double(-122.4071));
                    recObj.addProperty("Rate", new Double(109));
                    recObj.addProperty("Price", new Double(190));
                } else if (i == 4) {
                    recObj.addProperty("Lat", new Double(37.7936));
                    recObj.addProperty("Lon", new Double(-122.3930));
                    recObj.addProperty("Rate", new Double(129));
                    recObj.addProperty("Price", new Double(160));
                } else if (i == 5) { 
                    recObj.addProperty("Lat", new Double(37.7831));
                    recObj.addProperty("Lon", new Double(-122.4181));
                    recObj.addProperty("Rate", new Double(109));
                    recObj.addProperty("Price", new Double(140));
                } else if (i == 6) {
                    recObj.addProperty("Lat", new Double(37.7863));
                    recObj.addProperty("Lon", new Double(-122.4015));
                    recObj.addProperty("Rate", new Double(149.00));
                    recObj.addProperty("Price", new Double(200.00));
                } else {
                    double lat = 37.7835 + i/500.0 * 3;
                    double lon = -122.41 + i/500.0 * 4;
                    recObj.addProperty("Lat", lat);
                    recObj.addProperty("Lon", lon);
                    double rate = 109.00;
                    double rate_inc = 123.17;
                    if (i % 5 == 1) {
                        rate = 120.00;
                        rate_inc = 140.00;
                    } else if (i % 5 == 2) {
                        rate = 124.00;
                        rate_inc = 144.00;
                    } else if (i % 5 == 3) {
                        rate = 132.00;
                        rate_inc = 158.00;
                    } else if (i % 5 == 4) {
                        rate = 232.00;
                        rate_inc = 258.00;
                    }
                    recObj.addProperty("Rate", rate);
                    recObj.addProperty("Price", rate_inc); 
                }
                
                Response rep = db.save(recObj);
                int code = rep.getStatusCode();
                if ((code < 200) || (code >= 400)) {
                    System.out.println("code for geo " + i + " : code is: " + code);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
    }
}

