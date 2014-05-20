/*
Copyright 2013, 2014 Jason LaFrance

This file is part of WTBBackend.

    WTBBackend is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    WTBBackend is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with WTBBackend.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.jasonlafrance.wtbbackend.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Jason LaFrance
 */
public class Config {

    // constants
    public static String GTFS_DIRS = "gtfs_dirs";
    public static String GTFS_TIME_GAP = "gtfs_time_gap";
    public static String DRONES_ACTIVE = "drones_active";
    public static String DRONE_SPEED = "drone_speed";
    public static String DRONE_UPDATE_SPEED = "drone_update_speed";
    public static String SERVER_PORT = "server_port";
    public static String SERVER_PASSWORD = "server_password";
    public static String PACKET_OK = "packet_ok";
    public static String PACKET_BAD = "packet_bad";
    public static String COORD_BUFFER_SIZE = "coord_buffer_size";
    public static String CLOSENESS_THRESHOLD = "closeness_threshold";
    public static String STOP_WINDOW_MARGIN = "stop_window_margin";
    public static String VEHICLE_LIST_TIMEOUT = "vehicle_list_timeout";
    public static String DEBUG_DISPLAY = "debug_display";

    private static HashMap<String, String> sOptions;
    private static Config sInstance = null;

    public static Config get() {
        if (sInstance == null) {
            sInstance = new Config();
        }
        return sInstance;
    }

    protected Config() {
        sOptions = new HashMap<>();
    }
    
    public void load(String filename){
        try {
            File fXmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            
            NodeList list = doc.getChildNodes();
            Node main = list.item(0);
            list = main.getChildNodes();
            
            for(int i = 0; i < list.getLength(); i++){
                Node node = list.item(i);
                
                if(!node.getNodeName().startsWith("#")){
                    String value = node.getTextContent().trim();
                    System.out.println("Config load: " + node.getNodeName() + " = " + value);
                    sOptions.put(node.getNodeName(), value);
                }
            }
            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            //Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getOption(String key) {
        return sOptions.get(key);
    }

    public void putOption(String key, String value) {
        sOptions.put(key, value);
    }

    public int getIntOption(String key) {
        int ret = -1;
        if(key == null) return ret;
        try {
            ret = Integer.parseInt(sOptions.get(key));
        } catch (NumberFormatException ex) {
            ;
        }
        return ret;
    }

    public synchronized double getDoubleOption(String key) {
        double ret = Double.NaN;
        if(key == null) return ret;
        try {
            ret = Double.parseDouble(sOptions.get(key));
        } catch (NumberFormatException ex) {
            ;
        }
        return ret;
    }
    
    public synchronized boolean getBooleanOption(String key){
        boolean ret = false;
        if(key == null) return ret;
        String value = sOptions.get(key).toLowerCase();

        if(value != null){
            if(value.equals("true") || value.equals("t")){
                ret = true;
            }
        }
        
        return ret;
    }
}
