package no.digipat.compare.servlets.utils;

import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.image.ImageComparison;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Analysis {


    public static JSONObject createRequestJson(List<Image> images, List<ImageComparison> comparisons) {
        JSONObject json = new JSONObject();
        List<Long> imageIds = new ArrayList<>();
        for (Image image : images) {
            imageIds.add(image.getImageId());
        }
        json.put("image_ids", imageIds);
        List<JSONObject> jsonComparisons = new ArrayList<>();
        for (ImageComparison comparison : comparisons) {
            JSONObject comparisonJson = new JSONObject();
            JSONObject winnerJson = new JSONObject();
            winnerJson.put("id", comparison.getWinner().getImageId());
            JSONObject loserJson = new JSONObject();
            loserJson.put("id", comparison.getLoser().getImageId());
            comparisonJson.put("winner", winnerJson);
            comparisonJson.put("loser", loserJson);
            jsonComparisons.add(comparisonJson);
        }
        json.put("comparison_data", jsonComparisons);
        return json;
    }
    
    public static JSONObject getAnalysisResponse(URL baseUrl, String prefix, JSONObject requestBody)
            throws IOException, JSONException {
        HttpResponse response = Request.Post(new URL(baseUrl, prefix).toString())
            .setHeader("Accept", "application/json")
            .bodyString(requestBody.toString(), ContentType.create("application/json"))
            .execute().returnResponse();
        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != 200) {
            throw new IOException("Expected response code 200 from analysis backend, but got " + responseCode);
        }
        return new JSONObject(new JSONTokener(response.getEntity().getContent()));
    }
}
