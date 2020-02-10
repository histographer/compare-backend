package no.digipat.cytomine;
import be.cytomine.client.*;
import be.cytomine.client.models.*;
import be.cytomine.client.collections.*;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CytomineConnection {

    private static final String CYTOMINE_URL = "";
    private static final String PUBLIC_KEY = "";
    private static final String PRIVATE_KEY = "";
    private static final String URL = CYTOMINE_URL+"j_spring_security_check";
    private static final String username = "";
    private static final String password = "";

    public static void getProjects() {
        Cytomine.connection(CYTOMINE_URL, PUBLIC_KEY, PRIVATE_KEY);
        try {


            Cytomine cytomine = Cytomine.getInstance();
            System.out.println("Hello " + cytomine.getCurrentUser().get("username"));
            System.out.println("******* You have access to these projects: *******");
            ProjectCollection projects = cytomine.getProjects();
            for (int i = 0; i < projects.size(); i++) {
                Project project = projects.get(i);
                System.out.println(project.toJSON());
            }
        } catch(Exception error) {
            System.out.println(error);
        }
    }
   public static CloseableHttpResponse login() throws MalformedURLException {
       java.net.URL url = new URL(URL);
       HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
       CloseableHttpClient client = HttpClientBuilder.create().build();

       HttpPost httpPost = new HttpPost(URL.toString());
       List<NameValuePair> urlParameters = new ArrayList<>();
       urlParameters.add(new BasicNameValuePair("j_username", username));
       urlParameters.add(new BasicNameValuePair("j_password", password));

       try {
           httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
           CloseableHttpResponse response = client.execute(httpPost);
           return response;
       } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
       } catch (ClientProtocolException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }

       return null;
   }
}

