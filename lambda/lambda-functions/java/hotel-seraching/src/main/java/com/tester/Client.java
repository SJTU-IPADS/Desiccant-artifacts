package com.tester;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import okhttp3.*;

import javax.net.ssl.*;

public class Client {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null,  new TrustManager[] { new TrustAllCerts() }, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    // private String username = "whisk_name";
    // private String password = "whisk_pwd";
    public String ip = "";

    private final Authenticator authenticator = new Authenticator() {
        @Override
        public Request authenticate(Route route, Response response) throws IOException {
            if (response.request().header("Authorization") != null) {
                return null; // Give up, we've already attempted to authenticate.
            }

            // System.out.println("Authenticating for response: " + response);
            // System.out.println("Challenges: " + response.challenges());
            String username = "23bc46b1-71f6-4ed5-8c54-816aa4f8c502";
            String password = "123zO3xZCLrMN6v2BKK1dXYFpXlPkccOFqm12CdAsMgRU4VrNZ9lyGVCGuMDGIwP";
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder()
                    .header("Authorization", credential)
                    .build();
        }
    };

    String post(String url, String json) throws Exception {
        System.out.println("URL: " + url + "\nJSON: " + json);
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.sslSocketFactory(createSSLSocketFactory());
        mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
        OkHttpClient client = mBuilder.connectTimeout(60000, TimeUnit.MILLISECONDS)
                                      .authenticator(authenticator).build();

//        OkHttpClient client = new OkHttpClient
//                .Builder()
//                .authenticator(authenticator)
//                .build();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public void search() throws Exception {
        Random r = new Random();
        r.setSeed(System.nanoTime());
        double lat = 38.0235 + (r.nextInt(481) - 240.5)/1000.0;
        double lon = -122.095 + (r.nextInt(325) - 157.0)/1000.0;
        String json = String.format("{\"Lat\":%f, \"Lon\":%f}", lat, lon); 
        String response = post("https://" + ip + "/api/v1/namespaces/_/actions/searchnearbysequence?blocking=true", json);
        System.out.println(response);
    }

    public void recommend() throws Exception {
        Random r = new Random();
        r.setSeed(System.nanoTime());
        double coin = r.nextDouble();
        String option = null;
        if (coin < 0.33) {
            option = "dis"; 
        } else if (coin < 0.66) {
            option = "rate";
        } else {
            option = "price";
        }
        double lat = 38.0235 + (r.nextInt(481) - 240.5)/1000.0;
        double lon = -122.095 + (r.nextInt(325) - 157.0)/1000.0;
        String json = String.format("{\"Require\":\"%s\", \"Lat\":%f, \"Lon\":%f}", option, lat, lon); 
        String response = post("https://" + ip + "/api/v1/namespaces/_/actions/recommendation?blocking=true", json);
        System.out.println(response);
    }

    public void login() throws Exception { 
        Random r = new Random();
        r.setSeed(System.nanoTime());
        int id = r.nextInt(500);
        String username = "SJTU_" + id;
        String password = "sjtuuser" + id;
        String json = String.format("{\"Username\": \"%s\", \"Password\": \"%s\"}", username, password);
        String response = post("https://" + ip + "/api/v1/namespaces/_/actions/checkuser?blocking=true", json);
        System.out.println(response);
    }

    public void reserve() throws Exception { 
        Random r = new Random();
        r.setSeed(System.nanoTime());
        int start = r.nextInt(2) + 26;
        int end = r.nextInt(2) + 28;
        int hotelId = r.nextInt(80) + 1;
        int id = r.nextInt(500);
        String username = "SJTU_" + id;
        String inDate = "2020-02-" + start;
        String outDate = "2020-02-" + end;
        String json = String.format("{\"InDate\": \"%s\", \"OutDate\": \"%s\", \"HotelId\": \"%s\", \"Number\": 1, \"CustomerName\": \"%s\"}", inDate, outDate, "" + hotelId, username);
        String response = post("https://" + ip + "/api/v1/namespaces/_/actions/reservation?blocking=true", json);
        System.out.println(response);
    }

    public static void main(String[] args) throws Exception {
        double search_ratio = 0.6;
        double recommend_ratio = 0.39;
        double user_ratio = 0.005;
        double reserve_ratio = 0.005;
        Client client = new Client();
        Random r = new Random();
        r.setSeed(System.nanoTime());
        int times = Integer.parseInt(args[0]);
        client.ip = args[1];
        for (int i = 0; i < times; i++) {
            double value = r.nextDouble();
            long beginTime = System.nanoTime();
            if (value < search_ratio) {
                client.search();
            } else if (value < search_ratio + recommend_ratio) {
                client.recommend();
                // client.search();
            } else if (value < 1 - reserve_ratio) {
                client.login();
                // client.search();
            } else {
                client.reserve();
                // client.search();
            }
            long endTime = System.nanoTime();
            double duration = (endTime - beginTime)/1000000.0;
            System.out.println("The execution time: " + duration + " ms");
        }
    }
}

