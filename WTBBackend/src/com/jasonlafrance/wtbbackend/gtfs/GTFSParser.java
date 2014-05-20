package com.jasonlafrance.wtbbackend.gtfs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 *
 * @author Jason LaFrance
 */
public abstract class GTFSParser {

    protected static final HashMap<Integer, HashMap<String, ArrayList<String>>> _headers = new HashMap<>();
    
    public static void setHeader(int inID, String inFilename, String inHeader) {
        synchronized (_headers) {
            if (_headers.get(inID) == null) { // add new header map for GTFS id
                _headers.put(inID, new HashMap<String, ArrayList<String>>());
            }

            HashMap<String, ArrayList<String>> map = _headers.get(inID);
            ArrayList<String> list = new ArrayList<>();

            String[] f = CSVParser.parseLine(inHeader);
            for (int i = 0; i < f.length; i++) {
                f[i] = f[i].trim().replaceAll("\"", "");
            }
            list.addAll(Arrays.asList(f));
            map.put(inFilename, list);
            _headers.put(inID, map);
        }
    }

    public static synchronized ArrayList<String> getHeader(int inID, String inFilename) {
        return _headers.get(inID).get(inFilename);
    }

    public static Constructor<?> getConstructor(String inClass, boolean touch) {
        // Create a class object from the class matching the table name
        Class<?> clazz = null;
        Constructor<?> cons = null;
        try {
            clazz = Class.forName(inClass);
        } catch (ClassNotFoundException ex) {
            //Logger.getLogger(GTFSParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (clazz != null) {
                if (touch) {
                    cons = clazz.getConstructor(String.class);
                } else {
                    cons = clazz.getConstructor(String.class, int.class);
                }
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            //Logger.getLogger(GTFSParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cons;
    }

    public static String getFilename(String inClass) {
        String ret = null;

        Constructor<?> cons = GTFSParser.getConstructor(inClass, true);

        GTFSParser parser = null;
        try {
            if (cons != null) {
                parser = (GTFSParser) cons.newInstance("");
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            //Logger.getLogger(GTFSParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (parser != null) {
            ret = parser.getFilename();
        }
        return ret;
    }

    private int sGTFS_ID;

    protected void setGTFS_ID(int id) {
        sGTFS_ID = id;
    }

    public int getGTFS_ID() {
        return sGTFS_ID;
    }

    public GTFS getGTFS() {
        return GTFS.getGTFS(sGTFS_ID);
    }

    public void parse(int inID, String inLine) {
        String[] f = CSVParser.parseLine(inLine);

        sGTFS_ID = inID;

        //System.out.println("Parsing: " + getGTFS_ID() + "   " + inLine);
        ArrayList<String> inHeader = _headers.get(inID).get(this.getFilename());

        if (inHeader == null || f == null || f.length == 0 || f.length != inHeader.size()) {
            return;
        }

        Class clazz = this.getClass();

        for (int i = 0; i < f.length; i++) {
            if (f[i] != null && f[i].length() > 0) {
                try {
                    clazz.getMethod("set_" + inHeader.get(i), String.class)
                            .invoke(this, f[i].replace("\"", ""));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                    Logger.getLogger(GTFSParser.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    public abstract String getFilename();

    public abstract int getID();

}
