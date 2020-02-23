package no.digipat.patornat.servlets;

import com.mongodb.MongoClient;
import no.digipat.patornat.mongodb.dao.Converter;
import no.digipat.patornat.mongodb.dao.MongoBestImageDAO;
import no.digipat.patornat.mongodb.dao.MongoUserDAO;
import no.digipat.patornat.mongodb.models.user.User;
import org.json.simple.JSONObject;
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
import java.io.PrintWriter;

@WebServlet(name = "no.digipat.patornat.servlets.UserServlet", urlPatterns = {"/user"})
public class UserServlet extends HttpServlet {


    /**
     * @param request
     * @param response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        JSONParser parser = new JSONParser();
        PrintWriter out = response.getWriter();
        try {
            BufferedReader reader = request.getReader();
            JSONObject userJson = (JSONObject) parser.parse(reader);
            User user = Converter.jsonToUser(userJson);
            if(user.getHospital() == null || user.getMonitorType() == null) {
                String ERROR = "Both hospital and monitortype has to be specified";
                out.println(ERROR);
                throw new IllegalArgumentException(ERROR);
            }
            MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
            MongoUserDAO userDAO = new MongoUserDAO(client, (String) context.getAttribute("MONGO_DATABASE"));
            userDAO.createUser(user);

            response.setContentType("application/json");
            JSONObject userResponse = new JSONObject();
            userResponse.put("user", user.getId());
            out.println(userResponse);
        } catch (ParseException | IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
