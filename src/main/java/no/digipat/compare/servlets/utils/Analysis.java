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

/**
 * Utility methods for communicating with the analysis API.
 * 
 * @author Kent Are Torvik
 * @author Jon Wallem Anundsen
 *
 */
public class Analysis {
    
    /**
     * Creates a JSON object of the following form:
     * 
     * <pre>
     * {
     *   "image_ids": [123, 1337, 99999, ...],
     *   "comparison_data": [
     *     {
     *       "winner": {
     *         "id": 123
     *       },
     *       "loser": {
     *         "id": 1337
     *       }
     *     },
     *     ...
     *   ]
     * }
     * </pre>
     * 
     * @param images the images
     * @param comparisons the comparisons
     * 
     * @return the JSON object
     */
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
    
    /**
     * Sends a POST request to the analysis API with a JSON object
     * as the body, returning the response as a JSON object.
     * 
     * @param baseUrl the base URL of the analysis API, e.g. {@code http://example.com/api}.
     * @param path the relative path of the request
     * @param requestBody the JSON object to include in the request body
     * 
     * @return the JSON object from the response body
     * 
     * @throws IOException if an I/O error occurs. In particular, if
     * the analysis API returns an unacceptable status code.
     * @throws JSONException if the response body is not a valid JSON object
     */
    public static JSONObject getAnalysisPostResponse(URL baseUrl, String path, JSONObject requestBody)
            throws IOException, JSONException {
        HttpResponse response = Request.Post(new URL(baseUrl, path).toString())
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
