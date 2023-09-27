package com.example;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *  Make Reservation in Hotel
 */
public class ReservationFunc {
  public static JsonObject main(JsonObject args) {
    JsonObject response = new JsonObject();
    String hotelId = args.getAsJsonPrimitive("HotelId").getAsString();
    String inDate = args.getAsJsonPrimitive("InDate").getAsString();
    String outDate = args.getAsJsonPrimitive("OutDate").getAsString();
    int number = args.getAsJsonPrimitive("Number").getAsInt();
    String customer = args.getAsJsonPrimitive("CustomerName").getAsString();
    try {

        CloudantClient client = ClientBuilder.url(new URL(HotelCommon.COUCHDB_URL))
            .username(HotelCommon.COUCHDB_USERNAME)
            .password(HotelCommon.COUCHDB_PASSWORD)
            .build();


        Database db = client.database("inventory", true);
        Database db1 = client.database("reservation", true);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(formatter.parse(inDate));
        Date outdate = formatter.parse(outDate);

        String cur = inDate;
        // We first check the availablilty of rooms for every day. The data from DB will be stored in the array
        List<JsonObject> updates = new ArrayList<JsonObject>();
        List<String> keys = new ArrayList<String>();

        while (!cur.equals(outDate)) {
            String invKey = hotelId + "_" + cur;
            keys.add(invKey);
            JsonObject invObj = new JsonParser().parse(new InputStreamReader(db.find(invKey))).getAsJsonObject();
         
            int inventory = invObj.getAsJsonPrimitive("Inventory").getAsInt();
            if (inventory >= number) {
                inventory -= number;
                invObj.remove("Inventory");
                invObj.addProperty("Inventory", new Integer(inventory));    
                updates.add(invObj);
            } else {
                response.addProperty("Status", "Failed");
                String reason = "Date has " + inventory + " rooms only";
                response.addProperty("Reason", reason);
                return response;
            }
           
            c.add(Calendar.DATE, 1);
            cur = formatter.format(c.getTime());
        }

        // After the availability check, sending updates to the DB. Abort if failed
        for (int i = 0; i < updates.size(); i++) { 
            Response rep = db.bulk(updates).get(0);
            // Response rep = db.update(updates.get(i));
            // System.out.println("The response code is: " + rep.getStatusCode()); 
            int code = rep.getStatusCode();
            if ((code < 200) || (code >= 400)) {
                response.addProperty("Status", "Failed");
                String reason = "The response code is: " + rep.getStatusCode();
                response.addProperty("Reason", reason);
                return response;
            } 
        }

        // Insert the Reservation log here
        // TODO: This part cannot be aborted.
        for (int i = 0; i < keys.size(); i++) {
            JsonObject log = new JsonObject();
            log.addProperty("_id", keys.get(i) + "_" + customer);
            log.addProperty("Date", keys.get(i).split("_")[1]);
            log.addProperty("Customer", customer);
            log.addProperty("Number", number);
            db1.save(log); 
        }
        

        // Return successfully
        response.addProperty("Status", "Succeeded");
        return response;
    } catch (Exception e) {
        response.addProperty("Status", "Failed");
        response.addProperty("Reason", e.toString());
        return response;
    }
  }
}
