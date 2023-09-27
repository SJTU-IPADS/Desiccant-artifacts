package com.example;
import static com.cloudant.client.api.query.Expression.gt;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.query.QueryBuilder;
import com.cloudant.client.api.query.QueryResult;
import com.cloudant.client.api.query.Selector;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import gnu.trove.procedure.TIntProcedure;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsi.SpatialIndex;
import net.sf.jsi.rtree.RTree;
import net.sf.jsi.Rectangle;
import net.sf.jsi.Point;



/**
 * Given a list of hotel IDs, get their profiles and return
 */
public class RecommendationFunc {

    public static JsonObject main(JsonObject args) {
        JsonObject response = new JsonObject();
        try {
            // Initialization by getting all objects from profile db 
            Database db = ClientBuilder.url(new URL(HotelCommon.COUCHDB_URL))
                                       .username(HotelCommon.COUCHDB_USERNAME)
                                       .password(HotelCommon.COUCHDB_PASSWORD)
                                       .build().database("recommendation", true);
            // QueryResult<Geo> profiles = db.query(new QueryBuilder(gt("Lat", 0)).build(), Geo.class);
            QueryResult<JsonObject> profiles = db.query(new QueryBuilder(gt("Lat", 0)).build(), JsonObject.class);
            List<JsonObject> profileList = profiles.getDocs();
            int size = profileList.size();

            String req = args.getAsJsonPrimitive("Require").getAsString();
            JsonArray ids = new JsonArray();
            if (req.equals("dis")) {
                float min = (float)1e10;
                double lat0 = args.getAsJsonPrimitive("Lat").getAsDouble();
                double lon0 = args.getAsJsonPrimitive("Lon").getAsDouble();
                Point p = new Point((float)lat0, (float)lon0);
                // get the minimum value first
                for (int i = 0; i < size; i++) {
                    JsonObject profile = profileList.get(i);
                    double lat = profile.getAsJsonPrimitive("Lat").getAsDouble();
                    double lon = profile.getAsJsonPrimitive("Lon").getAsDouble();
                    Rectangle rect = new Rectangle((float)lat, (float)lon, (float)lat, (float)lon);   
                    float dist = rect.distance(p);
                    if (min > dist) {
                        min = dist;
                    }
                }

                // use the value to get all hotel id
                for (int i = 0; i < size; i++) {
                    JsonObject profile = profileList.get(i);
                    double lat = profile.getAsJsonPrimitive("Lat").getAsDouble();
                    double lon = profile.getAsJsonPrimitive("Lon").getAsDouble();
                    Rectangle rect = new Rectangle((float)lat, (float)lon, (float)lat, (float)lon);   
                    float dist = rect.distance(p);
                    if (min == dist) {
                        String id = profile.getAsJsonPrimitive("_id").getAsString();
                        ids.add(id);
                    }
                }
            } else if (req.equals("rate")) {
               int max = 0; 
               // get the minimum value first
               for (int i = 0; i < size; i++) {
                   JsonObject profile = profileList.get(i);
                   int rate = profile.getAsJsonPrimitive("Rate").getAsInt();
                   if (max < rate) {
                       max = rate;
                   }
               }

               // use the value to get all hotel id
               for (int i = 0; i < size; i++) {
                   JsonObject profile = profileList.get(i);
                   int rate = profile.getAsJsonPrimitive("Rate").getAsInt();
                   if (max == rate) {
                       String id = profile.getAsJsonPrimitive("_id").getAsString();
                       ids.add(id);
                   }
               }
            } else if (req.equals("price")) {
                int min = 100000000;
                // get the minimum value first
                for (int i = 0; i < size; i++) {
                    JsonObject profile = profileList.get(i);
                    int price = profile.getAsJsonPrimitive("Price").getAsInt();
                    if (min > price) {
                        min = price;
                    }
                }

                // use the value to get all hotel id
                for (int i = 0; i < size; i++) {
                    JsonObject profile = profileList.get(i);
                    int price = profile.getAsJsonPrimitive("Price").getAsInt();
                    if (min == price) {
                       String id = profile.getAsJsonPrimitive("_id").getAsString();
                       ids.add(id);
                    }
                }               
            } else {
                response.addProperty("Status", "Failed");
                response.addProperty("Reason", "The requirement is not supported");
                return response;
            }



            response.addProperty("Status", "OK");
            response.add("Hotels", ids);
            // response.add("Profiles", profiles);
            return response;
        } catch (Exception e) {
            response.addProperty("Status", "Failed");
            response.addProperty("Reason", e.toString());
            return response;
        }
    }
}
