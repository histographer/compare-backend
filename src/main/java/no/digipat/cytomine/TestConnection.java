package no.digipat.cytomine;

import com.mongodb.MongoClient;
import no.digipat.mongodb.DAO.MongoUserDAO;
import no.digipat.mongodb.models.IUser;
import no.digipat.mongodb.models.User;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "TestConnection", urlPatterns = {"/testconnection"})
public class TestConnection extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //CytomineConnection.getProjects();
        HttpEntity res = CytomineConnection.getUser();

//        Header[] cookieHeaders  = res.getHeaders("Set-Cookie");
//        Header[] pathHeaders = res.getHeaders("Location");
//
//        String value = cookieHeaders[1].getValue();
//        String regexString = "(?<=JSESSIONID=)[^;]+";
//        Pattern JsessionRegexPattern = Pattern.compile(regexString);
//
//        Matcher JsessionValue = JsessionRegexPattern.matcher(value);
//        if (JsessionValue.find()) {
//            Cookie JSESSIONID = new Cookie("JSESSIONID", JsessionValue.group(0).toString());
//            JSESSIONID.setHttpOnly(true);
//            JSESSIONID.setPath("/");
//            response.addCookie(JSESSIONID);
//        }

        //String regexString = "(?<=JSESSIONID=)[^;]+";
        PrintWriter out = response.getWriter();
        out.println(res);
    }
}
