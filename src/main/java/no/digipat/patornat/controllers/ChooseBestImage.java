package no.digipat.patornat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.digipat.patornat.mongodb.dao.Converter;
import no.digipat.patornat.mongodb.models.BestImage;
import no.digipat.patornat.mongodb.models.Image;
import org.apache.http.conn.routing.RouteInfo;
import org.json.HTTP;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static no.digipat.patornat.mongodb.dao.Converter.JsonToBestImage;

@WebServlet(name = "ChooseBestImage",  urlPatterns = {"/bestimage"})
public class ChooseBestImage extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Gets the json from the request
        StringBuffer stringBuffer = new StringBuffer();
        String line = "";
        try {
            BufferedReader reader = request.getReader();
            while((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (Exception e){
            throw new IOException("Reading buffer failed: ", e);
        }

        JSONParser parser = new JSONParser();
        // Parsing the json to objects
        try {
            JSONObject bestImageJson=  (JSONObject) parser.parse(stringBuffer.toString());
            BestImage bestImage = JsonToBestImage(bestImageJson);
        } catch (JSONException |  ParseException e) {
            throw new IOException("Error parsing JSON request string");
        }

        // Todo: logic for adding to db
    }

}
