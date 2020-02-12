package no.digipat.patornat.mongodb.listener;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class MongoDBContextListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            ServletContext context = servletContextEvent.getServletContext();
            String host = context.getInitParameter("MONGODB_HOST");
            Integer port = Integer.parseInt(context.getInitParameter("MONGODB_PORT"));
            String username = context.getInitParameter("MONGODB_USERNAME");
            String password = context.getInitParameter("MONGODB_PASSWORD");
            String database = context.getInitParameter("MONGODB_DATABASE");
            MongoClientURI  MONGO_URI = new MongoClientURI("mongodb://"+username+":"+password+"@"+host+":"+port+"/"+database);


            MongoClient client = new MongoClient(MONGO_URI);
            context.log("Mongoclient connected successfully at "+host+":"+port);
            servletContextEvent.getServletContext().setAttribute("MONGO_CLIENT", client);
        } catch(Exception error) {
            throw new RuntimeException("Mongoclient initialization failed", error);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        MongoClient client = (MongoClient) servletContextEvent.getServletContext().getAttribute(("MONGO_CLIENT"));
        client.close();
        servletContextEvent.getServletContext().log("Mongo connection terminated");
    }
}
