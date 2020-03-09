package no.digipat.compare.servlets.utils;

import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.image.ImageComparison;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Analysis {


    public static JSONObject createRequestJson(List<Image> images, List<ImageComparison> comparisons) {
        JSONObject json = new JSONObject();
        List<Long> imageIds = new ArrayList<>();
        for (Image image : images) {
            imageIds.add(image.getId());
        }
        json.put("image_ids", imageIds);
        List<JSONObject> jsonComparisons = new ArrayList<>();
        for (ImageComparison comparison : comparisons) {
            JSONObject comparisonJson = new JSONObject();
            JSONObject winnerJson = new JSONObject();
            winnerJson.put("id", comparison.getChosen().getId());
            JSONObject loserJson = new JSONObject();
            loserJson.put("id", comparison.getOther().getId());
            comparisonJson.put("winner", winnerJson);
            comparisonJson.put("loser", loserJson);
            jsonComparisons.add(comparisonJson);
        }
        json.put("comparison_data", jsonComparisons);
        System.out.println(json.toString());
        return json;
    }

    public static JSONObject getAnalysisResponse(URL baseUrl, String prefix, JSONObject requestBody)
            throws IOException, JSONException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl, prefix).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        try (PrintWriter writer = new PrintWriter(connection.getOutputStream())) {
            writer.print(requestBody);
            writer.flush();
        }
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Expected response code 200 from analysis backend, but got " + responseCode);
        }
        try (InputStream inputStream = connection.getInputStream()) {
            return new JSONObject(new JSONTokener(inputStream));
        }
    }
}
