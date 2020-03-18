package no.digipat.compare.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import be.cytomine.client.Cytomine;
import be.cytomine.client.CytomineConnection;
import no.digipat.compare.models.image.Image;
import no.digipat.compare.mongodb.dao.MongoImageDAO;

/**
 * A context listener that add a Cytomine instance to the context
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
     * Stores a connection to a Cytomine instance in the servlet context.
     * The connection will be stored as the context parameter {@code CYTOMINE_CONNECTION}.
     * <p>
     * In order to connect to the Cytomine instance, this method requires that
     * the environment variables {@code COMPARE_CYTOMINE_URL},
     * {@code COMPARE_CYTOMINE_PUBLIC_KEY}, {@code COMPARE_CYTOMINE_PRIVATE_KEY}
     * be set to the base URL of the Cytomine instance, the public key used to
     * connect to Cytomine, and the corresponding private key, respectively.
     * </p>
     * 
     * @param servletContextEvent the context event whose context will store the
     * Cytomine connection
     * 
     * @throws IllegalStateException if any of the required environment variables are missing
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String cytomineUrl = System.getenv("COMPARE_CYTOMINE_URL");
        String cytominePublicKey = System.getenv("COMPARE_ADMIN_PUB_KEY");
        String cytominePrivateKey = System.getenv("COMPARE_ADMIN_PRIV_KEY");
        if (cytomineUrl == null || cytominePublicKey == null || cytominePrivateKey == null) {
            throw new IllegalStateException("One or more required environment variables have not been set");
        }
        ServletContext context = servletContextEvent.getServletContext();
        CytomineConnection connection = Cytomine.connection(cytomineUrl, cytominePublicKey, cytominePrivateKey);
        context.setAttribute("CYTOMINE_CONNECTION", connection);
    }
    

    /**
     * Does nothing.
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        
    }
    
}
