package no.digipat.patornat.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet for retrieving the pair of images that should be
 * compared by a user.
 * 
 * @author Jon Wallem Anundsen
 *
 */
@WebServlet(urlPatterns="/imagePair")
public class NextImagePairServlet extends HttpServlet {
    
    /**
     * Gets a pair of images for comparison. The response body will contain
     * a JSON object of the form <code>{"pair": [id1, id2]}</code>, where {@code id1}
     * and {@code id2} are integers.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * 
     * @throws ServletException if there are not at least two images in the database
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO
        
    }
    
}
