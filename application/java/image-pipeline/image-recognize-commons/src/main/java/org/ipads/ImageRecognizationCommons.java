package org.ipads;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
public class ImageRecognizationCommons {
public static String IP_ADDR = "192.168.22.156";
    public static int PORT = 5984;
    public static String COUCHDB_URL = "http://" +  IP_ADDR + ":" + PORT;
    public static String COUCHDB_USERNAME = "whisk_admin";
    public static String COUCHDB_PASSWORD = "some_passw0rd";
    public static String COUCHDB_DBNAME = "images";
    public static final String IMAGE_NAME = "imageName";
    public static final String EXTRACTED_METADATA = "extractedMetadata";
    public static final String THUMBNAIL = "thumbnail";
    public static JsonObject findJsonObjectFromDb(Database db, String id) throws IOException {
           return new JsonParser().parse(new InputStreamReader(db.find(id))).getAsJsonObject();
    }

}
