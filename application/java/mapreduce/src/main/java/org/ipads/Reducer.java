package org.ipads;

import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonArray;

public class Reducer {
    public static JsonObject main(JsonObject args) {
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

}
