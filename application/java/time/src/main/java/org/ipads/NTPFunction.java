package org.ipads;
import java.util.Date;
import com.google.gson.JsonObject;

public class NTPFunction {
    public static JsonObject main(JsonObject args) {
        Date objDate = new Date();
        JsonObject response = new JsonObject();
        response.addProperty("date", objDate.toString());
        return response;
    }
}
