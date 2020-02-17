package no.digipat.patornat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import no.digipat.patornat.mongodb.dao.Converter;
import no.digipat.patornat.mongodb.dao.MongoBestImageDAO;
import no.digipat.patornat.mongodb.models.BestImage;
import no.digipat.patornat.mongodb.models.Image;
import org.apache.http.conn.routing.RouteInfo;
import org.json.HTTP;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "ChooseBestImage",  urlPatterns = {"/scoring"})
public class ChooseBestImage extends HttpServlet {

    /**
     * The json request looks like this
     * {
     *   "chosen": {
     *     "id": 1,
     *     "comment": "testcomment",
     *     "kjernestruktur": 1,
     *     "cellegrenser": 1,
     *     "kontrastKollagen": 1,
     *     "kontrastBindevev": 1
     *   },
     *   "other": {
     *     "id": 2,
     *     "comment": "testcomment2",
     *     "kjernestruktur": 2,
     *     "cellegrenser": 3,
     *     "kontrastKollagen": 2,
     *     "kontrastBindevev": 3
     *   }
     * }
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Gets the json from the request
        StringBuffer stringBuffer = new StringBuffer();
        String line = "";
        try {
            BufferedReader reader = request.getReader();
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (Exception e){
            throw new IOException("Reading buffer failed: ", e);
        }

        // Parsing the json to objects
        JSONParser parser = new JSONParser();
        try {
            JSONObject bestImageJson=  (JSONObject) parser.parse(stringBuffer.toString());
            BestImage bestImage = Converter.jsonToBestImage(bestImageJson);
            MongoClient client = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
            MongoBestImageDAO bestImageDAO = new MongoBestImageDAO(client);
            bestImageDAO.createBestImage(bestImage);
        } catch (JSONException |  ParseException e) {
            throw new IOException("Error parsing JSON request string", e);
        }
    }
}
