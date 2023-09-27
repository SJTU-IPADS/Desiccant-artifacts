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
public class RateGen {
    public static void main(String[] args) {
        JsonObject response = new JsonObject();
        try {
            for (int dbi = 0; dbi <= 2; dbi++) {
                Database db = ClientBuilder.url(new URL(HotelCommon.COUCHDB_URL))
                    .username(HotelCommon.COUCHDB_USERNAME)
                    .password(HotelCommon.COUCHDB_PASSWORD)
                    .build().database("rate"+dbi, true);
                for (int i = 1; i <= 80; i++) {
                    JsonObject rateObj = new JsonObject();
                    JsonObject roomObj = new JsonObject();
                    rateObj.addProperty("_id", "" + i);
                    if (i == 1) {
                        rateObj.addProperty("Code", "RACK");
                        rateObj.addProperty("InDate", "2020-02-26");
                        rateObj.addProperty("OutDate", "2020-02-29");
                        roomObj.addProperty("BookableRate", new Double(109));
                        roomObj.addProperty("Code", "KNG");
                        roomObj.addProperty("RoomDescription", "King sized bed");
                        roomObj.addProperty("TotalRate", new Double(109));
                        roomObj.addProperty("TotalRateInclusive", new Double(123.17));
                        rateObj.add("RoomType", roomObj);
                    } else if (i == 2) {
                        rateObj.addProperty("Code", "RACK");
                        rateObj.addProperty("InDate", "2020-02-28");
                        rateObj.addProperty("OutDate", "2020-02-29");
                        roomObj.addProperty("BookableRate", new Double(139));
                        roomObj.addProperty("Code", "QN");
                        roomObj.addProperty("RoomDescription", "Queen sized bed");
                        roomObj.addProperty("TotalRate", new Double(139));
                        roomObj.addProperty("TotalRateInclusive", new Double(153.09));
                        rateObj.add("RoomType", roomObj);
                    } else if (i == 3) {
                        rateObj.addProperty("Code", "RACK");
                        rateObj.addProperty("InDate", "2020-02-28");
                        rateObj.addProperty("OutDate", "2020-02-29");
                        roomObj.addProperty("BookableRate", new Double(109));
                        roomObj.addProperty("Code", "KNG");
                        roomObj.addProperty("RoomDescription", "King sized bed");
                        roomObj.addProperty("TotalRate", new Double(109));
                        roomObj.addProperty("TotalRateInclusive", new Double(123.17));
                        rateObj.add("RoomType", roomObj);
                    } else {
                        String endDataPrefix = "2020-02-";
                        double rate = 109.00;
                        double rate_inc = 123.17;
                        if (i % 2 == 0) {
                            endDataPrefix = endDataPrefix + "29";
                        } else {
                            endDataPrefix = endDataPrefix + "27";
                        }
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
                        rateObj.addProperty("Code", "RACK");
                        rateObj.addProperty("InDate", "2020-02-26");
                        rateObj.addProperty("OutDate", endDataPrefix);
                        roomObj.addProperty("BookableRate", new Double(rate));
                        roomObj.addProperty("Code", "KNG");
                        roomObj.addProperty("RoomDescription", "King sized bed");
                        roomObj.addProperty("TotalRate", new Double(rate));
                        roomObj.addProperty("TotalRateInclusive", new Double(rate_inc));
                        rateObj.add("RoomType", roomObj);
                    }

                    Response rep = db.save(rateObj);
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

