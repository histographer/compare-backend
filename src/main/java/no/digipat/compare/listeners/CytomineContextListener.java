package no.digipat.compare.listeners;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.MongoClient;

import be.cytomine.client.Cytomine;
import be.cytomine.client.CytomineConnection;
import be.cytomine.client.CytomineException;
import no.digipat.compare.models.image.Image;
import no.digipat.compare.mongodb.dao.MongoImageDAO;

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
public class CytomineContextListener implements ServletContextListener {
    
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
        System.out.println("Image Retrieval listener");
        String cytomineUrl = System.getenv("COMPARE_CYTOMINE_URL");
        String cytominePublicKey = System.getenv("COMPARE_ADMIN_PUB_KEY");
        String cytominePrivateKey = System.getenv("COMPARE_ADMIN_PRIV_KEY");
        if (cytomineUrl == null || cytominePublicKey == null || cytominePrivateKey == null
                ) {
            throw new IllegalStateException("One or more required environment variables have not been set");
        }
        ServletContext context = servletContextEvent.getServletContext();
        context.setAttribute("CYTOMINE_URL", cytomineUrl);
        context.setAttribute("CYTOMINE_PUBLIC_KEY", cytominePublicKey);
        context.setAttribute("CYTOMINE_PRIVATE_KEY", cytominePrivateKey);
    }
    

    /**
     * Does nothing.
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        
    }
    
}
