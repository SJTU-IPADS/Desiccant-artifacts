package org.ipads;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Thumbnail {

    final static float MAX_WIDTH = 250;
    final static float MAX_HEIGHT= 250;

    public static JsonObject main(JsonObject args) {
        JsonObject response = args;
        System.out.println("thumbnail invoked");

        try {
            String imageName = args.get(ImageRecognizationCommons.IMAGE_NAME).getAsString();
            Database db = ClientBuilder.url(new URL(ImageRecognizationCommons.COUCHDB_URL))
                    .username(ImageRecognizationCommons.COUCHDB_USERNAME)
                    .password(ImageRecognizationCommons.COUCHDB_PASSWORD)
                    .build().database(ImageRecognizationCommons.COUCHDB_DBNAME, true);
            InputStream imageStream = db.getAttachment(imageName, imageName);
            FileOutputStream outputStream = new FileOutputStream(imageName);
            IOUtils.copy(imageStream, outputStream);
            JsonObject size = args.getAsJsonObject(ImageRecognizationCommons.EXTRACTED_METADATA)
                    .getAsJsonObject("dimensions");
            int width = size.get("width").getAsInt();
            int height = size.get("height").getAsInt();

            float scalingFactor = Math.min(MAX_HEIGHT/ height, MAX_WIDTH / width);
            width = (int) (width * scalingFactor);
            height = (int) (height * scalingFactor);

            String thumbnailName = "thumbnail-" + imageName;
            ConvertCmd cmd = new ConvertCmd();
            IMOperation op = new IMOperation();
            op.addImage(imageName);
            op.resize(width, height);
            op.addImage(thumbnailName);
            cmd.run(op);

            imageStream = new FileInputStream(thumbnailName);
            JsonObject doc = ImageRecognizationCommons.findJsonObjectFromDb(db, args.get(ImageRecognizationCommons.IMAGE_NAME).getAsString());
            db.saveAttachment(imageStream, thumbnailName,
                    args.get(ImageRecognizationCommons.EXTRACTED_METADATA).getAsJsonObject().get("format").getAsString(),
                    doc.get("_id").getAsString(),
                    doc.get("_rev").getAsString());

            response.addProperty(ImageRecognizationCommons.THUMBNAIL, thumbnailName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public static void main (String args[]) {
        String jsonStr = "{\n" +
                "    \"extractedMetadata\": {\n" +
                "        \"creationTime\": \"2019:10:15 14:03:39\",\n" +
                "        \"dimensions\": {\n" +
                "            \"height\": 3968,\n" +
                "            \"width\": 2976\n" +
                "        },\n" +
                "        \"exifMake\": \"HUAWEI\",\n" +
                "        \"exifModel\": \"ALP-AL00\",\n" +
                "        \"fileSize\": \"2.372MB\",\n" +
                "        \"format\": \"image/jpeg\",\n" +
                "        \"geo\": {\n" +
                "            \"latitude\": {\n" +
                "                \"D\": 31,\n" +
                "                \"Direction\": \"N\",\n" +
                "                \"M\": 1,\n" +
                "                \"S\": 27\n" +
                "            },\n" +
                "            \"longitude\": {\n" +
                "                \"D\": 121,\n" +
                "                \"Direction\": \"E\",\n" +
                "                \"M\": 26,\n" +
                "                \"S\": 15\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"imageName\": \"test.jpg\"\n" +
                "}\n";
        JsonObject jsonArgs = new JsonParser().parse(jsonStr).getAsJsonObject();
        main(jsonArgs);
    }

}
