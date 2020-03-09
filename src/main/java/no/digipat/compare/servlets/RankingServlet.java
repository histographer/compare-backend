package no.digipat.compare.servlets;

import com.mongodb.MongoClient;
import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.image.ImageComparison;
import no.digipat.compare.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.compare.mongodb.dao.MongoImageDAO;
import no.digipat.compare.servlets.utils.Analysis;
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
     * Gets a ranking of the images that are stored in the database. The response
     * body will contain a JSON array whose elements are JSON objects of the form
     *
     * <pre>
     * {
     *   "id": id,
     *   "score": score,
     *   "rankings": rankings
     * },
     * </pre>
     *
     * where {@code id} is a long, {@code score} is a float, and {@code rankings} is a long.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     *
     * @throws ServletException if there are not at least two images in the database
     * @throws IOException      if an I/O error occurs. In particular, if an I/O
     *                          error occurs when connecting to the analysis
     *                          backend.
     *
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
        JSONObject analysisResponse;
        JSONArray score;
        try {
            analysisResponse = Analysis.getAnalysisResponse(baseUrl, "ranking/ranking/", jsonForAnalysisBackend);
            List<Map.Entry<Long, Long>> rankings = comparisonDao.getNumberOfComparisonsForEachImage();
            score = analysisResponse.getJSONArray("scores");
            for (int i=0; i < score.length(); i++) {
                JSONObject tempObject = score.getJSONObject(i);
                Long id = tempObject.getLong("id");
                Long ranking = rankings.stream().filter(rank -> rank.getKey().equals(id)).findFirst().get().getValue();
                tempObject.put("rankings", ranking);
            }
        } catch (JSONException | NoSuchElementException e) {
            throw new IOException("Analysis backend returned an invalid response", e);
        }
        response.setContentType("application/json");
        response.getWriter().print(score);
    }





}
