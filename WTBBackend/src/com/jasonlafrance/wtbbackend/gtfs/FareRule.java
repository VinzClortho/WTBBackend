package com.jasonlafrance.wtbbackend.gtfs;

import static com.jasonlafrance.wtbbackend.gtfs.GTFSParser._headers;

import java.util.ArrayList;

import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 *
 * @author Jason LaFrance
 */
public class FareRule extends GTFSParser {

    private static final String _filename = "//fare_rules.txt";

    private String _fare_id = null;
    private String _route_id = null;
    private String _origin_id = null;
    private String _destination_id = null;
    private String _contains_id = null;

    public FareRule() {;
    }

    public FareRule(
            String inID,
            String inRouteID,
            String inOriginID,
            String inDestID,
            String inContID
    ) {
        _fare_id = inID;
        _route_id = inRouteID;
        _origin_id = inOriginID;
        _destination_id = inDestID;
        _contains_id = inContID;
    }

    public FareRule(String inLine) {
    }
    
    public FareRule(String inLine, int id) {
        ArrayList<String> h = _headers.get(id).get(_filename);
        String[] f = CSVParser.parseLine(inLine);

        if (f.length != h.size()) {
            return;
        }

        for (int i = 0; i < f.length; i++) {
            switch (h.get(i)) {
                case "contains_id":
                    set_contains_id(f[i]);
                    break;
                case "destination_id":
                    set_destination_id(f[i]);
                    break;
                case "fare_id":
                    set_fare_id(f[i]);
                    break;
                case "origin_id":
                    set_origin_id(f[i]);
                    break;
                case "route_id":
                    set_route_id(f[i]);
                    break;
                default:
                    break;
            }
        }
    }

    public void set_fare_id(String in) {
        _fare_id = in;
    }

    public void set_route_id(String in) {
        _route_id = in;
    }

    public void set_origin_id(String in) {
        _origin_id = in;
    }

    public void set_destination_id(String in) {
        _destination_id = in;
    }

    public void set_contains_id(String in) {
        _contains_id = in;
    }

    @Override
    public int getID() {
        return _fare_id.hashCode();
    }

    public String get_fare_id() {
        return _fare_id;
    }

    public String get_route_id() {
        return _route_id;
    }

    public String get_origin_id() {
        return _origin_id;
    }

    public String get_destination_id() {
        return _destination_id;
    }

    public String get_contains_id() {
        return _contains_id;
    }

    @Override
    public String toString() {
        return _fare_id + ","
                + _route_id + ","
                + _origin_id + ","
                + _destination_id + ","
                + _contains_id + "\n";
    }

    @Override
    public String getFilename() {
        return _filename;
    }
}
