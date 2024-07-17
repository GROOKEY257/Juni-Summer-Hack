import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class GooglePlacesAPIExample {

    private static final String API_KEY = "AIzaSyDWKGj1FdtoxetA3xYe_Ez0wuUiGT37se0";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String GEOCODE_API_BASE = "https://maps.googleapis.com/maps/api/geocode";
    private static final String TYPE_NEARBY_SEARCH = "/nearbysearch";
    private static final String OUT_JSON = "/json";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a place name (e.g., New York City): ");
        String placeName = scanner.nextLine();

        // Get coordinates (latitude, longitude) for the entered place name
        String location = getCoordinates(placeName);
        if (location == null) {
            System.out.println("Place not found. Please enter a valid place name.");
            return;
        }

        int radius = 5000; // Radius in meters
        String[] types = {"tourist_attraction"};

        String urlString = PLACES_API_BASE + TYPE_NEARBY_SEARCH + OUT_JSON +
                "?location=" + URLEncoder.encode(location, "UTF-8") +
                "&radius=" + radius +
                "&types=" + String.join("|", types) +
                "&key=" + API_KEY;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        conn.disconnect();

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray results = jsonResponse.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject place = results.getJSONObject(i);
            String placeId = place.getString("place_id");
            JSONObject placeDetails = getPlaceDetails(placeId);

            if (placeDetails != null) {
                System.out.println("Name: " + place.getString("name"));
                System.out.println("Address: " + placeDetails.getString("formatted_address"));
                System.out.println("Description: " + getPlaceDescription(placeDetails));
                System.out.println();
            }
        }
    }

    private static String getCoordinates(String placeName) throws IOException {
        String urlString = GEOCODE_API_BASE + OUT_JSON +
                "?address=" + URLEncoder.encode(placeName, "UTF-8") +
                "&key=" + API_KEY;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        conn.disconnect();

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray results = jsonResponse.getJSONArray("results");

        if (results.length() > 0) {
            JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");
            return lat + "," + lng;
        }

        return null;
    }

    private static JSONObject getPlaceDetails(String placeId) throws IOException {
        String urlString = PLACES_API_BASE + "/details" + OUT_JSON +
                "?place_id=" + placeId +
                "&fields=name,formatted_address,formatted_phone_number,website,rating,reviews" +
                "&key=" + API_KEY;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        conn.disconnect();

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONObject result = jsonResponse.getJSONObject("result");

        return result;
    }

    private static String getPlaceDescription(JSONObject placeDetails) {
        StringBuilder description = new StringBuilder();

        // Format details into a description string
        if (placeDetails.has("formatted_phone_number")) {
            description.append("Phone: ").append(placeDetails.getString("formatted_phone_number")).append("\n");
        }
        if (placeDetails.has("website")) {
            description.append("Website: ").append(placeDetails.getString("website")).append("\n");
        }
        if (placeDetails.has("rating")) {
            description.append("Rating: ").append(placeDetails.getDouble("rating")).append("\n");
        }
        if (placeDetails.has("reviews")) {
            JSONArray reviews = placeDetails.getJSONArray("reviews");
            if (reviews.length() > 0) {
                JSONObject review = reviews.getJSONObject(0); // Get the first review for brevity
                description.append("Review: ").append(review.getString("text")).append("\n");
            }
        }

        return description.toString();
    }
}
