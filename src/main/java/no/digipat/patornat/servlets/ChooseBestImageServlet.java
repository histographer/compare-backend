package no.digipat.patornat.servlets;

import com.mongodb.MongoClient;
import no.digipat.patornat.mongodb.dao.Converter;
import no.digipat.patornat.mongodb.dao.MongoBestImageDAO;
import no.digipat.patornat.mongodb.models.image.BestImageChoice;
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

@WebServlet(name = "ChooseBestImage",  urlPatterns = {"/scoring"})
public class ChooseBestImageServlet extends HttpServlet {

    /**
     * The json request looks like this
     * {
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
            JSONObject bestImageJson = (JSONObject) parser.parse(reader);
            BestImageChoice bestImageChoice = Converter.jsonToBestImageChoice(bestImageJson);
            MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
            MongoBestImageDAO bestImageDAO = new MongoBestImageDAO(client, (String) context.getAttribute("MONGO_DATABASE"));
            bestImageDAO.createBestImage(bestImageChoice);
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
