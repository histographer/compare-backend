package no.digipat.patornat.servlets;

import com.mongodb.MongoClient;
import no.digipat.patornat.mongodb.dao.MongoSessionDAO;
import no.digipat.patornat.mongodb.models.session.Session;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "no.digipat.patornat.servlets.UserServlet", urlPatterns = {"/session"})
public class SessionServlet extends HttpServlet {


    /**
     * @param request
     * {
     *     monitorType, string
     *     hospital, string
     * }
     * @param response
     *
     * StatusCode: 200
     *
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        ServletContext context = request.getServletContext();
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
        } catch (ParseException |  IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }


    private static Session jsonToSession(JSONObject json, String id) {
        try {
            String hospital= (String) json.get("hospital");
            String monitorType = (String) json.getOrDefault("monitorType", null);
            return new Session().setHospital(hospital).setMonitorType(monitorType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JSON is not valid, hospital is missing");
        }
    }

}
