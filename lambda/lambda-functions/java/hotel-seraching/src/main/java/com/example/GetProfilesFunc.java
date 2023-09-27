package com.example;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.URL;


/**
 * Given a list of hotel IDs, get their profiles and return
 */
public class GetProfilesFunc {
    public static JsonObject main(JsonObject args) {
        JsonObject response = new JsonObject();
        JsonArray ids = args.getAsJsonArray("Ids");
        int len = ids.size();
        try {
 
            Database db = ClientBuilder.url(new URL(HotelCommon.COUCHDB_URL))
                .username(HotelCommon.COUCHDB_USERNAME)
                .password(HotelCommon.COUCHDB_PASSWORD)
                .build().database("profile", true);
            JsonArray profiles = new JsonArray();
            for (int i = 0; i < len; i++) {
                String id = ids.get(i).getAsString();
                JsonObject profObj = new JsonParser().parse(new InputStreamReader(db.find(id))).getAsJsonObject();
                profiles.add(profObj);
            }
            response.addProperty("Status", "OK");
            response.add("Profiles", profiles);
            return response;
        } catch (Exception e) {
            response.addProperty("Status", "Failed");
            response.addProperty("Reason", e.toString());
            return response;
        }
    }
}
