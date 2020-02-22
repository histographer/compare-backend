import com.mongodb.MongoClient;
import no.digipat.patornat.mongodb.dao.MongoUserDAO;
import no.digipat.patornat.mongodb.models.user.IUser;
import no.digipat.patornat.mongodb.models.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "TestServlet", urlPatterns = {"/testservlet"})
public class TestServlet extends HttpServlet {


    /**
     * Example for insertion in mongoDB via servlet, used for reference
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String id = request.getParameter("id");
        if(username == null) {
            System.out.println("Username is mandatory, but is missing");
        }
        else {
            User user = new User(id, username);
            MongoClient client = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
            MongoUserDAO userDAO = new MongoUserDAO(client);
            userDAO.createUser(user);
            System.out.println("User was added successfully with username: "+username+" and id: "+id);
        }

    }

    /**
     * Example for get document from mongodb in servlet, user for reference
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String id = request.getParameter("id");
        if(username == null) {
            System.out.println("Username is mandatory, but is missing");
        }
        else {
            User user = new User(id, username);
            MongoClient client = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
            MongoUserDAO userDAO = new MongoUserDAO(client);
            IUser userFromDb = userDAO.readUser(user);
            System.out.println("Got user from db:" +userFromDb.getUsername()+" and id: "+userFromDb.getId());
        }
    }
}
