package no.digipat.patornat.listeners;

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
     * The base URL is constructed from the environment variables {@code COMPARE_ANALYSIS_BACKEND_PROTOCOL},
     * {@code COMPARE_ANALYSIS_BACKEND_HOST}, and {@code COMPARE_ANALYSIS_BACKEND_PORT}.
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
        String protocol = System.getenv("COMPARE_ANALYSIS_BACKEND_PROTOCOL");
        String host = System.getenv("COMPARE_ANALYSIS_BACKEND_HOST");
        String port = System.getenv("COMPARE_ANALYSIS_BACKEND_PORT");
        try {
            URL url = new URL(protocol, host, Integer.parseInt(port), "");
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
