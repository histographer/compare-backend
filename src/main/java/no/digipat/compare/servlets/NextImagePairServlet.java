package no.digipat.compare.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.mongodb.MongoClient;

import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.image.ImageComparison;
import no.digipat.compare.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.compare.mongodb.dao.MongoImageDAO;

/**
 * A servlet for retrieving the pair of images that should be compared by a
 * user.
 * 
 * @author Jon Wallem Anundsen
 *
 */
@WebServlet(urlPatterns = "/imagePair")
public class NextImagePairServlet extends HttpServlet {

    /**
     * Gets a pair of images for comparison. The response body will contain a JSON
     * array whose elements are two JSON objects of the form
     * 
     * <pre>
     * {
     *   "id": id,
     *   "width": width,
     *   "height": height,
     *   "depth": depth,
     *   "magnification": magnification,
     *   "resolution": resolution,
     *   "mime": mime,
     *   "imageServerURLs": [url1, url2, ..., urlN]
     * },
     * </pre>
     * 
     * where {@code id}, {@code width}, {@code height}, {@code depth}, and
     * {@code magnification} are longs, {@code resolution} is a double, {@code mime}
     * is a string, and {@code url1, url2, ..., urlN} are strings.
     * 
     * @param request  the HTTP request
     * @param response the HTTP response
     * 
     * @throws ServletException if there are not at least two images in the database
     * @throws IOException      if an I/O error occurs. In particular, if an I/O
     *                          error occurs when connecting to the analysis
     *                          backend.
     * 
     * @see Image
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext context = getServletContext();
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoImageDAO imageDao = new MongoImageDAO(client, databaseName);
        List<Image> images = imageDao.getAllImages();
        if (images.size() < 2) {
            throw new ServletException("Not enough images in the database");
        }
        MongoImageComparisonDAO comparisonDao = new MongoImageComparisonDAO(client, databaseName);
        List<ImageComparison> comparisons = comparisonDao.getAllImageComparisons();
        JSONObject jsonForAnalysisBackend = createRequestJson(images, comparisons);
        URL baseUrl = (URL) context.getAttribute("ANALYSIS_BASE_URL");
        JSONArray responseForUser;
        try {
            JSONObject analysisResponse = getAnalysisResponse(baseUrl, jsonForAnalysisBackend);
            JSONArray pair = analysisResponse.getJSONArray("pair");
            long id1 = pair.getLong(0), id2 = pair.getLong(1);
            Image image1 = images.stream().filter(image -> image.getId() == id1).findFirst().get();
            Image image2 = images.stream().filter(image -> image.getId() == id2).findFirst().get();
            responseForUser = createResponseJson(image1, image2);
        } catch (JSONException | NoSuchElementException e) {
            throw new IOException("Analysis backend returned an invalid response", e);
        }
        response.setContentType("application/json");
        response.getWriter().print(responseForUser);
    }

    private static JSONObject createRequestJson(List<Image> images, List<ImageComparison> comparisons) {
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

    private static JSONObject getAnalysisResponse(URL baseUrl, JSONObject requestBody)
            throws IOException, JSONException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl, "ranking/suggestpair/").openConnection();
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

    private static JSONArray createResponseJson(Image image1, Image image2) {
        JSONArray returnJson = new JSONArray();
        for (Image image : new Image[] { image1, image2 }) {
            JSONObject imageJson = new JSONObject();
            imageJson.put("id", image.getId());
            imageJson.put("width", image.getWidth());
            imageJson.put("height", image.getHeight());
            imageJson.put("depth", image.getDepth());
            imageJson.put("magnification", image.getMagnification());
            imageJson.put("resolution", image.getResolution());
            imageJson.put("mime", image.getMimeType());
            imageJson.put("imageServerURLs", image.getImageServerURLs());
            returnJson.put(imageJson);
        }
        return returnJson;
    }

}
