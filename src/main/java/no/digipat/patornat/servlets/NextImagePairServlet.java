package no.digipat.patornat.servlets;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.MongoClient;

import no.digipat.patornat.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.patornat.mongodb.dao.MongoImageDAO;
import no.digipat.patornat.mongodb.models.image.ImageComparison;
import no.digipat.patornat.mongodb.models.image.Image;

/**
 * A servlet for retrieving the pair of images that should be
 * compared by a user.
 * 
 * @author Jon Wallem Anundsen
 *
 */
@WebServlet(urlPatterns="/imagePair")
public class NextImagePairServlet extends HttpServlet {
    
    /**
     * Gets a pair of images for comparison. The response body will contain
     * a JSON object of the form <code>{"pair": [id1, id2]}</code>, where {@code id1}
     * and {@code id2} are longs.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * 
     * @throws ServletException if there are not at least two images in the database
     * @throws IOException if an I/O error occurs. In particular, if an I/O error
     * occurs when connecting to the analysis backend.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoImageDAO imageDao = new MongoImageDAO(client, databaseName);
        List<Image> images = imageDao.getAllImages();
        if (images.size() < 2) {
            throw new ServletException("Not enough images in the database");
        }
        MongoImageComparisonDAO comparisonDao = new MongoImageComparisonDAO(client, databaseName);
        List<ImageComparison> comparisons = comparisonDao.getAllBestImageChoices();
        JSONObject jsonForAnalysisBackend = createRequestJson(images, comparisons);
        URL baseUrl = (URL) context.getAttribute("ANALYSIS_BASE_URL");
        JSONObject analysisResponse;
        try {
            analysisResponse = getAnalysisResponse(baseUrl, jsonForAnalysisBackend);
        } catch (ParseException e) {
            throw new ServletException("Analysis backend returned an invalid response", e);
            // This isn't documented in the Javadoc because it should never happen
        }
        // TODO respond to user
        
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
        return json;
    }
    
    private static JSONObject getAnalysisResponse(URL baseUrl, JSONObject requestBody) throws IOException, ParseException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl, "ranking/suggestpair").openConnection();
        connection.setRequestMethod("POST");
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
        try (Reader responseReader = new InputStreamReader(connection.getInputStream())) {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(responseReader);
        }
    }
    
}
