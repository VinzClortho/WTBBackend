package com.jasonlafrance.wtbbackend.gtfs;

import static com.jasonlafrance.wtbbackend.gtfs.GTFSParser._headers;

import java.util.ArrayList;
import java.util.Comparator;

import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 *
 * @author Jason LaFrance
 */
public final class Stop extends GTFSParser implements Comparator<Stop> {

    private static final String _filename = "//stops.txt";

    private String _stop_id = null;
    private String _stop_code = null;
    private String _stop_name = null;
    private String _stop_desc = null;
    private double _stop_lat = 0.0;
    private double _stop_lon = 0.0;
    private String _zone_id = null;
    private String _stop_url = null;
    private int _location_type = -1;
    private String _parent_station = null;
    private String _stop_timezone = null;
    private int _wheelchair_boarding = -1;
    private String _position = null;
    private String _direction = null;
    private Route _route = null;
    private Stop _nextStop = null;
    private StopTime _stopTime = null;

    public Stop() {;
    }

    public Stop(
            String inID,
            String inName,
            String inDesc,
            double inLat,
            double inLon,
            String inZoneID,
            String inUrl,
            int inType,
            String inParent) {
        _stop_id = inID;
        _stop_name = inName;
        _stop_desc = inDesc;
        _stop_lat = inLat;
        _stop_lon = inLon;
        _zone_id = inZoneID;
        _stop_url = inUrl;
        _location_type = inType;
        _parent_station = inParent;
    }

    public Stop(String inLine) {
    }

    public Stop(String inLine, int id) {
        ArrayList<String> h = _headers.get(id).get(_filename);
        String[] f = CSVParser.parseLine(inLine);

        if (f.length != h.size()) {
            return;
        }

        for (int i = 0; i < f.length; i++) {
            switch (h.get(i)) {
                case "direction":
                    set_direction(f[i]);
                    break;
                case "location_type":
                    set_location_type(f[i]);
                    break;
                case "parent_station":
                    set_parent_station(f[i]);
                    break;
                case "position":
                    set_position(f[i]);
                    break;
                case "stop_code":
                    set_stop_code(f[i]);
                    break;
                case "stop_desc":
                    set_stop_desc(f[i]);
                    break;
                case "stop_id":
                    set_stop_id(f[i]);
                    break;
                case "stop_lat":
                    set_stop_lat(f[i]);
                    break;
                case "stop_lon":
                    set_stop_lon(f[i]);
                    break;
                case "stop_name":
                    set_stop_name(f[i]);
                    break;
                case "stop_timezone":
                    set_stop_timezone(f[i]);
                    break;
                case "stop_url":
                    set_stop_url(f[i]);
                    break;
                case "wheelchair_boarding":
                    set_wheelchair_boarding(f[i]);
                    break;
                case "zone_id":
                    set_zone_id(f[i]);
                    break;
                default:
                    break;
            }
        }
    }

    public void setNextStop(Stop in) {
        _nextStop = in;
    }

    public Stop getNextStop() {
        return _nextStop;
    }

    public void set_stop_id(String in) {
        _stop_id = in;
    }

    public void set_stop_code(String in) {
        _stop_code = in;
    }

    public void set_stop_name(String in) {
        _stop_name = in;
    }

    public void set_stop_desc(String in) {
        _stop_desc = in;
    }

    public void set_stop_lat(double in) {
        _stop_lat = in;
    }

    public void set_stop_lat(String in) {
        try {
            _stop_lat = Double.parseDouble(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_stop_lon(double in) {
        _stop_lon = in;
    }

    public void set_stop_lon(String in) {
        try {
            _stop_lon = Double.parseDouble(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void setCoordinates(double inLat, double inLon) {
        _stop_lat = inLat;
        _stop_lon = inLon;
    }

    public void set_zone_id(String in) {
        _zone_id = in;
    }

    public void set_stop_url(String in) {
        _stop_url = in;
    }

    public void set_location_type(int in) {
        _location_type = in;
    }

    public void set_location_type(String in) {
        try {
            _location_type = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_parent_station(String in) {
        _parent_station = in;
    }

    public void set_stop_timezone(String in) {
        _stop_timezone = in;
    }

    public void set_wheelchair_boarding(int in) {
        _wheelchair_boarding = in;
    }

    public void set_wheelchair_boarding(String in) {
        try {
            _wheelchair_boarding = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void setStopTime(StopTime in) {
        _stopTime = in;
    }

    // getters
    @Override
    public int getID() {
        return _stop_id.hashCode();
    }

    public String get_stop_id() {
        return _stop_id;
    }

    public String get_stop_code() {
        return _stop_code;
    }

    public String get_stop_name() {
        return _stop_name;
    }

    public String get_stop_desc() {
        return _stop_desc;
    }

    public double get_stop_lat() {
        return _stop_lat;
    }

    public double get_stop_lon() {
        return _stop_lon;
    }

    public Vertex getVertex() {
        return new Vertex("", _stop_lat, _stop_lon, 0, 0.0);
    }

    public String get_zone_id() {
        return _zone_id;
    }

    public String get_stop_url() {
        return _stop_url;
    }

    public int get_location_type() {
        return _location_type;
    }

    public String get_parent_station() {
        return _parent_station;
    }

    public String get_stop_timezone() {
        return _stop_timezone;
    }

    public int get_wheelchair_boarding() {
        return _wheelchair_boarding;
    }

    public StopTime getStopTime() {
        return _stopTime;
    }

    public void setRoute(Route in) {
        _route = in;
    }

    public Route getRoute() {
        return _route;
    }
    /*
     @Override
     public Stop clone() {
     Stop s
     = new Stop(
     _stop_id,
     _stop_name,
     _stop_desc,
     _stop_lat,
     _stop_lon,
     _zone_id,
     _stop_url,
     _location_type,
     _parent_station);
     s.setGTFS_ID(this.getGTFS_ID());

     return s;
     }
     */

    @Override
    public String toString() {
        return _stop_id + ","
                + _stop_name + ","
                + _stop_desc + ","
                + _stop_lat + ","
                + _stop_lon + ","
                + _zone_id + ","
                + _stop_url + ","
                + _location_type + ","
                + _parent_station + "\n";
    }

    @Override
    public int compare(Stop o1, Stop o2) {
        //if(o1.getStopTime()
        return 0;
    }

    public int compare(Stop in) {
        return this.compare(this, in);
    }

    @Override
    public String getFilename() {
        return _filename;
    }

    /**
     * @return the _position
     */
    public String get_position() {
        return _position;
    }

    /**
     * @param _position the _position to set
     */
    public void set_position(String _position) {
        this._position = _position;
    }

    /**
     * @return the _direction
     */
    public String get_direction() {
        return _direction;
    }

    /**
     * @param _direction the _direction to set
     */
    public void set_direction(String _direction) {
        this._direction = _direction;
    }
}
