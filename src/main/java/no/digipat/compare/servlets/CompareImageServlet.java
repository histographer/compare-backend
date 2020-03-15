package no.digipat.compare.servlets;

import com.mongodb.MongoClient;

import no.digipat.compare.models.image.ImageChoice;
import no.digipat.compare.models.image.ImageComparison;
import no.digipat.compare.mongodb.dao.MongoImageComparisonDAO;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "CompareImage",  urlPatterns = {"/scoring"})
public class CompareImageServlet extends HttpServlet {
    // TODO project ID
    
    /**
     * The json request looks like this
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
     * @param request
     * @param response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ServletContext context = request.getServletContext();
        String servletSessionID = request.getSession().getId();
        JSONParser parser = new JSONParser();
        try {
            BufferedReader reader = request.getReader();
            JSONObject imageComparisonJson = (JSONObject) parser.parse(reader);
            ImageComparison imageComparison = jsonToImageComparison(imageComparisonJson, servletSessionID);
            MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
            MongoImageComparisonDAO comparisonDAO = new MongoImageComparisonDAO(client, (String) context.getAttribute("MONGO_DATABASE"));
            comparisonDAO.createImageComparison(imageComparison);
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private static ImageComparison jsonToImageComparison(JSONObject json, String sessionID) {
        ImageChoice winner = jsonToImageChoice((JSONObject) json.get("chosen"));
        ImageChoice loser = jsonToImageChoice((JSONObject) json.get("other"));
        long projectId = (long) json.get("projectId");
        return new ImageComparison().setSessionID(sessionID).setWinner(winner).setLoser(loser).setProjectId(projectId);
    }
    
    private static ImageChoice jsonToImageChoice(JSONObject json) {
        try {
            long id = (Long) json.get("id");
            String comment = (String) json.getOrDefault("comment", "");
            return new ImageChoice(id, comment);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JSON is not valid, id is missing");
        }
    }
    
}
