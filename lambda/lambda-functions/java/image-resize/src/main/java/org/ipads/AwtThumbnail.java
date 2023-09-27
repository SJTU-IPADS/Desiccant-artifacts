package org.ipads;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import java.lang.reflect.Field;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import java.util.*;

public class AwtThumbnail {
public static String IP_ADDR = "172.31.16.227";
    public static final String COUCHDB_USERNAME = "whisk_admin";
    public static final String COUCHDB_PASSWORD = "some_passw0rd";
    public static final String COUCHDB_DBNAME = "images";
    public static String COUCHDB_URL = "http://" +  IP_ADDR + ":5984";
    public static final String IMAGE_NAME = "imageName";

    public static Database db = null;


    public static JsonObject main(JsonObject args) {
      JsonObject response = new JsonObject();
      try {
          String imageName = args.get(IMAGE_NAME).getAsString();
	  System.out.println("imageName is "+imageName);
          if (db == null) {
              db = ClientBuilder.url(new URL(COUCHDB_URL))
                  .username(COUCHDB_USERNAME)
                  .password(COUCHDB_PASSWORD)
                  .build().database(COUCHDB_DBNAME, true);
          }
	  System.out.println("test attach img");
          InputStream imageStream = db.getAttachment(imageName, imageName);
          String tmpFilePrefix = String.valueOf(System.nanoTime());
	  System.out.println("get image at "+tmpFilePrefix);
          FileOutputStream outputStream = new FileOutputStream("/tmp/"+imageName);
          IOUtils.copy(imageStream, outputStream);

          // String tmpFilePrefix = String.valueOf(System.nanoTime());
          //Thread.sleep(9);

          BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
          BufferedImage temp = ImageIO.read(new File("/tmp/" + imageName));
	  System.out.println("Create img "+"/tmp/t-" + tmpFilePrefix + imageName);
          img.createGraphics().drawImage(temp.getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH), 0, 0, null);
          ImageIO.write(img, "jpg", new File("/tmp/t-" + tmpFilePrefix + imageName));
          response.addProperty("thumb", "ok");
      } catch (Exception e) {
          e.printStackTrace();
          response.addProperty("thumb", "fail");
      }
      return response;
    }

    public static void main(String args[]) {
        try {
            CloudantClient client = ClientBuilder.url(new URL(COUCHDB_URL))
                    .username(COUCHDB_USERNAME)
                    .password(COUCHDB_PASSWORD)
                    .build();
            Database db = client.database("images", true);

            FileInputStream ift = new FileInputStream(args[0]);
            db.saveAttachment(ift, "test.jpg", "image/jpg", "test.jpg", null); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
