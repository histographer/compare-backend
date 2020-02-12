import com.mongodb.MongoClient;
import no.digipat.mongodb.dao.MongoUserDAO;
import no.digipat.mongodb.models.IUser;
import no.digipat.mongodb.models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "TestServlet", urlPatterns = {"/testservlet"})
public class TestServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String id = request.getParameter("id");
        if(username == null) {
            System.out.println("Username is mandatory, but is missing");
        }
        else {
            User user = new User();
            user.setUsername(username);
            user.setId(id);
            MongoClient client = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
            MongoUserDAO userDAO = new MongoUserDAO(client);
            userDAO.createUser(user);
            System.out.println("User was added successfully with username: "+username+" and id: "+id);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String id = request.getParameter("id");
        if(username == null) {
            System.out.println("Username is mandatory, but is missing");
        }
        else {
            User user = new User();
            user.setUsername(username);
            user.setId(id);
            MongoClient client = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
            MongoUserDAO userDAO = new MongoUserDAO(client);
            IUser userFromDb = userDAO.readUser(user);
            System.out.println("Got user from db:" +userFromDb.getUsername()+" and id: "+userFromDb.getId());
        }
    }
}
