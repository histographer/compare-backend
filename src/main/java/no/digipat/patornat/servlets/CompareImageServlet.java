package no.digipat.patornat.servlets;

import com.mongodb.MongoClient;
import no.digipat.patornat.mongodb.dao.MongoImageComparisonDAO;
import no.digipat.patornat.mongodb.models.image.ImageChoice;
import no.digipat.patornat.mongodb.models.image.ImageComparison;
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

    /**
     * The json request looks like this
     * {
     *   "user": "string",
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
        JSONParser parser = new JSONParser();
        try {
            BufferedReader reader = request.getReader();
            JSONObject imageComparisonJson = (JSONObject) parser.parse(reader);
            ImageComparison imageComparison = jsonToImageComparison(imageComparisonJson);
            MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
            MongoImageComparisonDAO comparisonDAO = new MongoImageComparisonDAO(client, (String) context.getAttribute("MONGO_DATABASE"));
            comparisonDAO.createImageComparison(imageComparison);
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private static ImageComparison jsonToImageComparison(JSONObject json) {
        ImageChoice chosen = jsonToImageChoice((JSONObject) json.get("chosen"));
        ImageChoice other = jsonToImageChoice((JSONObject) json.get("other"));
        String user = (String) json.get("user");
        return new ImageComparison(user, chosen, other);
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
