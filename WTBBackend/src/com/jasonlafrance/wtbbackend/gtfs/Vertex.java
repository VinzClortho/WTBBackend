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
 * GTFS Vertex (shapes) class.
 * 
 * @author Jason LaFrance
 */
public final class Vertex extends GTFSParser {

	private static final String _filename = "//shapes.txt";

	/**
	 * Generate a hashcode for a given longitude and latitude
	 * 
	 * @param lat
	 *            Latitude
	 * @param lon
	 *            Longitude
	 * @return The hashcode
	 */
	private static int genHashCode(double lat, double lon) {
		return (int) (Double.doubleToRawLongBits(lat) * 31 + Double
				.doubleToRawLongBits(lon));
	}

	private String _shape_id = null;
	private double _shape_pt_lat = 0.0, _shape_pt_lon = 0.0;
	private int _shape_pt_sequence = -1;

	private double _shape_dist_traveled = 0.0;

	private Integer mHashCode = null;

	private StopAdapter _stop = null;

	/**
	 * Stub constructor
	 */
	public Vertex() {
	}

	/**
	 * Create a Vertex object from given longitude and latitude
	 * 
	 * @param inLat
	 *            Latidude
	 * @param inLon
	 *            Longitude
	 */
	public Vertex(double inLat, double inLon) {
		this("", inLat, inLon, -1, 0.0);
	}

	/**
	 * Stub constructor
	 * 
	 * @param inLine A String
	 */
	public Vertex(String inLine) {
	}

