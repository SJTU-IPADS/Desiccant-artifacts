package org.ipads;

import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import redis.clients.jedis.Jedis;

public class RedisUtil {
    static String HOST = Config.IP_ADDR;
    static int PORT = 6379;

    public static void RedisUpload(String filename, String srcPath, boolean remote) {
        String base64 = null;
        if (remote) {
            try {
                DownloadFileFromURL(srcPath, filename);
                base64 = EncryptedBase64(filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            base64 = EncryptedBase64(srcPath);
        }
        try (Jedis jedis = new Jedis(HOST, PORT)) {
            jedis.set(filename, base64);
        }
    }

    public static void RedisDownload(String filename) {
        String base64 = null;
        try (Jedis jedis = new Jedis(HOST, PORT)) {
            long downloadtime = System.nanoTime();
            base64 = jedis.get(filename);
            long decryptedtime = System.nanoTime();
            DecryptedBase64(base64, filename);
            System.out.println("redis download " + filename + " " + (decryptedtime - downloadtime) + "ns decrypt"
                    + (System.nanoTime() - decryptedtime) + "ns");
        }
    }

    private static String EncryptedBase64(String srcFile) {
        try {
            byte[] buffer = Files.readAllBytes(Paths.get(srcFile));
            return Base64.getEncoder().encodeToString(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void DecryptedBase64(String base64, String filename) {
        try {
            Files.write(Paths.get(filename), Base64.getDecoder().decode(base64), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void DownloadFileFromURL(String search, String path) throws IOException {

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = new URL(search);
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", USER_AGENT);
            int contentLength = con.getContentLength();
            System.out.println("File contentLength = " + contentLength + " bytes");
            inputStream = con.getInputStream();
            outputStream = new FileOutputStream(path);
            byte[] buffer = new byte[2048];
            int length;
            int downloaded = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                downloaded += length;

            }
        } catch (Exception ex) {
            // Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        outputStream.close();
        inputStream.close();
    }

}
