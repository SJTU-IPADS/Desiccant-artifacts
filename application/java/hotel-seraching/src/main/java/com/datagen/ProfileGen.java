package com.datagen;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.URL;

/**
 * Profile Generator
 */
public class ProfileGen {
    public static void main(String[] args) {
        JsonObject response = new JsonObject();
        try {
            Database db = ClientBuilder.url(new URL("http://localhost:5984"))
                                       .username("whisk_admin")
                                       .password("some_passw0rd")
                                       .build().database("profile", true);
            for (int i = 1; i <= 80; i++) {
                JsonObject geoObj = new JsonObject();
                JsonObject addrObj = new JsonObject();
                geoObj.addProperty("_id", "" + i);
                if (i == 1) {
                    geoObj.addProperty("Name", "Clift Hotel");
                    geoObj.addProperty("PhoneNumber", "(415) 775-4700");
                    geoObj.addProperty("Description", "A 6-minute walk from Union Square and 4 minutes from a Muni Metro station, this luxury hotel designed by Philippe Starck features an artsy furniture collection in the lobby, including work by Salvador Dali.");
                    addrObj.addProperty("StreetNumber", "495");
                    addrObj.addProperty("StreetName", "Geary St");
                    addrObj.addProperty("City", "San Francisco");
                    addrObj.addProperty("State", "CA");
                    addrObj.addProperty("Country", "United States");
                    addrObj.addProperty("PostalCode", "94102");
                    addrObj.addProperty("Lat", new Double(37.7867));
                    addrObj.addProperty("Lon", new Double(-122.4112));
                    geoObj.add("Address", addrObj);
                } else if (i == 2) {
                    geoObj.addProperty("Name", "W San Francisco");
                    geoObj.addProperty("PhoneNumber", "(415) 777-5300");
                    geoObj.addProperty("Description", "Less than a block from the Yerba Buena Center for the Arts, this trendy hotel is a 12-minute walk from Union Square.");
                    addrObj.addProperty("StreetNumber", "181");
                    addrObj.addProperty("StreetName", "3rd St");
                    addrObj.addProperty("City", "San Francisco");
                    addrObj.addProperty("State", "CA");
                    addrObj.addProperty("Country", "United States");
                    addrObj.addProperty("PostalCode", "94103");
                    addrObj.addProperty("Lat", new Double(37.7854));
                    addrObj.addProperty("Lon", new Double(-122.4005));
                    geoObj.add("Address", addrObj);
                } else if (i == 3) {
                    geoObj.addProperty("Name", "Hotel Zetta");
                    geoObj.addProperty("PhoneNumber", "(415) 543-8555");
                    geoObj.addProperty("Description", "A 3-minute walk from the Powell Street cable-car turnaround and BART rail station, this hip hotel 9 minutes from Union Square combines high-tech lodging with artsy touches.");
                    addrObj.addProperty("StreetNumber", "55");
                    addrObj.addProperty("StreetName", "5th St");
                    addrObj.addProperty("City", "San Francisco");
                    addrObj.addProperty("State", "CA");
                    addrObj.addProperty("Country", "United States");
                    addrObj.addProperty("PostalCode", "94103");
                    addrObj.addProperty("Lat", new Double(37.7834));
                    addrObj.addProperty("Lon", new Double(-122.4071));
                    geoObj.add("Address", addrObj);
                } else if (i == 4) {
                    geoObj.addProperty("Name", "Hotel Vitale");
                    geoObj.addProperty("PhoneNumber", "(415) 278-3700");
                    geoObj.addProperty("Description", "This waterfront hotel with Bay Bridge views is 3 blocks from the Financial District and a 4-minute walk from the Ferry Building.");
                    addrObj.addProperty("StreetNumber", "8");
                    addrObj.addProperty("StreetName", "Mission St");
                    addrObj.addProperty("City", "San Francisco");
                    addrObj.addProperty("State", "CA");
                    addrObj.addProperty("Country", "United States");
                    addrObj.addProperty("PostalCode", "94105");
                    addrObj.addProperty("Lat", new Double(37.7936));
                    addrObj.addProperty("Lon", new Double(-122.3930));
                    geoObj.add("Address", addrObj);
                } else if (i == 5) { 
                    geoObj.addProperty("Name", "Phoenix Hotel");
                    geoObj.addProperty("PhoneNumber", "(415) 776-1380");
                    geoObj.addProperty("Description", "Located in the Tenderloin neighborhood, a 10-minute walk from a BART rail station, this retro motor lodge has hosted many rock musicians and other celebrities since the 1950s. It’s a 4-minute walk from the historic Great American Music Hall nightclub.");
                    addrObj.addProperty("StreetNumber", "601");
                    addrObj.addProperty("StreetName", "Eddy St");
                    addrObj.addProperty("City", "San Francisco");
                    addrObj.addProperty("State", "CA");
                    addrObj.addProperty("Country", "United States");
                    addrObj.addProperty("PostalCode", "94109");
                    addrObj.addProperty("Lat", new Double(37.7831));
                    addrObj.addProperty("Lon", new Double(-122.4181));
                    geoObj.add("Address", addrObj);
                } else if (i == 6) {
                    geoObj.addProperty("Name", "St. Regis San Francisco");
                    geoObj.addProperty("PhoneNumber", "(415) 284-4000");
                    geoObj.addProperty("Description", "St. Regis Museum Tower is a 42-story, 484 ft skyscraper in the South of Market district of San Francisco, California, adjacent to Yerba Buena Gardens, Moscone Center, PacBell Building and the San Francisco Museum of Modern Art.");
                    addrObj.addProperty("StreetNumber", "125");
                    addrObj.addProperty("StreetName", "3rd St");
                    addrObj.addProperty("City", "San Francisco");
                    addrObj.addProperty("State", "CA");
                    addrObj.addProperty("Country", "United States");
                    addrObj.addProperty("PostalCode", "94109");
                    addrObj.addProperty("Lat", new Double(37.7863));
                    addrObj.addProperty("Lon", new Double(-122.4015));
                    geoObj.add("Address", addrObj);
                } else {
                    String suffix = "" + i;
                    if (suffix.length() < 2) {
                        suffix = "0" + suffix;
                    }
                    String phoneNum = "(415) 284-40" + suffix;
                    geoObj.addProperty("Name", "St. Regis San Francisco");
                    geoObj.addProperty("Description", "St. Regis Museum Tower is a 42-story, 484 ft skyscraper in the South of Market district of San Francisco, California, adjacent to Yerba Buena Gardens, Moscone Center, PacBell Building and the San Francisco Museum of Modern Art.");
                    addrObj.addProperty("StreetNumber", "125");
                    addrObj.addProperty("StreetName", "3rd St");
                    addrObj.addProperty("City", "San Francisco");
                    addrObj.addProperty("State", "CA");
                    addrObj.addProperty("Country", "United States");
                    addrObj.addProperty("PostalCode", "94109");
                    double lat = 37.7835 + i/500.0 * 3;
                    double lon = -122.41 + i/500.0 * 4;
                    geoObj.addProperty("Lat", lat);
                    geoObj.addProperty("Lon", lon);
                    geoObj.add("Address", addrObj);
                }
                
                Response rep = db.save(geoObj);
                int code = rep.getStatusCode();
                if ((code < 200) || (code >= 400)) {
                    System.out.println("code for profile " + i + " : code is: " + code);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
    }
}

