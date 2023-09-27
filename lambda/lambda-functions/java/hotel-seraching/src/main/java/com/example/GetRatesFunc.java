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
import java.util.concurrent.TimeUnit;

/**
 * Given a list of hotel IDs, get their rates and return
 */
public class GetRatesFunc {
    static Database db = null;
    public static JsonObject main(JsonObject args) {
        JsonObject response = new JsonObject();
        int dbi = args.get("thread").getAsInt();
        response.addProperty("thread", dbi);
        if (args.getAsJsonPrimitive("Status") != null) {
            String status = args.getAsJsonPrimitive("Status").getAsString();
            if (status.equals("Failed") == true) {
                return args;
            }
        } 
        JsonArray ids = args.getAsJsonArray("Ids");
        int len = ids.size();
        try {
//            CloudantClient client = ClientBuilder.url(new URL("http://lg-4k:5984"))
            if (db == null) {
                CloudantClient client = ClientBuilder.url(new URL(HotelCommon.COUCHDB_URL))
                    .username(HotelCommon.COUCHDB_USERNAME)
                    .password(HotelCommon.COUCHDB_PASSWORD)
                    .build();
                db = client.database("rate"+dbi, true);
            }
            // System.out.println("[zzm] GetRatesFunc.create: " + (t1 - t0));
            JsonArray rates = new JsonArray();
            for (int i = 0; i < len; i++) {
                String id = ids.get(i).getAsString();
                InputStreamReader x = new InputStreamReader(db.find(id));
                JsonObject rateObj = new JsonParser().parse(x).getAsJsonObject();
                x.close();
                rates.add(rateObj);
            }
            /*
            Thread.sleep(8);
            JsonArray rates = new JsonArray();
            for (int i = 0; i < len; i++) {
                String id = ids.get(i).getAsString();
                String s = getFakeObject(id);
                JsonObject rateObj = new JsonParser().parse(s).getAsJsonObject();
                rates.add(rateObj);
            }
            */
            response.addProperty("Status", "OK");
            response.add("Rates", rates);
            return response;
        } catch (Exception e) {
            response.addProperty("Status", "Failed");
            response.addProperty("Reason", e.toString());
            return response;
        }
    }

    private static String getFakeObject(String id) {
        if (id.equals("2"))
            return  "{\"_id\":\"2\",\"_rev\":\"1-aaa34a4c4c98e4a4af84dbe141cfa3ab\",\"Code\":\"RACK\",\"InDate\":\"2020-02-28\",\"OutDate\":\"2020-02-29\",\"RoomType\":{\"BookableRate\":139.0,\"Code\":\"QN\",\"RoomDescription\":\"Queen sized bed\",\"TotalRate\":139.0,\"TotalRateInclusive\":153.09}}";
        else
            return "{\"_id\":\"3\",\"_rev\":\"1-9b14198b3af11ed6df70f831d843131d\",\"Code\":\"RACK\",\"InDate\":\"2020-02-28\",\"OutDate\":\"2020-02-29\",\"RoomType\":{\"BookableRate\":109.0,\"Code\":\"KNG\",\"RoomDescription\":\"King sized bed\",\"TotalRate\":109.0,\"TotalRateInclusive\":123.17}}";
    }
}

 
