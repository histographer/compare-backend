package mongodb.listener;

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
            String Host = context.getInitParameter("MONGODB_HOST");
            Integer Port = Integer.parseInt(context.getInitParameter("MONGODB_PORT"));
            String Username = context.getInitParameter("MONGODB_USERNAME");
            String Password = context.getInitParameter("MONGODB_PASSWORD");
            String Database = context.getInitParameter("MONGODB_DATABASE");
            MongoClientURI  MONGO_URI = new MongoClientURI("mongodb://"+Username+":"+Password+"@"+Host+":"+Port+"/"+Database);

            MongoClient client = new MongoClient(MONGO_URI);
            System.out.println("Mongoclient connected successfully at "+Host+":"+Port);
            servletContextEvent.getServletContext().setAttribute("MONGO_CLIENT", client);
        } catch(Exception error) {
            throw new RuntimeException("Mongoclient initialization failed");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

        MongoClient client = (MongoClient) servletContextEvent.getServletContext().getAttribute(("MONGO_CLIENT"));
        client.close();
        System.out.println("Mongo connection terminated");
    }
}
