package no.digipat.compare.servlets;

import com.mongodb.MongoClient;

import no.digipat.compare.models.image.ImageChoice;
import no.digipat.compare.models.image.ImageComparison;
import no.digipat.compare.mongodb.dao.MongoImageComparisonDAO;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet for comparing two images.
 * 
 * @author Kent Are Torvik
 *
 */
@WebServlet("/scoring")
public class CompareImageServlet extends HttpServlet {
    
    /**
     * Submits a comparison of two images. The request body looks like this:
     * <pre>
     * {
     *   projectId: 3,
     *   "chosen": {
     *     "id": 1,
     *     "comment": "testcomment",
     *   },
     *   "other": {
     *     "id": 2,
     *     "comment": "testcomment2",
     *   }
     * }
     * </pre>
     * 
     * @param request
     * @param response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        String servletSessionID = request.getSession().getId();
        try {
            JSONObject imageComparisonJson = new JSONObject(
                    IOUtils.toString(request.getInputStream(), request.getCharacterEncoding()));
            ImageComparison imageComparison = jsonToImageComparison(imageComparisonJson,
                    servletSessionID);
            MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
            MongoImageComparisonDAO comparisonDAO = new MongoImageComparisonDAO(client,
                    (String) context.getAttribute("MONGO_DATABASE"));
            comparisonDAO.createImageComparison(imageComparison);
        } catch (JSONException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
    
    private static ImageComparison jsonToImageComparison(JSONObject json, String sessionID)
            throws JSONException {
        ImageChoice winner = jsonToImageChoice(json.getJSONObject("chosen"));
        ImageChoice loser = jsonToImageChoice(json.getJSONObject("other"));
        long projectId = json.getLong("projectId");
        return new ImageComparison()
                .setSessionId(sessionID)
                .setWinner(winner)
                .setLoser(loser)
                .setProjectId(projectId);
    }
    
    private static ImageChoice jsonToImageChoice(JSONObject json) throws JSONException {
        long id = json.getLong("id");
        String comment = json.has("comment") ? json.getString("comment") : null;
        return new ImageChoice(id, comment);
    }
    
}