	/**
	 * Create a Vertex object from given values
	 * 
	 * @param inID
	 *            Shape ID
	 * @param inLat
	 *            Latitude
	 * @param inLon
	 *            Longitude
	 * @param inSequence
	 *            Sequence in shape
	 * @param inDist
	 *            Distance traveled in shape
	 */
	public Vertex(String inID, double inLat, double inLon, int inSequence,
			double inDist) {
		_shape_id = inID;
		_shape_pt_lat = inLat;
		_shape_pt_lon = inLon;
		_shape_pt_sequence = inSequence;
		_shape_dist_traveled = inDist;

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

	/**
	 * Get distance traveled
	 * 
	 * @return Distance traveled
	 */
	public double get_shape_dist_traveled() {
		return _shape_dist_traveled;
	}

	/**
	 * Get the shape ID
	 * 
	 * @return Shape ID
	 */
	public String get_shape_id() {
		return _shape_id;
	}

	/**
	 * Get latitude
	 * 
	 * @return Latitude
	 */
	public double get_shape_pt_lat() {
		return _shape_pt_lat;
	}

	/**
	 * Get longitude
	 * 
	 * @return Longitude
	 */
	public double get_shape_pt_lon() {
		return _shape_pt_lon;
	}

	/**
	 * Get vertex's sequence order in the shape
	 * 
	 * @return Sequence order
	 */
	public int get_shape_pt_sequence() {
		return _shape_pt_sequence;
	}

	/**
	 * Get distance from this Vertex to another Vertex
	 * 
	 * @param in
	 *            Vertex to get distance to
	 * @return Distance between vertices
	 */
	public double getDistance(Vertex in) {
		return Math.sqrt(Math.pow(_shape_pt_lat - in.get_shape_pt_lat(), 2.0)
				+ Math.pow(_shape_pt_lon - in.get_shape_pt_lon(), 2.0));
	}

	/**
	 * Get distance in meters from this Vertex to a given set of coordinates
	 * 
	 * @param inLat
	 *            Latitude to ...
	 * @param inLon
	 *            Longitude to...
	 * @return Distance between this Vertex and given coordinates
	 */
	public double getDistanceInMeters(double inLat, double inLon) {
		return com.jasonlafrance.wtbbackend.wtb_util.GPSCalc
				.getDistanceInMeters(_shape_pt_lat, _shape_pt_lon, inLat, inLon);
	}

	/**
	 * Get distance in meters from this Vertex to a given Vertex
	 * 
	 * @param in
	 *            Vertex to get distance to
	 * @return Distance between this Vertex and given Vertex
	 */
	public double getDistanceInMeters(Vertex in) {
		return com.jasonlafrance.wtbbackend.wtb_util.GPSCalc
				.getDistanceInMeters(_shape_pt_lat, _shape_pt_lon,
						in.get_shape_pt_lat(), in.get_shape_pt_lon());
	}

	/**
	 * Get the GTFS file name associated with this object.
	 * 
	 * @return The GTFS file name associated with this object.
	 */
	@Override
	public String getFilename() {
		return _filename;
	}

	/**
	 * Get this objects unique ID.
	 * 
	 * @return This objects unique ID
	 */
	@Override
	public int getID() {
		return _shape_id.hashCode();
	}

	/**
	 * Get squared distance to another Vertex
	 * 
	 * @param in
	 *            Vertex to get squared distance to
	 * @return Squared distance between vertices
	 */
	public double getRawDistance(Vertex in) {
		double dLat = _shape_pt_lat - in.get_shape_pt_lat();
		double dLon = _shape_pt_lon - in.get_shape_pt_lon();
		return (dLat * dLat) + (dLon * dLon);
	}

	/**
	 * Get squared distance to a set of coordinates
	 * 
	 * @param inLat
	 *            Latitude
	 * @param inLon
	 *            Longitude
	 * @return The squared distance between here and there
	 */
	public double getRawDistanceInMeters(double inLat, double inLon) {
		return com.jasonlafrance.wtbbackend.wtb_util.GPSCalc
				.getRawDistanceInMeters(_shape_pt_lat, _shape_pt_lon, inLat,
						inLon);
	}

	/**
	 * Get squared distance to another Vertex
	 * 
	 * @param in
	 *            Vertex to get squared distance to
	 * @return The distance between vertices
	 */
	public double getRawDistanceInMeters(Vertex in) {
		return com.jasonlafrance.wtbbackend.wtb_util.GPSCalc
				.getRawDistanceInMeters(_shape_pt_lat, _shape_pt_lon,
						in.get_shape_pt_lat(), in.get_shape_pt_lon());
	}

	/**
	 * Get stop associated with this Vertex, if any
	 * 
	 * @return Associated Stop wrapped in a StopAdapter, or null
	 */
	public StopAdapter getStop() {
		return _stop;
	}

	@Override
	public int hashCode() {
		// Lazy initialize the hashcode...
		if (mHashCode == null)
			mHashCode = genHashCode(_shape_pt_lat, _shape_pt_lon);
		return mHashCode;
	}

	/**
	 * Check if there is a Stop associated with this Vertex
	 * 
	 * @return If a stop is associated...
	 */
	public boolean isStop() {
		return _stop instanceof StopAdapter;
	}

	/**
	 * Set distance traveled
	 * 
	 * @param in
	 *            Distance traveled
	 */
	public void set_shape_dist_traveled(double in) {
		_shape_dist_traveled = in;
	}

	/**
	 * Set distance traveled
	 * 
	 * @param in
	 *            Distance traveled
	 */
	public void set_shape_dist_traveled(String in) {
		try {
			_shape_dist_traveled = Double.parseDouble(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set shape ID
	 * 
	 * @param in
	 *            Shape ID
	 */
	public void set_shape_id(String in) {
		_shape_id = in;
	}

	/**
	 * Set latitude
	 * 
	 * @param in
	 *            Latitude
	 */
	public void set_shape_pt_lat(double in) {
		_shape_pt_lat = in;
		mHashCode = genHashCode(_shape_pt_lat, _shape_pt_lon);
	}

	/**
	 * Set latitude
	 * 
	 * @param in
	 *            Latitude
	 */
	public void set_shape_pt_lat(String in) {
		try {
			_shape_pt_lat = Double.parseDouble(in);
			mHashCode = genHashCode(_shape_pt_lat, _shape_pt_lon);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set longitude
	 * 
	 * @param in
	 *            Longitude
	 */
	public void set_shape_pt_lon(double in) {
		_shape_pt_lon = in;
		mHashCode = genHashCode(_shape_pt_lat, _shape_pt_lon);
	}

	/**
	 * Set longitude
	 * 
	 * @param in
	 *            Longitude
	 */
	public void set_shape_pt_lon(String in) {
		try {
			_shape_pt_lon = Double.parseDouble(in);
			mHashCode = genHashCode(_shape_pt_lat, _shape_pt_lon);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set vertex sequence in shape
	 * 
	 * @param in
	 *            Sequence
	 */
	public void set_shape_pt_sequence(int in) {
		_shape_pt_sequence = in;
	}

	/**
	 * Set vertex sequence in shape
	 * 
	 * @param in
	 *            Sequence
	 */
	public void set_shape_pt_sequence(String in) {
		try {
			_shape_pt_sequence = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set Vertex coordinates
	 * 
	 * @param inLat
	 *            Latitude
	 * @param inLon
	 *            Longitude
	 */
	public void setCoordinates(double inLat, double inLon) {
		_shape_pt_lat = inLat;
		_shape_pt_lon = inLon;
		mHashCode = genHashCode(inLat, inLon);
	}

	/**
	 * Associate a Stop with this Vertex
	 * 
	 * @param in
	 *            A Stop wrapped in a StopAdapter
	 */
	public void setStop(StopAdapter in) {
		_stop = in;
	}

	/**
	 * Get a KML line representing this Vertex
	 * 
	 * @return KML line
	 */
	public String toKMLLine() {
		return _shape_pt_lon + "," + _shape_pt_lat + ",0\n";
	}

	@Override
	public String toString() {
		return _shape_id + "," + _shape_pt_lat + "," + _shape_pt_lon + ","
				+ _shape_pt_sequence + "," + _shape_dist_traveled;
	}
}
