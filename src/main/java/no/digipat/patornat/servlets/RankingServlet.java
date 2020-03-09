package no.digipat.patornat.servlets;

import com.mongodb.MongoClient;
import no.digipat.patornat.models.image.Image;
import no.digipat.patornat.models.image.ImageComparison;
import no.digipat.patornat.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.patornat.mongodb.dao.MongoImageDAO;
import no.digipat.patornat.servlets.utils.Analysis;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A servlet for retrieving a ranking and score for all the images
 *
 *
 * @author Kent Are Torvik
 *
 */
@WebServlet(urlPatterns = "/ranking")
public class RankingServlet extends HttpServlet {

    /**
     * Gets a pair of images for comparison. The response body will contain a JSON
     * array whose elements are two JSON objects of the form
     *
     * <pre>
     * { [
     *    {
     *      "id": id,
     *      "score": score,
     *      "rankings: rankings
     * },
     * </pre>
     *
     * where {@code id} is long, {@code score} is float, and {@code rankings} is int.
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
        JSONObject jsonForAnalysisBackend = Analysis.createRequestJson(images, comparisons);
        URL baseUrl = (URL) context.getAttribute("ANALYSIS_BASE_URL");
        JSONArray responseForUser;
        JSONObject analysisResponse;
        JSONArray score;
        try {
            analysisResponse = Analysis.getAnalysisResponse(baseUrl, "ranking/ranking/", jsonForAnalysisBackend);
            List<Map.Entry<Long, Long>> rankings = comparisonDao.getNumberOfComparisonsForEachImage();

            score = analysisResponse.getJSONArray("scores");
            score.getJSONObject(1).put("test", "test");
            for (int i=0; i < score.length(); i++) {
                JSONObject tempObject = score.getJSONObject(i);
                Long id = tempObject.getLong("id");

                //Long rank = rankings.stream().filter(ranking -> ranking.getKey() == id).findFirst().get().getValue();

                tempObject.put("ranking", new JSONArray(rankings));
            }
                //long id1 = pair.getLong(0), id2 = pair.getLong(1);
            //Image image1 = images.stream().filter(image -> image.getId() == id1).findFirst().get();
            //Image image2 = images.stream().filter(image -> image.getId() == id2).findFirst().get();
        } catch (JSONException | NoSuchElementException e) {
            throw new IOException("Analysis backend returned an invalid response", e);
        }
        response.setContentType("application/json");
        response.getWriter().print(score);
    }





}
