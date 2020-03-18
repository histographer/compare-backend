package no.digipat.compare.listeners;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * A context listener that sets the context parameters required to
 * connect to the analysis backend.
 * 
 * @author Jon Wallem Anundsen
 *
 */
@WebListener
public class AnalysisBackendContextListener implements ServletContextListener {
    
    /**
     * Sets the context parameters required to connect to the analysis backend.
     * After this method has been called, the context's {@code ANALYSIS_BASE_URL}
     * attribute will have been set to an instance of {@link URL} containing the
     * base URL of the analysis backend.
     * <p>
     * The base URL is constructed from the environment variables {@code PATORNAT_ANALYSIS_BACKEND_PROTOCOL},
     * {@code PATORNAT_ANALYSIS_BACKEND_HOST}, and {@code PATORNAT_ANALYSIS_BACKEND_PORT}.
     * </p>
     * 
     * @param servletContextEvent the context event whose context will have its
     * parameters set
     * 
     * @throws IllegalStateException if the required environment variables are missing
     * or have invalid values
     * 
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String protocol = System.getenv("COMPARE_ANALYSIS_PROTOCOL");
        String host = System.getenv("COMPARE_ANALYSIS_URL");
        String port = System.getenv("COMPARE_ANALYSIS_PORT");
        try {
            URL url = null;
            if(port != null) {
                url = new URL(protocol, host, Integer.parseInt(port), "");
            } else {
                url = new URL(protocol+"://"+host);
            }
            servletContextEvent.getServletContext().setAttribute("ANALYSIS_BASE_URL", url);
        } catch (MalformedURLException | NumberFormatException | NullPointerException e) {
            throw new IllegalStateException("Environment variables have not been set correctly", e);
        }
    }
    
    /**
     * Does nothing.
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        
    }
    
}
