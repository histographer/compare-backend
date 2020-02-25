package no.digipat.patornat.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.mongodb.MongoClient;

import no.digipat.patornat.mongodb.dao.MongoBestImageDAO;
import no.digipat.patornat.mongodb.dao.MongoImageDAO;
import no.digipat.patornat.mongodb.models.image.BestImageChoice;
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
     * and {@code id2} are integers.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * 
     * @throws ServletException if there are not at least two images in the database
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject jsonForAnalysisBackend = new JSONObject();
        ServletContext context = getServletContext();
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoImageDAO imageDao = new MongoImageDAO(client, databaseName);
        List<Image> images = imageDao.getAllImages();
        if (images.size() < 2) {
            throw new ServletException("Not enough images in the database");
        }
        List<Integer> imageIds = new ArrayList<>();
        for (Image image : images) {
            imageIds.add(image.getId());
        }
        jsonForAnalysisBackend.put("image_ids", imageIds);
        MongoBestImageDAO comparisonDao = new MongoBestImageDAO(client, databaseName);
        List<BestImageChoice> comparisons = comparisonDao.getAllBestImageChoices();
        List<JSONObject> jsonComparisons = new ArrayList<>();
        for (BestImageChoice comparison : comparisons) {
            JSONObject comparisonJson = new JSONObject();
            JSONObject winnerJson = new JSONObject();
            winnerJson.put("id", comparison.getChosen().getId());
            JSONObject loserJson = new JSONObject();
            loserJson.put("id", comparison.getOther().getId());
            comparisonJson.put("winner", winnerJson);
            comparisonJson.put("loser", loserJson);
            jsonComparisons.add(comparisonJson);
        }
        jsonForAnalysisBackend.put("comparison_data", jsonComparisons);
        String requestBody = jsonForAnalysisBackend.toString();
        // TODO send request to analysis backend and send response to user
        
    }
    
}
