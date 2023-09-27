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
public class InventoryGen {
    public static void main(String[] args) {
        JsonObject response = new JsonObject();
        try {
            Database db = ClientBuilder.url(new URL("http://localhost:5984"))
                                       .username("whisk_admin")
                                       .password("some_passw0rd")
                                       .build().database("inventory", true);
            String[] dates = {"2020-02-26", "2020-02-27", "2020-02-28", "2020-02-29"};
            for (int i = 1; i <= 80; i++) {
                for (int j = 0; j < dates.length; j++) {
                    JsonObject invObj = new JsonObject();
                    invObj.addProperty("_id", i + "_" + dates[j]);
                    invObj.addProperty("Inventory", 2000);
                    Response rep = db.save(invObj);
                    int code = rep.getStatusCode();
                    if ((code < 200) || (code >= 400)) {
                        System.out.println("code for geo " + i + " : code is: " + code);
                    }
                }               
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
    }
}

