package no.digipat.compare.servlets.project;

import com.mongodb.MongoClient;
import no.digipat.compare.models.project.Project;
import no.digipat.compare.mongodb.dao.MongoProjectDAO;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "no.digipat.compare.servlets.project.ProjectUpdateServlet", urlPatterns = {"/project/update"})
public class ProjectUpdateServlet extends HttpServlet {


    /**
     *
     * Updates a project's active status. The request body must contain
     * a JSON object of the following form:
     * 
     * <pre>
     *   {
     *     "projectId": &lt;long&gt;,
     *     "active": &lt;boolean&gt;
     *   }
     * </pre>
     * 
     * The response body will contain a representation of the project's
     * state after the update, represented by a JSON object of the following form:
     * 
     * <pre>
     *   {
     *     "id": &lt;long&gt;,
     *     "active": &lt;boolean&gt;,
     *     "name": &lt;String&gt;
     *   }
     * </pre>
     * 
     * @param request the request
     * @param response the response
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext context = getServletContext();
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoProjectDAO projectDao = new MongoProjectDAO(client, databaseName);
        try {
            JSONObject json = new JSONObject(new JSONTokener(request.getInputStream()));
            long projectId = json.getLong("projectId");
            boolean active = json.getBoolean("active");
            if (!projectDao.projectExists(projectId)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                Project project = projectDao.updateProjectActive(projectId, active);
                response.setContentType("application/json");
                response.getWriter().print(getProjectResponse(project));
            }
        } catch(JSONException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private static JSONObject getProjectResponse(Project project) {
        JSONObject json = new JSONObject();
        json.put("id", project.getId());
        json.put("name", project.getName());
        json.put("active", project.getActive());
        return json;
    }

}
