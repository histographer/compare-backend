package no.digipat.compare.servlets.utils;

import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.image.ImageComparison;

import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;

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
     * as the body, returning the response.
     * 
     * @param baseUrl the base URL of the analysis API, e.g. {@code http://example.com/api}.
     * @param path the relative path of the request
     * @param requestBody the JSON object to include in the request body
     * @param acceptableStatusCodes the status codes that it is acceptable for
     * the analysis API to return. If empty, only the code 200 is acceptable.
     * 
     * @return the response
     * 
     * @throws IOException if an I/O error occurs. In particular, if
     * the analysis API returns an unacceptable status code.
     */
    public static HttpResponse getAnalysisPostResponse(URL baseUrl, String path, JSONObject requestBody,
            int... acceptableStatusCodes) throws IOException {
        HttpResponse response = Request.Post(new URL(baseUrl, path).toString())
            .setHeader("Accept", "application/json")
            .bodyString(requestBody.toString(), ContentType.create("application/json"))
            .execute().returnResponse();
        if (acceptableStatusCodes.length == 0) {
            acceptableStatusCodes = new int[] {200};
        }
        int responseCode = response.getStatusLine().getStatusCode();
        if (!ArrayUtils.contains(acceptableStatusCodes, responseCode)) {
            throw new IOException("Analysis API returned unacceptable status code: " + responseCode);
        }
        return response;
    }
}
