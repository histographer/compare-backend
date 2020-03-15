package no.digipat.compare.servlets;

import be.cytomine.client.CytomineConnection;
import be.cytomine.client.CytomineException;
import com.mongodb.MongoClient;
import no.digipat.compare.models.image.Image;
import no.digipat.compare.models.session.Session;
import no.digipat.compare.mongodb.dao.MongoImageDAO;
import no.digipat.compare.mongodb.dao.MongoSessionDAO;
import org.json.simple.JSONArray;
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
import java.util.List;

@WebServlet(name = "no.digipat.compare.servlets.UserServlet", urlPatterns = {"/session"})
public class ProjectServlet extends HttpServlet {


    /**
     * Creates a new session for the user. The request body must contain
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext context = getServletContext();

        JSONParser parser = new JSONParser();
        try {
            BufferedReader reader = request.getReader();
            JSONObject projectJson = (JSONObject) parser.parse(reader);
            Long projectId = jsonToProjectId(projectJson);
            MongoClient mongoClient = (MongoClient) context.getAttribute("MONGO_CLIENT");
            String databaseName = (String) context.getAttribute("MONGO_DATABASE");
            MongoImageDAO imageDao = new MongoImageDAO(mongoClient, databaseName);
            CytomineConnection connection = (CytomineConnection) context.getAttribute("CYTOMINE_CONNECTION");

            retrieveAndAddImages(connection, projectId, imageDao, context);

        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }


    private static Long jsonToProjectId(JSONObject json) {
        try {
            long projectId = (Long) json.get("projectId");
            return projectId;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JSON is not valid, project is missing");
        }
    }

/**
 * Connects to a Cytomine instance, retrieves information about all the
 * images in a given project, and adds the information to the database.
 * If the project contains any images that have already been registered in
 * the database, then the database's information about these images will
 * not be updated.
 */
 private static void retrieveAndAddImages(CytomineConnection connection, long projectId, MongoImageDAO imageDao, ServletContext context) {
        try {
            JSONObject abstractImageListJson = connection.doGet("/api/project/" + projectId + "/image.json");
            for (Object object : (JSONArray) abstractImageListJson.get("collection")) {
                JSONObject abstractImageJson = (JSONObject) object;
                Image image = new Image()
                        .setImageId((Long) abstractImageJson.get("id"))
                        .setProjectId(projectId)
                        .setMimeType((String) abstractImageJson.get("mime"))
                        .setWidth((Long) abstractImageJson.get("width"))
                        .setHeight((Long) abstractImageJson.get("height"))
                        .setDepth((Long) abstractImageJson.get("depth"))
                        .setResolution((Double) abstractImageJson.get("resolution"))
                        .setMagnification((Long) abstractImageJson.get("magnification"));
                // TODO file name
                @SuppressWarnings("unchecked")
                List<String> serverUrls = (List<String>) connection.doGet("/api/abstractimage/"
                        + image.getImageId() + "/imageservers.json").get("imageServersURLs");
                image.setImageServerURLs(serverUrls.toArray(new String[] {}));
                try {
                    imageDao.createImage(image);
                } catch (IllegalStateException e) {
                    context.log("Image with ID " + image.getImageId() + " already exists and was not added to the database");
                }
            }
        } catch (CytomineException e) {
            throw new RuntimeException(e);
        }
    }

}
