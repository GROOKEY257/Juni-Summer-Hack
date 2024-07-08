import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GooglePlacesAPIExample {

    public static void main(String[] args) throws IOException {
        // Replace with your API key
        String apiKey = "AIzaSyDWKGj1FdtoxetA3xYe_Ez0wuUiGT37se0";

        // Define the endpoint and parameters for the nearby search
        String endpoint = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
        String location = "28.6139,77.2090";  // Replace with latitude,longitude of your location
        String radius = "5000";  // Search radius in meters
        String type = "tourist_attraction";  // Type of place you are searching for

        // Create the URL for the request
        String urlStr = String.format("%s?location=%s&radius=%s&type=%s&key=%s",
                endpoint, location, radius, type, apiKey);

        // Make the HTTP request
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Read the response into a StringBuilder
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
        }
        rd.close();

        // Parse the JSON response
        JSONObject jsonResponse = new JSONObject(response.toString());

        // Check if the request was successful
        String status = jsonResponse.getString("status");
        if (status.equals("OK")) {
            JSONArray results = jsonResponse.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject place = results.getJSONObject(i);
                String name = place.getString("name");
                JSONObject locationObj = place.getJSONObject("geometry").getJSONObject("location");
                double lat = locationObj.getDouble("lat");
                double lng = locationObj.getDouble("lng");
                System.out.println("Name: " + name + ", Latitude: " + lat + ", Longitude: " + lng);
            }
        } else {
            System.out.println("Error occurred: " + status);
        }
    }
}
