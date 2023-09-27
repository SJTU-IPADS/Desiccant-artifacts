package org.ipads;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.im4java.core.Info;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

public class ExtractImageMetadata {
    public static JsonObject main(JsonObject args) {
        JsonObject response = args;
        System.out.println("ExtractImageMetadata invoked");
        String imageName = args.get(ImageRecognizationCommons.IMAGE_NAME).getAsString();
        try {
            Database db = ClientBuilder.url(new URL(ImageRecognizationCommons.COUCHDB_URL))
                    .username(ImageRecognizationCommons.COUCHDB_USERNAME)
                    .password(ImageRecognizationCommons.COUCHDB_PASSWORD)
                    .build().database(ImageRecognizationCommons.COUCHDB_DBNAME, true);
            InputStream imageStream = db.getAttachment(imageName, imageName);
            FileOutputStream outputStream = new FileOutputStream(imageName);
            IOUtils.copy(imageStream, outputStream);
            Info imageInfo = new Info(imageName, false);
            response.addProperty(ImageRecognizationCommons.IMAGE_NAME, imageName);
            response.add(ImageRecognizationCommons.EXTRACTED_METADATA, new  Gson().toJsonTree(imageInfo).getAsJsonObject().getAsJsonObject("iAttributes"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void main(String args[]) {
        try {
            CloudantClient client = ClientBuilder.url(new URL(ImageRecognizationCommons.COUCHDB_URL))
                    .username(ImageRecognizationCommons.COUCHDB_USERNAME)
                    .password(ImageRecognizationCommons.COUCHDB_PASSWORD)
                    .build();
            Database db = client.database(ImageRecognizationCommons.COUCHDB_DBNAME, true);

            FileInputStream ift = new FileInputStream(args[0]);
            db.saveAttachment(ift, "test.jpg", "image/jpeg", "test.jpg", null); 
	    InputStream imageStream = db.getAttachment("test.jpg", "test.jpg");
	    FileOutputStream outputStream = new FileOutputStream("test-tmp.jpg");
	    IOUtils.copy(imageStream, outputStream);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
