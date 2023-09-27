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
public class SearchResultFunc {
    public static JsonObject main(JsonObject args) {
        JsonObject response = new JsonObject();
        int dbi = args.get("thread").getAsInt();
        response.addProperty("thread", dbi);
        if (args.getAsJsonPrimitive("Status") != null) {
            String status = args.getAsJsonPrimitive("Status").getAsString();
            if (status.equals("Failed") == true) {
                response.addProperty("Status", "Failed");
                return args;
            }
        } 
        try {
            JsonArray rates = args.getAsJsonArray("Rates");
            int len = rates.size();
            JsonArray ids = new JsonArray();
            for (int i = 0; i < len; i++) {
                String id = rates.get(i).getAsJsonObject().getAsJsonPrimitive("_id").getAsString();
                ids.add(id);
            }
            response.addProperty("Status", "OK");
            response.add("Hotels", ids);
            return response;
        } catch (Exception e) {
            response.addProperty("Status", "Failed");
            response.addProperty("Reason", e.toString());
            return response;
        }
    }
}
