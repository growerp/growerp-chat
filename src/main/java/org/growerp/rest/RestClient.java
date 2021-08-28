package org.growerp.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestClient {
	private String urlString = "https://localhost:8080/rest/s1/growerp/100/";

    public RestClient(String url) {
        this.urlString = url;
    }
    
    public Boolean validate(String apiKey) {
        Boolean result = false;
        Logger logger = LoggerFactory.getLogger(RestClient.class);
        try {
            URL url = new URL(urlString + "CheckApiKey");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("api_key", apiKey);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);        
            int status = con.getResponseCode();

            if(status == 200) {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
                  String inputLine;
                  StringBuffer content = new StringBuffer();
                  while ((inputLine = in.readLine()) != null) {
                      content.append(inputLine);
                  }
                  if (content.toString().contains("ok")) result = true;
                  in.close();
            }
            con.disconnect();
        } catch (Exception ex) {
            logger.info("Validation request not worked error: " + ex);
        }
        return result;
    }
}

