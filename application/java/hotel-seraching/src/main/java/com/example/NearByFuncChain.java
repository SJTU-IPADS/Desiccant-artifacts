package com.example;
import static com.cloudant.client.api.query.Expression.gt;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.query.QueryBuilder;
import com.cloudant.client.api.query.QueryResult;
import com.cloudant.client.api.query.Selector;
import com.cloudant.client.api.views.AllDocsRequestBuilder;
import com.cloudant.client.api.views.AllDocsRequest;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import gnu.trove.procedure.TIntProcedure;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.sf.jsi.SpatialIndex;
import net.sf.jsi.rtree.RTree;
import net.sf.jsi.Rectangle;
import net.sf.jsi.Point;



/**
 * Given a list of hotel IDs, get their profiles and return
 */
public class NearByFuncChain {
    static CloudantClient client = null;
    public static JsonObject main(JsonObject args) {
        JsonObject response = new JsonObject();
        int dbi = args.get("thread").getAsInt();
        response.addProperty("thread", dbi);
        try {
            // Initialization by getting all objects from geo db
//                client = ClientBuilder.url(new URL("http://lg-4k:5984"))
            if (client == null) {
                client = ClientBuilder.url(new URL(HotelCommon.COUCHDB_URL))
                    .username(HotelCommon.COUCHDB_USERNAME)
                    .password(HotelCommon.COUCHDB_PASSWORD)
                    .build();
            }
            Database db = client.database("geo"+dbi, true);
            // QueryResult<Geo> geos = db.query(new QueryBuilder(gt("Lat", 0)).build(), Geo.class);
            QueryResult<JsonObject> geos = db.query(new QueryBuilder(gt("Lat", 0)).build(), JsonObject.class);
            List<JsonObject> geoList = geos.getDocs();
            // fake get geoList
            // List<JsonObject> geoList = fakeQueryResults(8);

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
            response.add("Ids", ret);
            // response.addProperty("InDate", args.getAsJsonPrimitive("InDate").getAsString());
            // response.addProperty("OutDate", args.getAsJsonPrimitive("OutDate").getAsString());
            // response.add("Profiles", profiles);
            return response;
        } catch (Exception e) {
            response.addProperty("Status", "Failed");
            response.addProperty("Reason", e.toString());
            return response;
        }
    }

    private static List<JsonObject> fakeQueryResults(int sleepMs)  {
        try {
            Thread.sleep(sleepMs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<JsonObject> geoList = new ArrayList<>(30);
        JsonParser parser = new JsonParser();
        geoList.add(parser.parse("{\"_id\":\"1\",\"_rev\":\"1-3fe2b43311af0e0b113cd974d947dcff\",\"Lat\":37.7867,\"Lon\":-122.4112}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"10\",\"_rev\":\"1-f6dc7ab8d7090dd53ebd91853ce8a910\",\"Lat\":37.8435,\"Lon\":-122.33}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"11\",\"_rev\":\"1-928b5495eeafd276f79b71d5d5a9a5f4\",\"Lat\":37.8495,\"Lon\":-122.322}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"12\",\"_rev\":\"1-d3bfe207c5ad9bd2f95f95745453ab8e\",\"Lat\":37.8555,\"Lon\":-122.314}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"13\",\"_rev\":\"1-191466475024238732e3fc47760056f5\",\"Lat\":37.8615,\"Lon\":-122.306}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"14\",\"_rev\":\"1-a9cee11d24e23e81a90552b3e887cf98\",\"Lat\":37.8675,\"Lon\":-122.298}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"15\",\"_rev\":\"1-e2453df930f3413838b603ee61a43323\",\"Lat\":37.8735,\"Lon\":-122.28999999999999}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"16\",\"_rev\":\"1-82278e226ee2c2ba30e70ab8cdf58b2d\",\"Lat\":37.87949999999999,\"Lon\":-122.282}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"17\",\"_rev\":\"1-bf7de0bbf40f4483277bd412ca9473a0\",\"Lat\":37.88549999999999,\"Lon\":-122.274}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"18\",\"_rev\":\"1-eaaa24b054838e9621b44a16721769b6\",\"Lat\":37.89149999999999,\"Lon\":-122.26599999999999}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"19\",\"_rev\":\"1-a072eaefe72c376ed8a81c3841f1e2e9\",\"Lat\":37.897499999999994,\"Lon\":-122.258}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"2\",\"_rev\":\"1-ccbcab11a46da66fabdab04bae69179f\",\"Lat\":37.7854,\"Lon\":-122.4005}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"20\",\"_rev\":\"1-1f0ad720639195fe84081a6fe2cc7647\",\"Lat\":37.903499999999994,\"Lon\":-122.25}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"21\",\"_rev\":\"1-4f5574c6df9460f2949979c8c21839e9\",\"Lat\":37.909499999999994,\"Lon\":-122.24199999999999}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"22\",\"_rev\":\"1-f68d12a2f9ed96f4bdd2c1b6379a1295\",\"Lat\":37.915499999999994,\"Lon\":-122.234}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"23\",\"_rev\":\"1-8cf8157c56c1f8a2c361504945edc8f3\",\"Lat\":37.921499999999995,\"Lon\":-122.226}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"24\",\"_rev\":\"1-f17c95f8cc619ba5d839c3c9b4e5e4af\",\"Lat\":37.927499999999995,\"Lon\":-122.218}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"25\",\"_rev\":\"1-512fb4c2e9a19410c38e9ddf84c38c79\",\"Lat\":37.933499999999995,\"Lon\":-122.21}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"26\",\"_rev\":\"1-93d3ab160d113081f16a7c595ef08b5f\",\"Lat\":37.939499999999995,\"Lon\":-122.202}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"27\",\"_rev\":\"1-998358d0301810d46d4be67066ba3793\",\"Lat\":37.945499999999996,\"Lon\":-122.194}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"28\",\"_rev\":\"1-4ca859cf78b9540154ce8c7651211f6c\",\"Lat\":37.951499999999996,\"Lon\":-122.18599999999999}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"29\",\"_rev\":\"1-8ad9359056fa1340ff4d83d6c5fb2d40\",\"Lat\":37.957499999999996,\"Lon\":-122.178}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"3\",\"_rev\":\"1-70002c0cb92cec672ec3119a5e3101b1\",\"Lat\":37.7854,\"Lon\":-122.4071}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"30\",\"_rev\":\"1-8c45f743ab984633659860447bd47729\",\"Lat\":37.963499999999996,\"Lon\":-122.17}").getAsJsonObject());
        geoList.add(parser.parse("{\"_id\":\"31\",\"_rev\":\"1-b131b848663a0e7a08a9fd5bd4c9310b\",\"Lat\":37.9695,\"Lon\":-122.16199999999999}").getAsJsonObject());
        return geoList;
    }
}
