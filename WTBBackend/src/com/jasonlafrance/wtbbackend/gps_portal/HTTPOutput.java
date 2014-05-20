package com.jasonlafrance.wtbbackend.gps_portal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Jason LaFrance
 */
public class HTTPOutput {

    private static final String USER_AGENT = "Mozilla/5.0";

    // HTTP GET request
    public static String HTTPGet(String inUrl) {
        StringBuilder sb = new StringBuilder();
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(new URL(inUrl).openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();

        } catch (MalformedURLException ex) {
            //Logger.getLogger(HTTPOutput.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Logger.getLogger(HTTPOutput.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                //Logger.getLogger(HTTPOutput.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sb.toString();
    }
}
