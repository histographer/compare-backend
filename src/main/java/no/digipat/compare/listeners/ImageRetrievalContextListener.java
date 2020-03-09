package no.digipat.compare.listeners;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import no.digipat.compare.models.image.Image;
import no.digipat.compare.mongodb.dao.MongoImageDAO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.MongoClient;

import be.cytomine.client.Cytomine;
import be.cytomine.client.CytomineConnection;
import be.cytomine.client.CytomineException;

/**
 * A context listener that retrieves information about all the images
 * in a given Cytomine project and adds them to the database.
 * 
 * @see Image
 * @see MongoImageDAO
 * @see MongoDBContextListener
 * 
 * @author Jon Wallem Anundsen
 * 
 */
public class ImageRetrievalContextListener implements ServletContextListener {
    
    /**
     * Connects to a Cytomine instance, retrieves information about all the
     * images in a given project, and adds the information to the database.
     * If the project contains any images that have already been registered in
     * the database, then the database's information about these images will
     * not be updated.
     * <p>
     * In order to connect to the Cytomine instance, this method requires that
     * the environment variables {@code COMPARE_CYTOMINE_URL},
     * {@code COMPARE_CYTOMINE_PUBLIC_KEY}, {@code COMPARE_CYTOMINE_PRIVATE_KEY},
     * and {@code COMPARE_CYTOMINE_PROJECT_ID} be set to the base URL of the
     * Cytomine instance, the public key used to connect to Cytomine, the corresponding
     * private key, and the ID of the project containing the images, respectively.
     * Additionally, in order to add the information to the database, the servlet
     * context associated with {@code servletContextEvent} must have the
     * {@code MONGO_CLIENT} and {@code MONGO_DATABASE} context attributes set to
     * a {@link MongoClient} instance and the name of the database, respectively.
     * </p>
     * 
     * @param servletContextEvent the context event whose context will
     * provide a database connection
     * 
     * @throws IllegalStateException if any of the required environment variables
     * and context attributes are missing or have invalid values
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String cytomineUrl = System.getenv("COMPARE_CYTOMINE_URL");
        String cytominePublicKey = System.getenv("COMPARE_CYTOMINE_PUBLIC_KEY");
        String cytominePrivateKey = System.getenv("COMPARE_CYTOMINE_PRIVATE_KEY");
        String cytomineProjectIdString = System.getenv("COMPARE_CYTOMINE_PROJECT_ID");
        if (cytomineUrl == null || cytominePublicKey == null || cytominePrivateKey == null
                || cytomineProjectIdString == null) {
            throw new IllegalStateException("One or more required environment variables have not been set");
        }
        long cytomineProjectId;
        try {
            cytomineProjectId = Long.parseLong(cytomineProjectIdString);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("The Cytomine project ID must be a long integer", e);
        }
        ServletContext context = servletContextEvent.getServletContext();
        MongoClient mongoClient = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        if (mongoClient == null || databaseName == null) {
            throw new IllegalStateException("The context attributes required to connect to the database have not been set");
        }
        CytomineConnection connection = Cytomine.connection(cytomineUrl, cytominePublicKey, cytominePrivateKey);
        MongoImageDAO imageDao = new MongoImageDAO(mongoClient, databaseName);
        retrieveAndAddImages(connection, cytomineProjectId, imageDao, context);
    }
    
    private static void retrieveAndAddImages(CytomineConnection connection, long projectId, MongoImageDAO imageDao, ServletContext context) {
        try {
            JSONObject abstractImageListJson = connection.doGet("/api/project/" + projectId + "/image.json");
            for (Object object : (JSONArray) abstractImageListJson.get("collection")) {
                JSONObject abstractImageJson = (JSONObject) object;
                Image image = new Image()
                        .setId((Long) abstractImageJson.get("id"))
                        .setMimeType((String) abstractImageJson.get("mime"))
                        .setWidth((Long) abstractImageJson.get("width"))
                        .setHeight((Long) abstractImageJson.get("height"))
                        .setDepth((Long) abstractImageJson.get("depth"))
                        .setResolution((Double) abstractImageJson.get("resolution"))
                        .setMagnification((Long) abstractImageJson.get("magnification"));
                @SuppressWarnings("unchecked")
                List<String> serverUrls = (List<String>) connection.doGet("/api/abstractimage/"
                        + image.getId() + "/imageservers.json").get("imageServersURLs");
                image.setImageServerURLs(serverUrls.toArray(new String[] {}));
                try {
                    imageDao.createImage(image);
                } catch (IllegalStateException e) {
                    System.out.println("Image with ID " + image.getId() + " already exists and was not added to the database");
                    context.log("Image with ID " + image.getId() + " already exists and was not added to the database");
                }
            }
        } catch (CytomineException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Does nothing.
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        
    }
    
}
