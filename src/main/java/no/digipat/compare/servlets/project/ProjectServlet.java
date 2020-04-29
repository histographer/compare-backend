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

@WebServlet("/project")
public class ProjectServlet extends HttpServlet {


    /**
     * Creates a new project and inserts images. The request body must contain
     * a JSON object with the following format:
     * 
     * <pre>
     * {
     *   "projectId": &lt;long&gt;,
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

        JSONParser parser = new JSONParser();
        try {
            BufferedReader reader = request.getReader();
            org.json.simple.JSONObject projectJson =
                    (org.json.simple.JSONObject) parser.parse(reader);
            Long projectId = jsonToProjectId(projectJson);

            MongoClient mongoClient = (MongoClient) context.getAttribute("MONGO_CLIENT");
            String databaseName = (String) context.getAttribute("MONGO_DATABASE");
            MongoProjectDAO projectDao = new MongoProjectDAO(mongoClient, databaseName);

            if (projectDao.projectExists(projectId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("A project with this ID already exists");
                // TODO is there a more appropriate response code?
            } else {
                CytomineConnection connection = (CytomineConnection) context
                        .getAttribute("CYTOMINE_CONNECTION");
                Project project = retrieveProjectInformation(connection, projectId);
                
                MongoImageDAO imageDao = new MongoImageDAO(mongoClient, databaseName);
                retrieveAndAddImages(connection, projectId, imageDao, context);
                // Placed last so if there is an exception with retrieve and
                // add images it will not create a project
                projectDao.createProject(project);
            }
        } catch (ParseException | IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(e);
        }
    }

    /**
     * Gets a project or all projects.
     * Get a single project by using the query {@code /project?projectId=id}.
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
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoProjectDAO projectDao = new MongoProjectDAO(client, databaseName);
        String projectIdString = request.getParameter("projectId");
        if (projectIdString == null) {
            // The projectId parameter is missing, so we return all projects
            List<Project> projects = projectDao.getAllProjects();
            response.getWriter().print(getAllProjectsResponse(projects));
        } else {
            // The user is requesting a specific project
            try {
                long projectId = Long.parseLong(request.getParameter("projectId"));
                if (!projectDao.projectExists(projectId)) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().print("Project does not exist in database}");
                } else {
                    Project project = projectDao.getProject(projectId);
                    response.getWriter().print(getProjectResponse(project));
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    private static JSONObject getProjectResponse(Project project) {
        JSONObject json = new JSONObject();
        json.put("id", project.getId());
        json.put("name", project.getName());
        json.put("active", project.getActive());
        return json;
    }

    private static JSONArray getAllProjectsResponse(List<Project> projects) {
        JSONArray jsonArray = new JSONArray();

        for (Project project : projects) {
           JSONObject json = new JSONObject();
            json.put("id", project.getId());
            json.put("name", project.getName());
            json.put("active", project.getActive());
            jsonArray.put(json);
        }
        return jsonArray;
    }


    private static Long jsonToProjectId(org.json.simple.JSONObject json)
            throws IllegalArgumentException {
        try {
            long projectId = (Long) json.get("projectId");
            return projectId;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JSON is not valid, projectId is missing");
        }
    }

    /**
     * Polls the cytomine client for information about the project
     * and returns a project object.
     *
     * @param connection {@code CytomineConnection}
     * @param projectId {@code long}
     * @return {@code Project}
     */
    private static Project retrieveProjectInformation(CytomineConnection connection,
            long projectId) {
        Project project;
        try {
            org.json.simple.JSONObject simpleJsonProjectInformation = connection
                    .doGet("/api/project/" + projectId + ".json");
            JSONObject projectInformation = new JSONObject(simpleJsonProjectInformation);
            project = new Project()
                    .setId(projectId)
                    .setName((String) projectInformation.get("name"))
                    .setActive(false);
            return project;
        } catch (CytomineException e) {
            throw new RuntimeException(
                    "Trouble fetching the project information from cytomine",
                    e
            );
        }
    }
    
    /**
     * Connects to a Cytomine instance, retrieves information about all the
     * images in a given project, and adds the information to the database.
     * If the project contains any images that have already been registered in
     * the database, then the database's information about these images will
     * not be updated.
     * 
     * @param connection a connection to the Cytomine instance
     * @param projectId the project ID
     * @param imageDao the DAO with which to update the database
     * @param context the servlet context
     */
    private static void retrieveAndAddImages(CytomineConnection connection,
            long projectId, MongoImageDAO imageDao, ServletContext context) {
        try {
            org.json.simple.JSONObject abstractImageListJsonSimple = connection
                    .doGet("/api/project/" + projectId + "/image.json");
            JSONObject abstractImageListJson = new JSONObject(abstractImageListJsonSimple);
            JSONArray arr = (JSONArray) abstractImageListJson.get("collection");
            
            for (Object object : arr) {
                JSONObject abstractImageJson = (JSONObject) object;
                try {
                   Long getWith = (Long) abstractImageJson.get("width");
                } catch (org.json.JSONException e) {
                    // this means that these values are null, and we will not use this object
                    // some images have this value as null, and are therefore unusable
                    continue;
                }
                Image image = new Image()
                        .setImageId((Long) abstractImageJson.get("id"))
                        .setProjectId(projectId)
                        .setFileName((String) abstractImageJson.get("filename"))
                        .setMimeType((String) abstractImageJson.get("mime"))
                        .setWidth((Long) abstractImageJson.get("width"))
                        .setHeight((Long) abstractImageJson.get("height"))
                        .setDepth((Long) abstractImageJson.get("depth"))
                        .setResolution((Double) abstractImageJson.get("resolution"))
                        .setMagnification((Long) abstractImageJson.get("magnification"));
                List<String> serverUrls = (List<String>) connection.doGet("/api/abstractimage/"
                        + image.getImageId() + "/imageservers.json").get("imageServersURLs");
                image.setImageServerURLs(serverUrls.toArray(new String[] {}));
                try {
                    imageDao.createImage(image);
                } catch (IllegalStateException e) {
                    context.log("Image with ID " + image.getImageId()
                        + " already exists and was not added to the database");
                }
            }
        } catch (CytomineException e) {
            throw new RuntimeException(e);
        }
    }

}
