package no.digipat.compare.servlets;

import com.mongodb.MongoClient;

import javassist.NotFoundException;
import no.digipat.compare.models.session.Session;
import no.digipat.compare.mongodb.dao.MongoProjectDAO;
import no.digipat.compare.mongodb.dao.MongoSessionDAO;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * A servlet for dealing with sessions.
 * 
 * @author Kent Are Torvik
 *
 */
@WebServlet("/session")
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ServletContext context = getServletContext();
        String servletSessionID = request.getSession().getId();
        try {
            JSONObject sessionJson = new JSONObject(
                    IOUtils.toString(request.getInputStream(), request.getCharacterEncoding()));
            MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
            String database = (String) context.getAttribute("MONGO_DATABASE");
            MongoSessionDAO sessionDAO = new MongoSessionDAO(client, database);
            MongoProjectDAO projectDAO = new MongoProjectDAO(client, database);
            if (!sessionDAO.sessionExists(servletSessionID)) {
                sessionDAO.createSession(jsonToSession(sessionJson, projectDAO, servletSessionID));
            }
        } catch (JSONException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        } catch (NotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "A project with this ID does not exist");
        }
    }
    
    /**
     * Invalidates a session if the query parameter {@code logout}
     * is {@code true}.
     * 
     * @param request the request
     * @param response the response
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        boolean logout = Boolean.parseBoolean(request.getParameter("logout"));
        if (session != null && logout) {
            session.invalidate();
            response.getWriter().print("session logged out");
        }
    }
    
    private static Session jsonToSession(JSONObject json, MongoProjectDAO projectDAO, String id)
            throws NotFoundException, JSONException {
        String hospital = json.getString("hospital");
        String monitorType = json.getString("monitorType");
        long projectId = json.getLong("projectId");
        if (!projectDAO.projectExists(projectId)) {
            throw new NotFoundException("A project with this ID does not exist");
        }
        return new Session().setHospital(hospital).setMonitorType(monitorType)
                .setId(id).setProjectId(projectId);
    }

}
