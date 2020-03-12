package no.digipat.compare.servlets;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HeadMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class AuthenticationAndAuthorizationTest {
    
    private static URL baseUrl;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
    }
    
    @Test
    @Parameters({"POST, scoring",
                 "GET, imagePair"})
    public void testStatusCodes(String method, String path) throws Exception {
        WebConversation conversation = new WebConversation();
        conversation.setExceptionsThrownOnErrorStatus(false);
        String url = new URL(baseUrl, path).toString();
        WebRequest request;
        switch (method) {
            case "POST":
                request = new PostMethodWebRequest(url);
                break;
            case "GET":
                request = new GetMethodWebRequest(url);
                break;
            case "HEAD":
                request = new HeadMethodWebRequest(url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
        // Test without session cookie:
        WebResponse response = conversation.sendRequest(request);
        assertEquals(401, response.getResponseCode());
        // Test with invalid session cookie:
        conversation.clearContents();
        conversation.putCookie("JSESSIONID", "arg blarg");
        assertEquals(401, response.getResponseCode());
    }

}
