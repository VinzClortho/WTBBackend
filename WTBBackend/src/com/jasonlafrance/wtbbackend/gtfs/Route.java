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

package com.jasonlafrance.wtbbackend.gtfs;

import static com.jasonlafrance.wtbbackend.gtfs.GTFSParser._headers;

import java.util.ArrayList;

import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 *
 * @author Jason LaFrance
 */
public final class Route extends GTFSParser {

    private static final String _filename = "//routes.txt";
    private static final double _threshold = 1.0 / 69.0;

    public static void addVertices(ArrayList<ArrayList<Vertex>> master, ArrayList<Vertex> in, int id) {
        if (in == null || in.isEmpty()) {
            return;
        }

        System.out.println("in addVertices...");
        while (id >= master.size()) {
            System.out.println("adding to list...");
            master.add(new ArrayList<Vertex>());
        }

        if (master.get(id).isEmpty()) {
            master.get(id).addAll(in);
        } else {
            Vertex mLast = master.get(id).get(master.size() - 1);
            Vertex inFirst = in.get(id);
            Vertex inLast = in.get(in.size() - 1);
            if (mLast.getDistance(inFirst) < _threshold) {
                master.get(id).addAll(in);
            } else if (mLast.getDistance(inLast) < _threshold) {
                // add vertices in reverse
                for (int v = in.size() - 1; v >= 0; v--) {
                    master.get(id).add(in.get(v));
                }
            }
        }

    }

    public static void addTrip(ArrayList<ArrayList<Trip>> master, Trip in, int id) {
        if (id > -1) {
            while (id >= master.size()) {
                master.add(new ArrayList<Trip>());
            }
            master.get(id).add(in);
        }
    }

    private String _route_id = null;
    private String _agency_id = null;
    private String _route_short_name = null;
    private String _route_long_name = null;
    private String _route_desc = null;
    private int _route_type = -1;
    private String _route_url = null;
    private String _route_color = "FFFFFF";
    private String _route_text_color = "000000";
    private Agency mAgency;

    private ArrayList<ArrayList<Vertex>> _vertices = new ArrayList<>();
    private ArrayList<ArrayList<Trip>> _trips = new ArrayList<>();

    public Route() {;
    }

    public Route(
            String inID,
            String inAID,
            String inShort,
            String inLong,
            String inDesc,
            int inType,
            String inUrl,
            String inColor,
            String inTextColor
    ) {
        _route_id = inID;
        _agency_id = inAID;
        _route_short_name = inShort;
        _route_long_name = inLong;
        _route_desc = inDesc;
        _route_type = inType;
        _route_url = inUrl;
        _route_color = inColor;
        _route_text_color = inTextColor;
    }

    public Route(String inLine) {
    }

    public Route(String inLine, int id) {
        ArrayList<String> h = _headers.get(id).get(_filename);
        String[] f = CSVParser.parseLine(inLine);

        if (f.length != h.size()) {
            return;
        }

        for (int i = 0; i < f.length; i++) {
            switch (h.get(i)) {
                case "agency_id":
                    set_agency_id(f[i]);
                    break;
                case "route_color":
                    set_route_color(f[i]);
                    break;
                case "route_desc":
                    set_route_desc(f[i]);
                    break;
                case "route_id":
                    set_route_id(f[i]);
                    break;
                case "route_long_name":
                    set_route_long_name(f[i]);
                    break;
                case "route_short_name":
                    set_route_short_name(f[i]);
                    break;
                case "route_text_color":
                    set_route_text_color(f[i]);
                    break;
                case "route_type":
                    set_route_type(f[i]);
                    break;
                case "route_url":
                    set_route_url(f[i]);
                    break;
                default:
                    break;
            }
        }
    }

    public void set_route_id(String in) {
        _route_id = in;
    }

    public void set_agency_id(String in) {
        _agency_id = in;
    }
    
    public void set_route_short_name(String in) {
        _route_short_name = in.trim().replaceAll("\"", "");
    }

    public void set_route_long_name(String in) {
        _route_long_name = in.trim().replaceAll("\"", "");
    }

    public void set_route_desc(String in) {
        _route_desc = in;
    }

    public void set_route_type(int in) {
        _route_type = in;
    }

    public void set_route_type(String in) {
        try {
            _route_type = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_route_url(String in) {
        _route_url = in;
    }

    public void set_route_color(String in) {
        _route_color = in;
    }

    public void set_route_text_color(String in) {
        _route_text_color = in;
    }

    // add vertices to default list
    public void addVertices(ArrayList<Vertex> in) {
        if (in == null || in.isEmpty()) {
            return;
        }
        addVertices(in, 0);
    }

    public void addVertices(ArrayList<Vertex> in, int id) {
        addVertices(_vertices, in, id);
    }

    public void addTrip(Trip in) {
        addTrip(_trips, in, 0);
    }

    @Override
    public int getID() {
        return _route_id.hashCode();
    }

    public String get_route_id() {
        return _route_id;
    }

    public String get_agency_id() {
        return _agency_id;
    }
    
    public Agency getAgency(){
        return mAgency;
    }
    
    public String getName(){
        if(_route_short_name == null){
            return _route_long_name;
        } else {
            return _route_short_name;
        }
    }
    
    public void setAgency(Agency in){
        mAgency = in;
    }

    public String get_route_short_name() {
        return _route_short_name;
    }

    public String get_route_long_name() {
        return _route_long_name;
    }

    public String get_route_desc() {
        return _route_desc;
    }

    public int get_route_type() {
        return _route_type;
    }

    public String get_route_url() {
        return _route_url;
    }

    public String get_route_color() {
        return _route_color;
    }

    public String get_route_text_color() {
        return _route_text_color;
    }

    public ArrayList<Vertex> getVertices() {
        if (!_vertices.isEmpty()) {
            return _vertices.get(0);
        } else {
            return null;
        }
    }

    public ArrayList<Trip> getTrips() {
        return _trips.get(0);
    }

    public ArrayList<Trip> getTrips(int in) {
        if (in > -1 && in < _trips.size()) {
            return _trips.get(in);
        } else {
            return null;
        }
    }

    public int getServices() {
        return _trips.size();
    }

    public int getTripsCount(int id) {
        if (id >= 0 && id < _trips.size()) {
            return _trips.get(id).size();
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return _route_id + ","
                + _route_short_name + ","
                + _route_long_name + ","
                + _route_desc + ","
                + _route_type + ","
                + _route_url + ","
                + _route_color + ","
                + _route_text_color + "\n";
    }

    @Override
    public String getFilename() {
        return _filename;
    }

}
