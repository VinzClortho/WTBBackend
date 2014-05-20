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
public final class Vertex extends GTFSParser {

    private static final String _filename = "//shapes.txt";

    private String _shape_id = null;
    private double _shape_pt_lat = 0.0, _shape_pt_lon = 0.0;
    private int _shape_pt_sequence = -1;
    private double _shape_dist_traveled = 0.0;

    private int mHashCode = 0;

    private StopAdapter _stop = null;

    public Vertex() {;
    }

    public Vertex(double inLat, double inLon) {
        this("", inLat, inLon, -1, 0.0);
    }

    public Vertex(
            String inID,
            double inLat,
            double inLon,
            int inSequence,
            double inDist) {
        _shape_id = inID;
        _shape_pt_lat = inLat;
        _shape_pt_lon = inLon;
        _shape_pt_sequence = inSequence;
        _shape_dist_traveled = inDist;
        mHashCode = genHashCode(inLat, inLon);
    }

    public Vertex(String inLine) {
    }

    public Vertex(String inLine, int id) {
        ArrayList<String> h = _headers.get(id).get(_filename);
        String[] f = CSVParser.parseLine(inLine);
        if (f.length != h.size()) {
            return;
        }

        for (int i = 0; i < f.length; i++) {
            switch (h.get(i)) {
                case "shape_dist_traveled":
                    set_shape_dist_traveled(f[i]);
                    break;
                case "shape_id":
                    set_shape_id(f[i]);
                    break;
                case "shape_pt_lat":
                    set_shape_pt_lat(f[i]);
                    break;
                case "shape_pt_lon":
                    set_shape_pt_lon(f[i]);
                    break;
                case "shape_pt_sequence":
                    set_shape_pt_sequence(f[i]);
                    break;
                default:
                    break;
            }
        }
    }

    private static int genHashCode(double lat, double lon) {
        return (int) (Double.doubleToRawLongBits(lat) * 31 + Double.doubleToRawLongBits(lon));
    }

    public int hashCode() {
        return mHashCode;
    }

    public boolean equals(Vertex in) {
        if (in == null) {
            return false;
        }
        if (_shape_pt_lat == in.get_shape_pt_lat()
                && _shape_pt_lon == in.get_shape_pt_lon()) {
            return true;
        }
        return false;
    }

    public void set_shape_id(String in) {
        _shape_id = in;
    }

    public void setCoordinates(double inLat, double inLon) {
        _shape_pt_lat = inLat;
        _shape_pt_lon = inLon;
        mHashCode = genHashCode(inLat, inLon);
    }

    public void set_shape_pt_lat(double in) {
        _shape_pt_lat = in;
        mHashCode = genHashCode(_shape_pt_lat, _shape_pt_lon);
    }

    public void set_shape_pt_lat(String in) {
        try {
            _shape_pt_lat = Double.parseDouble(in);
            mHashCode = genHashCode(_shape_pt_lat, _shape_pt_lon);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_shape_pt_lon(double in) {
        _shape_pt_lon = in;
        mHashCode = genHashCode(_shape_pt_lat, _shape_pt_lon);
    }

    public void set_shape_pt_lon(String in) {
        try {
            _shape_pt_lon = Double.parseDouble(in);
            mHashCode = genHashCode(_shape_pt_lat, _shape_pt_lon);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_shape_pt_sequence(int in) {
        _shape_pt_sequence = in;
    }

    public void set_shape_pt_sequence(String in) {
        try {
            _shape_pt_sequence = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_shape_dist_traveled(double in) {
        _shape_dist_traveled = in;
    }

    public void set_shape_dist_traveled(String in) {
        try {
            _shape_dist_traveled = Double.parseDouble(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void setStop(StopAdapter in) {
        _stop = in;
    }

    public String get_shape_id() {
        return _shape_id;
    }

    @Override
    public int getID() {
        return _shape_id.hashCode();
    }

    public double get_shape_pt_lat() {
        return _shape_pt_lat;
    }

    public double get_shape_pt_lon() {
        return _shape_pt_lon;
    }

    public int get_shape_pt_sequence() {
        return _shape_pt_sequence;
    }

    public double get_shape_dist_traveled() {
        return _shape_dist_traveled;
    }

    public StopAdapter getStop() {
        return _stop;
    }

    public boolean isStop() {
        return _stop instanceof StopAdapter;
    }

    public double getDistance(Vertex in) {
        return Math.sqrt(Math.pow(_shape_pt_lat - in.get_shape_pt_lat(), 2.0) + Math.pow(_shape_pt_lon - in.get_shape_pt_lon(), 2.0));
    }

    public double getRawDistance(Vertex in) {
        double dLat = _shape_pt_lat - in.get_shape_pt_lat();
        double dLon = _shape_pt_lon - in.get_shape_pt_lon();
        return (dLat * dLat) + (dLon * dLon);
    }

    public double getRawDistanceInMeters(Vertex in) {
        return com.jasonlafrance.wtbbackend.wtb_util.GPSCalc.getRawDistanceInMeters(_shape_pt_lat, _shape_pt_lon, in.get_shape_pt_lat(), in.get_shape_pt_lon());
    }

    public double getRawDistanceInMeters(double inLat, double inLon) {
        return com.jasonlafrance.wtbbackend.wtb_util.GPSCalc.getRawDistanceInMeters(_shape_pt_lat, _shape_pt_lon, inLat, inLon);
    }

    public double getDistanceInMeters(Vertex in) {
        return com.jasonlafrance.wtbbackend.wtb_util.GPSCalc.getDistanceInMeters(_shape_pt_lat, _shape_pt_lon, in.get_shape_pt_lat(), in.get_shape_pt_lon());
    }

    public double getDistanceInMeters(double inLat, double inLon) {
        return com.jasonlafrance.wtbbackend.wtb_util.GPSCalc.getDistanceInMeters(_shape_pt_lat, _shape_pt_lon, inLat, inLon);
    }

    @Override
    public String toString() {
        return _shape_id + ","
                + _shape_pt_lat + ","
                + _shape_pt_lon + ","
                + _shape_pt_sequence + ","
                + _shape_dist_traveled;
    }

    public String toKMLLine() {
        return _shape_pt_lon + "," + _shape_pt_lat + ",0\n";
    }

    @Override
    public String getFilename() {
        return _filename;
    }
}
