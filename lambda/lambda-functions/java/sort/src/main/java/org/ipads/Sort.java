package org.ipads;

import java.util.Arrays;
import java.util.Random;

import com.google.gson.JsonObject;

public class Sort {

    private static final int size = 1024 * 1024;

    private static int run(int seed) {
        int[] array = new int[size];
        // array[Math.abs(seed) % size] = seed;

        // Random generator = new Random();
        // generator.setSeed(seed);
        // for (int i = 0; i < size; i++) {
        // array[i] = generator.nextInt();
        // }
        for (int i = 0; i < size; i++) {
            array[i] = Math.abs(size - i * 7);
        }

        Arrays.sort(array);

        return Arrays.hashCode(array);

    }

    public static JsonObject main(JsonObject args) {
        int[] array = new int[size];
        int hash = 0;
        long time = System.nanoTime();

        if (args.has("seed")) {
            hash = run(args.getAsJsonPrimitive("seed").getAsInt());
        } else {
            hash = run(0);
        }

        long cost = System.nanoTime() - time;
        System.out.println("sort cost " + cost + " ns");
        JsonObject response = new JsonObject();
        response.addProperty("hash", String.format("%d", hash));
        response.addProperty("cost", cost);
        return response;
    }
}
