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
import java.util.Comparator;

import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 * GTFS Stop class.
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

	/**
	 * Stub constructor
	 */
	public Stop() {
	}

	/**
	 * Stub constructor
	 * 
	 * @param inLine
	 *            A String
	 */
	public Stop(String inLine) {
	}

	/**
	 * Create a Stop object from the given values
	 * 
	 * @param inID
	 *            Stop ID
	 * @param inName
	 *            Name
	 * @param inDesc
	 *            Description
	 * @param inLat
	 *            Latitude
	 * @param inLon
	 *            Longitude
	 * @param inZoneID
	 *            Zone ID
	 * @param inUrl
	 *            URL
	 * @param inType
	 *            Location type
	 * @param inParent
	 *            Parent station
	 */
	public Stop(String inID, String inName, String inDesc, double inLat,
			double inLon, String inZoneID, String inUrl, int inType,
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

	public int compare(Stop in) {
		return this.compare(this, in);
	}

	// TODO need this?
	@Override
	public int compare(Stop o1, Stop o2) {
		// if(o1.getStopTime()
		return 0;
	}

	/**
	 * Get the direction
	 * 
	 * @return Direction
	 */
	public String get_direction() {
		return _direction;
	}

	/**
	 * Get the location type
	 * 
	 * @return Location type
	 */
	public int get_location_type() {
		return _location_type;
	}

	/**
	 * Get the parent station
	 * 
	 * @return Parent station
	 */
	public String get_parent_station() {
		return _parent_station;
	}

	/**
	 * Get the position
	 * 
	 * @return Position
	 */
	public String get_position() {
		return _position;
	}

	/**
	 * Get the stop code
	 * 
	 * @return Stop code
	 */
	public String get_stop_code() {
		return _stop_code;
	}

	/**
	 * Get the stop description
	 * 
	 * @return Stop description
	 */
	public String get_stop_desc() {
		return _stop_desc;
	}

	/**
	 * Get the stop ID
	 * 
	 * @return Stop ID
	 */
	public String get_stop_id() {
		return _stop_id;
	}

	/**
	 * Get the latitude
	 * 
	 * @return Latitude
	 */
	public double get_stop_lat() {
		return _stop_lat;
	}

	/**
	 * Get the longitude
	 * 
	 * @return Longitude
	 */
	public double get_stop_lon() {
		return _stop_lon;
	}

	/**
	 * Get stop name
	 * 
	 * @return Name
	 */
	public String get_stop_name() {
		return _stop_name;
	}

	/**
	 * Get time zone
	 * 
	 * @return Time zone
	 */
	public String get_stop_timezone() {
		return _stop_timezone;
	}

	/**
	 * Get stop's URL
	 * 
	 * @return URL
	 */
	public String get_stop_url() {
		return _stop_url;
	}

	/**
	 * Get if wheelchairs can board here
	 * 
	 * @return If wheelchairs can board here
	 */
	public int get_wheelchair_boarding() {
		return _wheelchair_boarding;
	}

	/**
	 * Get the zone ID
	 * 
	 * @return Zone ID
	 */
	public String get_zone_id() {
		return _zone_id;
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
		return _stop_id.hashCode();
	}

	/**
	 * Get next stop
	 * 
	 * @return Next Stop
	 */
	public Stop getNextStop() {
		return _nextStop;
	}

	/**
	 * Get this stop's Route object
	 * 
	 * @return This stop's Route
	 */
	public Route getRoute() {
		return _route;
	}

	/*
	 * @Override public Stop clone() { Stop s = new Stop( _stop_id, _stop_name,
	 * _stop_desc, _stop_lat, _stop_lon, _zone_id, _stop_url, _location_type,
	 * _parent_station); s.setGTFS_ID(this.getGTFS_ID());
	 * 
	 * return s; }
	 */

	/**
	 * Get StopTime object
	 * 
	 * @return This stop's StopTime object
	 */
	public StopTime getStopTime() {
		return _stopTime;
	}

	/**
	 * Get a Vertex representation of this Stop
	 * 
	 * @return Vertex version of this stop
	 */
	public Vertex getVertex() {
		return new Vertex("", _stop_lat, _stop_lon, 0, 0.0);
	}

	/**
	 * Set the direction
	 * 
	 * @param _direction
	 *            The direction to set
	 */
	public void set_direction(String _direction) {
		this._direction = _direction;
	}

	/**
	 * Set the location type
	 * 
	 * @param in
	 *            Location type
	 */
	public void set_location_type(int in) {
		_location_type = in;
	}

	/**
	 * Set the location type
	 * 
	 * @param in
	 *            Location type
	 */
	public void set_location_type(String in) {
		try {
			_location_type = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set the parent station
	 * 
	 * @param in
	 *            Parent station
	 */
	public void set_parent_station(String in) {
		_parent_station = in;
	}

	/**
	 * Set the position
	 * 
	 * @param _position
	 *            The position to set
	 */
	public void set_position(String _position) {
		this._position = _position;
	}

	/**
	 * Set the stop code
	 * 
	 * @param in
	 *            Stop code
	 */
	public void set_stop_code(String in) {
		_stop_code = in;
	}

	/**
	 * Set the description
	 * 
	 * @param in
	 *            Description
	 */
	public void set_stop_desc(String in) {
		_stop_desc = in;
	}

	/**
	 * Set this stop's ID
	 * 
	 * @param in
	 *            ID
	 */
	public void set_stop_id(String in) {
		_stop_id = in;
	}

	/**
	 * Set latitude
	 * 
	 * @param in
	 *            Latitude
	 */
	public void set_stop_lat(double in) {
		_stop_lat = in;
	}

	/**
	 * Set latitude
	 * 
	 * @param in
	 *            Latitude
	 */
	public void set_stop_lat(String in) {
		try {
			_stop_lat = Double.parseDouble(in);
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
	public void set_stop_lon(double in) {
		_stop_lon = in;
	}

	/**
	 * Set longitude
	 * 
	 * @param in
	 *            Longitude
	 */
	public void set_stop_lon(String in) {
		try {
			_stop_lon = Double.parseDouble(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set name
	 * 
	 * @param in
	 *            Name
	 */
	public void set_stop_name(String in) {
		_stop_name = in;
	}

	/**
	 * Set time zone
	 * 
	 * @param in
	 *            Time zone
	 */
	public void set_stop_timezone(String in) {
		_stop_timezone = in;
	}

	/**
	 * Set stop's URL
	 * 
	 * @param in
	 *            URL
	 */
	public void set_stop_url(String in) {
		_stop_url = in;
	}

	/**
	 * Set if wheelchair boarding is allowed here
	 * 
	 * @param in
	 *            If wheelchair boarding is allowed here
	 */
	public void set_wheelchair_boarding(int in) {
		_wheelchair_boarding = in;
	}

	/**
	 * Set if wheelchair boarding is allowed here
	 * 
	 * @param in
	 *            If wheelchair boarding is allowed here
	 */

	public void set_wheelchair_boarding(String in) {
		try {
			_wheelchair_boarding = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set zone ID
	 * 
	 * @param in
	 *            Zone ID
	 */
	public void set_zone_id(String in) {
		_zone_id = in;
	}

	/**
	 * Set stop longitude and latitude both at once
	 * 
	 * @param inLat
	 *            Latitude
	 * @param inLon
	 *            Longitude
	 */
	public void setCoordinates(double inLat, double inLon) {
		_stop_lat = inLat;
		_stop_lon = inLon;
	}

	/**
	 * Set next stop reference
	 * 
	 * @param in
	 *            Next stop object
	 */
	public void setNextStop(Stop in) {
		_nextStop = in;
	}

	/**
	 * Set Route object
	 * 
	 * @param in
	 *            Route object
	 */
	public void setRoute(Route in) {
		_route = in;
	}

	/**
	 * Set StopTime object
	 * 
	 * @param in
	 *            StopTime object
	 */
	public void setStopTime(StopTime in) {
		_stopTime = in;
	}

	@Override
	public String toString() {
		return _stop_id + "," + _stop_name + "," + _stop_desc + "," + _stop_lat
				+ "," + _stop_lon + "," + _zone_id + "," + _stop_url + ","
				+ _location_type + "," + _parent_station + "\n";
	}
}
