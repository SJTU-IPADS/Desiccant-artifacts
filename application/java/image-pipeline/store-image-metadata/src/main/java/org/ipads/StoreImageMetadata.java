package org.ipads;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URL;

public class StoreImageMetadata {

    public static JsonObject main(JsonObject args) {
        System.out.println("StoreImageMetadata invoked");

        JsonObject originalObj = new JsonObject();
        JsonObject extractedMetadata = args.getAsJsonObject(ImageRecognizationCommons.EXTRACTED_METADATA);
        try {
            Database db = ClientBuilder.url(new URL(ImageRecognizationCommons.COUCHDB_URL))
                    .username(ImageRecognizationCommons.COUCHDB_USERNAME)
                    .password(ImageRecognizationCommons.COUCHDB_PASSWORD)
                    .build().database(ImageRecognizationCommons.COUCHDB_DBNAME, true);

            originalObj = ImageRecognizationCommons.findJsonObjectFromDb(db, args.get(ImageRecognizationCommons.IMAGE_NAME).getAsString());
            originalObj.addProperty("uploadTime", System.currentTimeMillis());
            originalObj.add(" imageFormat", extractedMetadata.get("format"));
            originalObj.add(" dimensions", extractedMetadata.get("dimensions"));
            originalObj.add(" fileSize", extractedMetadata.get("fileSize"));
            originalObj.addProperty(" userID", ImageRecognizationCommons.COUCHDB_USERNAME);
            originalObj.addProperty(" albumID", ImageRecognizationCommons.COUCHDB_DBNAME);


            if (extractedMetadata.has("geo")) {
                originalObj.add("latitude", extractedMetadata.getAsJsonObject("geo").get("latitude"));
                originalObj.add("longtitude", extractedMetadata.getAsJsonObject("geo").get("longitude"));
            }

            if (extractedMetadata.has("exifMake")) {
                originalObj.add("exifMake", extractedMetadata.get("exifMake"));
            }

            if (extractedMetadata.has("exifModel")) {
                originalObj.add("exifModel", extractedMetadata.get("exifModel"));
            }

            if (args.has(ImageRecognizationCommons.THUMBNAIL)) {
                originalObj.add("thumbnail", args.get(ImageRecognizationCommons.THUMBNAIL));
            }
            db.update(originalObj);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return originalObj;

    }

    public static void main(String args[]) {
        JsonObject jsonArgs = new JsonParser().parse("{\n" +
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
                "    \"imageName\": \"test.jpg\",\n" +
                "    \"thumbnail\": \"thumbnail-test.jpg\"\n" +
                "}\n").getAsJsonObject();
        main(jsonArgs);
    }

}
