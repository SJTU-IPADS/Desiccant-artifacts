package org.ipads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

class Global {
    public static int num_mapper = 3;
    public static String filename = "text";
    //public static ConcurrentHashMap<String, Integer>[] g_map_list = new ConcurrentHashMap[num_mapper];
    public static SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:SSS");
}

class ThreadMapper extends Thread {
    private int mapper_id;
    public JsonObject results;
    public String suffix;

    ThreadMapper(int id, String suffix) {
        mapper_id = id;
        //results = new ConcurrentHashMap<String, Integer>();
        results = new JsonObject();
        this.suffix = suffix;
        // System.out.println("Creating " + threadName +
        // Global.df.format(System.currentTimeMillis()));
    }

    public void run() {
        // System.out.println("Running " + threadName +
        // Global.df.format(System.currentTimeMillis()));
        long time = System.nanoTime();

        // try {
        // Thread.sleep(10000);
        // } catch (InterruptedException e1) {
        // e1.printStackTrace();
        // }

        //Global.g_map_list[mapper_id - 1] = new ConcurrentHashMap<String, Integer>();
        String map_file = "/tmp/text" + this.suffix + "-" + String.valueOf(mapper_id);
        File filename = new File(map_file);
        if (!filename.exists()) {
            System.out.println("cannot find file " + filename);
            return;
        }
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader buffer = new BufferedReader(reader);
            String line = null;
            while ((line = buffer.readLine()) != null) {
                String regExp = "[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~!@#￥%……&*（）——+|{}【】。，、？]";
                String[] words = line.replaceAll(regExp, "").split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    int count = results.has(words[i])
                            ? results.get(words[i]).getAsInt()
                            : 0;
                    results.addProperty(words[i], count + 1);
                }
            }
            buffer.close();
            reader.close();
            //System.out.println("Thread " + mapper_id + " size " + results.size() + " cost "
                    //+ (System.nanoTime() - time) + " ns");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Mapper {

    public static void main(String[] args) {
        //        "/home/cxj/Desktop/program/wsk-test-app/mapreduce/text", false);
        RedisUtil.RedisUpload(Global.filename,
                "./assets/text", false);
        //JsonObject object = null;
        //JsonObject test = main(object);
        //System.out.println(test);
    }

    public static JsonObject main2(JsonObject args) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:SSS");
        //System.out.println(df.format(System.currentTimeMillis()));
        //long time = System.nanoTime();
        long t0 = System.nanoTime();
        JsonArray jsonMapArray = args.getAsJsonArray("map");
        int numMapper = jsonMapArray.size();
//        Gson gson = new Gson();
//        Type listType = new TypeToken<ConcurrentHashMap<String, Integer>[]>() {
//        }.getType();
//        ConcurrentHashMap<String, Integer>[] map_list = gson.fromJson(json_map_array, listType);
        long t1 = System.nanoTime();
        //long transfer_time = System.nanoTime();
        // System.out.println("transfer " + (transfer_time - time) + " ns");
        ConcurrentHashMap<String, Integer> word_count = new ConcurrentHashMap<String, Integer>();
        for (int i = 0; i < numMapper; i++) {
            //System.out.println("map " + i + " size " + map_list[i].size());
            for (Map.Entry<String, JsonElement> entry : jsonMapArray.get(i).getAsJsonObject().entrySet()) {
                int count = word_count.containsKey(entry.getKey())
                        ? word_count.get(entry.getKey()) + entry.getValue().getAsInt()
                        : entry.getValue().getAsInt();
                word_count.put(entry.getKey(), count);
            }
        }
        long t2 = System.nanoTime();
        // Gson gsonbuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'
        // 'HH:mm:ss").create();
        // com.google.gson.JsonElement jsonElement = gsonbuilder.toJsonTree(word_count);
        JsonObject response = new JsonObject();
        JsonArray jarray = new JsonArray();
        long reduce_time = System.nanoTime() - t1;
        //System.out.println("reduce " + reduce_time + " ns");
        jarray.add(String.valueOf(reduce_time));
        response.add("reduce", jarray);
        long t3 = System.nanoTime();
        String s = String.format("%d, %d, %d", t1 - t0, t2 - t1, t3 - t2);
        response.addProperty("reduceTimes", s);
        response.add("mapTimes", args.get("mapTimes"));



        return response;
    }


    public static JsonObject main(JsonObject args) {
        long t0 = System.nanoTime();
        //String suffix = String.valueOf(t0);
        String suffix = "aaa";
        RedisUtil.RedisDownload("text", suffix);
        long t1 = System.nanoTime();
        try {
            FileUtil.Split("text" + suffix, 3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long t2 = System.nanoTime();
        //long thread_time = System.nanoTime();
        // String suffix = String.valueOf(thread_time);
        List<ThreadMapper> threadSet = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            ThreadMapper thread = new ThreadMapper(i, suffix);
            thread.start();
            threadSet.add(thread);
        }

        //ConcurrentHashMap<String, Integer>[] g_map_list = new ConcurrentHashMap[3];
        JsonArray mapList = new JsonArray(3);
        int curPos = 0;

        for (ThreadMapper thread : threadSet) {
            try {
                thread.join();
                mapList.add(thread.results);
                //mapList.set(curPos, thread.results);
                curPos += 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long t3 = System.nanoTime();
        //System.out.println("thread cost " + (System.nanoTime() - thread_time) + " ns");
        //long time = System.nanoTime();
        //Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        


        JsonElement jsonElement = mapList;

        JsonObject response = new JsonObject();
        response.add("map", jsonElement);
        long t4 = System.nanoTime();
        String s = String.format("%d, %d, %d, %d", t1 - t0, t2 - t1, t3 - t2, t4 - t3);
        //long[] tmp = {t1 - t0, t2 - t1, t3 - t2, t4 - t3};
        response.addProperty("mapTimes", s);

        //System.out.println("transfer cost " + (System.nanoTime() - time) + " ns");
        //System.out.println(Global.df.format(System.currentTimeMillis()));

        //return main2(response);
        return response;

        //return response;
    }

}
