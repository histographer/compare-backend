package no.digipat.compare.servlets;

import com.mongodb.MongoClient;

import javassist.NotFoundException;
import no.digipat.compare.models.session.Session;
import no.digipat.compare.mongodb.dao.MongoProjectDAO;
import no.digipat.compare.mongodb.dao.MongoSessionDAO;

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
            String database = (String) context.getAttribute("MONGO_DATABASE");
            MongoSessionDAO sessionDAO = new MongoSessionDAO(client, database);
            MongoProjectDAO projectDAO = new MongoProjectDAO(client, database);
            if(!sessionDAO.sessionExists(servletSessionID)) {
                sessionDAO.createSession(jsonToSession(sessionJson, projectDAO, servletSessionID));
            }

        } catch (ParseException | NullPointerException | NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(e);
        }
    }

    /**
     * Invalidates a session
     * @param request the request
     * @param response the response
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if(session == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("No active session to invalidate");
            return;
        }
        try {
            boolean logout = Boolean.parseBoolean(request.getParameter("logout"));
            if(logout == true) {
                request.getSession().invalidate();
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print("session logged out");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print("something went wrong when logging out. The logout paramater value is: "+logout+". It should be true");

            }
        } catch(NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("something went wrong when logging out");
        }

    }

    private static Session jsonToSession(JSONObject json, MongoProjectDAO projectDAO, String id) throws NotFoundException {
            String hospital= (String) json.getOrDefault("hospital", null);
            String monitorType = (String) json.getOrDefault("monitorType", null);
            Long projectId = (Long) json.getOrDefault("projectId", null);
            if(hospital == null) {
                throw new NullPointerException("hospital field has to be set");
            }
            if(monitorType == null) {
                throw new NullPointerException("monitorType field has to be set");
            }
            if(projectId == null) {
                throw new NullPointerException("projectId field has to be set");
            }
            if(!projectDAO.ProjectExist(projectId)){
                throw new NotFoundException("A project with this id does not exist");
            }
            return new Session().setHospital(hospital).setMonitorType(monitorType)
                    .setId(id).setProjectId(projectId);
    }

}
