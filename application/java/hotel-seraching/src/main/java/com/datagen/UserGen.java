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
public class UserGen {
    public static void main(String[] args) {
        JsonObject response = new JsonObject();
        try {
            Database db = ClientBuilder.url(new URL("http://localhost:5984"))
                                       .username("whisk_admin")
                                       .password("some_passw0rd")
                                       .build().database("user", true);
            for (int i = 0; i <= 500; i++) {
                String username = "SJTU_" + i;
                JsonObject userObj = new JsonObject();
                String password = "sjtuuser" + i;
                userObj.addProperty("_id", username);
                userObj.addProperty("Password", password);

               
                Response rep = db.save(userObj);
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

