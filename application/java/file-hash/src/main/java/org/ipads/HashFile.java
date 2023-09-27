package org.ipads;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import com.google.gson.JsonObject;

public class HashFile {

    public static String filename = "test.pdf";

    private static String read(String filename) {
        try {
            File f = new File(filename);
            System.out.println(filename + " len " + String.valueOf(f.length()));
            byte[] buffer = new byte[(int) f.length()];
            InputStream stream = new FileInputStream(filename);
            stream.read(buffer);
            stream.close();
            System.out.println(buffer.length);
            return MessageDigest.getInstance("MD5").digest(buffer).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        RedisUtil.RedisUpload(filename,
                "./assets/" + filename,
                false);
        JsonObject object = null;
        JsonObject test = main(object);
        System.out.println(test);
    }

    public static JsonObject main(JsonObject args) {
        String hash = null;
        long downloadTime = System.nanoTime();
        RedisUtil.RedisDownload(filename);
        long hashtime = System.nanoTime();
        System.out.println("download " + (hashtime - downloadTime));
        hash = read("test.pdf");
        long time = System.nanoTime();
        JsonObject response = new JsonObject();
        response.addProperty("hash", hash);
        response.addProperty("downloadtime", hashtime - downloadTime);
        response.addProperty("hashtime", time - hashtime);
        System.out.println("hash " + (time - hashtime));
        return response;
    }

}
