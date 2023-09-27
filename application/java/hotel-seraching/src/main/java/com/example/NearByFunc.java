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
public class NearByFunc {

    private class Geo {
        private String _id;
        private String _rev;
        private double lat;
        private double lon;

        public String id() { return _id; }
        public double lat()   { return lat; }
        public double lon()   { return lon; }
    }

    public static JsonObject main(JsonObject args) {
        JsonObject response = new JsonObject();
        try {
            // Initialization by getting all objects from geo db 
//            Database db = ClientBuilder.url(new URL("http://lg-4k:5984"))

            Database db = ClientBuilder.url(new URL(HotelCommon.COUCHDB_URL))
                                       .username(HotelCommon.COUCHDB_USERNAME)
                                       .password(HotelCommon.COUCHDB_PASSWORD)
                                       .build().database("geo", true);
            // QueryResult<Geo> geos = db.query(new QueryBuilder(gt("Lat", 0)).build(), Geo.class);
            QueryResult<JsonObject> geos = db.query(new QueryBuilder(gt("Lat", 0)).build(), JsonObject.class);
            List<JsonObject> geoList = geos.getDocs();
            // List<Geo> geoList = geos.getDocs();

            // Use RTree (Java Spatial Index) to build the indices.
            SpatialIndex rtree = new RTree();
            rtree.init(null);
            int size = geoList.size();
            ArrayList<Integer> foundIds = new ArrayList<Integer>();
            for (int i = 0; i < size; i++) {
                JsonObject geo = geoList.get(i);
                double lat = geo.getAsJsonPrimitive("Lat").getAsDouble();
                double lon = geo.getAsJsonPrimitive("Lon").getAsDouble();
                Rectangle rect = new Rectangle((float)lat, (float)lon, (float)lat, (float)lon);
                rtree.add(rect, i);
            }

			double lat = args.getAsJsonPrimitive("Lat").getAsDouble();
			double lon = args.getAsJsonPrimitive("Lon").getAsDouble();
			Point p = new Point((float)lat, (float)lon);
			rtree.nearestN(p, new TIntProcedure() {
				public boolean execute(int i) {
                    foundIds.add(new Integer(i)); 
					return true;
				}
			}, 2, Float.MAX_VALUE);

            // mapping int to hotel id
            int pLen = foundIds.size();
            JsonArray ret = new JsonArray();
            for (int i = 0; i < pLen; i++) {
                int id = foundIds.get(i);
                String hId = geoList.get(id).getAsJsonPrimitive("_id").getAsString();
                ret.add(hId);
            }
				
            /*
            JsonArray profiles = new JsonArray();
            for (int i = 0; i < len; i++) {
                String id = ids.get(i).getAsString();
                JsonObject profObj = new JsonParser().parse(new InputStreamReader(db.find(id))).getAsJsonObject();
                profiles.add(profObj);
            }
            */
            response.addProperty("Status", "OK");
            response.add("Hotels", ret);
            // response.add("Profiles", profiles);
            return response;
        } catch (Exception e) {
            response.addProperty("Status", "Failed");
            response.addProperty("Reason", e.toString());
            return response;
        }
    }
}
