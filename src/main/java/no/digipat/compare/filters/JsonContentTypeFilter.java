package no.digipat.compare.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * A filter that sets the content type of responses to {@code application/json}.
 * 
 * @author Jon Wallem Anundsen
 *
 */
@WebFilter("/*")
public class JsonContentTypeFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        response.setContentType("application/json");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        
    }

}
