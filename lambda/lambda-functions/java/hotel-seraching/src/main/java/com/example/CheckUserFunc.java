package com.example;

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
public class CheckUserFunc {
    public static JsonObject main(JsonObject args) {
        JsonObject response = new JsonObject();
        String user = args.getAsJsonPrimitive("Username").getAsString();
        String pwd = args.getAsJsonPrimitive("Password").getAsString();
        try {
            Database db = ClientBuilder.url(new URL(HotelCommon.COUCHDB_URL))
                .username(HotelCommon.COUCHDB_USERNAME)
                .password(HotelCommon.COUCHDB_PASSWORD)
                .build().database("user", true);

            JsonObject userObj = new JsonParser().parse(new InputStreamReader(db.find(user))).getAsJsonObject();
            if (userObj == null) { 
                response.addProperty("Status", "Failed");
                response.addProperty("Reason", "User does not exist");
                return response;
            }
            String realPwd = userObj.getAsJsonPrimitive("Password").getAsString();
            if (pwd.equals(realPwd)) {
                response.addProperty("Status", "OK");
                return response;
            } else {
                response.addProperty("Status", "Failed");
                response.addProperty("Reason", "Incorrect password");
                return response;
            }
        } catch (Exception e) {
            response.addProperty("Status", "Failed");
            response.addProperty("Reason", e.toString());
            return response;
        }
    }
}

