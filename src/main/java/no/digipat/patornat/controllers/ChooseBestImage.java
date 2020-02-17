package no.digipat.patornat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet(name = "ChooseBestImage")
public class ChooseBestImage extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Gets the json from the request
        BufferedReader bufferedReader=
                new BufferedReader(new InputStreamReader(request.getInputStream()));

        String json = "";
        if(bufferedReader != null){
            json = bufferedReader.readLine();
            System.out.println(json);
        }

        ObjectMapper mapper = new ObjectMapper();

        // Mapping for the object
        // Object object = mapper.readValue(json, Object.class);


        // Todo: logic for adding to db

        // Mapping from object to json
        // mapper.writeValue(response.getOutPutStream(), object)
    }

}
