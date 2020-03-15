package no.digipat.compare.servlets;

import com.mongodb.MongoClient;

import no.digipat.compare.models.session.Session;
import no.digipat.compare.mongodb.dao.MongoSessionDAO;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "no.digipat.compare.servlets.SessionServlet", urlPatterns = {"/session"})
public class SessionServlet extends HttpServlet {


    /**
     * Creates a new session for the user. The request body must contain
     * a JSON object with the following format:
     * 
     * <pre>
     * {
     *   "projectId": &lt;long&gt;,
     *   "monitorType": &lt;String&gt;,
     *   "hospital": &lt;String&gt;
     * }
     * </pre>
     * 
     * @param request the request
     * @param response the response
     * 
     * @throws IOException if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext context = getServletContext();
        String servletSessionID = request.getSession().getId();

        JSONParser parser = new JSONParser();
        try {
            BufferedReader reader = request.getReader();
            JSONObject sessionJson = (JSONObject) parser.parse(reader);
            MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
            MongoSessionDAO sessionDAO = new MongoSessionDAO(client, (String) context.getAttribute("MONGO_DATABASE"));
            if(!sessionDAO.sessionExists(servletSessionID)) {
                sessionDAO.createSession(jsonToSession(sessionJson, servletSessionID));
            }

        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private static Session jsonToSession(JSONObject json, String id) {
        try {
            String hospital= (String) json.get("hospital");
            String monitorType = (String) json.getOrDefault("monitorType", null);
            long projectId = (Long) json.get("projectId");
            return new Session().setHospital(hospital).setMonitorType(monitorType)
                    .setId(id).setProjectId(projectId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JSON is not valid, hospital is missing");
        }
    }

}
