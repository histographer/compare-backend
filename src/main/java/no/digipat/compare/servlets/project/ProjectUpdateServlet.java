package no.digipat.compare.servlets.project;

import be.cytomine.client.CytomineConnection;
import be.cytomine.client.CytomineException;
import com.mongodb.MongoClient;
import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.project.Project;
import no.digipat.compare.mongodb.dao.MongoImageDAO;
import no.digipat.compare.mongodb.dao.MongoProjectDAO;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.List;

@WebServlet(name = "no.digipat.compare.servlets.project.ProjectUpdateServlet", urlPatterns = {"/project/update"})
public class ProjectUpdateServlet extends HttpServlet {


    /**
     *
     * Updates a project active status
     *  needs query parameters {@code projectId} which is long and {@active} which is bool
     *
     * @param request the request
     * @param response the response
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext context = getServletContext();
        Long projectId = null;
        Boolean active = null;
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoProjectDAO projectDao = new MongoProjectDAO(client, databaseName);
        response.setContentType("application/json");
        try {
            projectId = Long.parseLong(request.getParameter("projectId"));
            active = Boolean.parseBoolean(request.getParameter("active"));
            if(!projectDao.projectExists(projectId)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                response.getWriter().print("Project does not exist in database");
            }
            {
                Project project = projectDao.updateProjectActive(projectId, active);
                response.getWriter().print(getProjectResponse(project));

            }
        } catch(NumberFormatException e) {
            response.getWriter().print(e);
        } catch (Exception e) {
            response.getWriter().print(e);
        }

        if(projectId == null || active == null) {
            response.getWriter().print("One of the parameters is not set: projectId="+projectId+". active: "+active+".");
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
