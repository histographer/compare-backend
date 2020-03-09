package no.digipat.patornat.filters;

import com.mongodb.MongoClient;
import no.digipat.patornat.mongodb.dao.MongoSessionDAO;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/scoring")
public class AuthorizationFilter implements Filter {
    private FilterConfig config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.config = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        ServletContext context = config.getServletContext();
        HttpSession session = request.getSession(false);

        if(session == null) {
            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not initiated session, please add session.");
        } else {
            MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
            MongoSessionDAO sessionDAO = new MongoSessionDAO(client, (String) context.getAttribute("MONGO_DATABASE"));
            String sessionID = session.getId();
            if(!sessionDAO.sessionExists(sessionID)) {
                ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session is not initiated correctly, please initiate again.");
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }

        }

    }

    @Override
    public void destroy() {

    }
}
