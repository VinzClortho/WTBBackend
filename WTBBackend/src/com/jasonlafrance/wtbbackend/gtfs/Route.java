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
 * GTFS Route class.
 * 
 * @author Jason LaFrance
 */
public final class Route extends GTFSParser {

	private static final String _filename = "//routes.txt";
	private static final double _threshold = 1.0 / 69.0;

	/**
	 * Add a trip to a master trip list
	 * 
	 * @param master
	 *            The master Trip list
	 * @param in
	 *            The Trip to add
	 * @param id
	 *            The ID if the master Trip list
	 */
	public static void addTrip(ArrayList<ArrayList<Trip>> master, Trip in,
			int id) {
		if (id > -1) {
			while (id >= master.size()) {
				master.add(new ArrayList<Trip>());
			}
			master.get(id).add(in);
		}
	}

	/**
	 * Add vertices to a master vertex list.
	 * 
	 * @param master
	 *            The master Vertex list
	 * @param in
	 *            The Vertex to add
	 * @param id
	 *            The ID of the master Vertex list
	 */
	public static void addVertices(ArrayList<ArrayList<Vertex>> master,
			ArrayList<Vertex> in, int id) {
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

	/**
	 * Stub constructor
	 */
	public Route() {
	}

	/**
	 * Stub constuctor
	 * 
	 * @param inLine
	 *            A String
	 */
	public Route(String inLine) {
	}

	/**
	 * Create a Route object with a supplied line from a GTFS routes.txt table.
	 * 
	 * @param inLine
	 *            Line from a GTFS routes.txt table.
	 * @param id
	 *            The GTFS ID from the GTFS multition that this object belongs
	 *            to.
	 */
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

	/**
	 * Create a Route object from the given values.
	 * 
	 * @param inID
	 *            The route ID
	 * @param inAID
	 *            The agency ID
	 * @param inShort
	 *            The short name
	 * @param inLong
	 *            The long name
	 * @param inDesc
	 *            The Description
	 * @param inType
	 *            The route type
	 * @param inUrl
	 *            The route's URL
	 * @param inColor
	 *            The route's associated color
	 * @param inTextColor
	 *            The route's associated text color
	 */
	public Route(String inID, String inAID, String inShort, String inLong,
			String inDesc, int inType, String inUrl, String inColor,
			String inTextColor) {
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

	/**
	 * Add a trip to this route's Trip list
	 * 
	 * @param in
	 *            Trip to add
	 */
	public void addTrip(Trip in) {
		addTrip(_trips, in, 0);
	}

	/**
	 * Add vertices to default list
	 * 
	 * @param in
	 *            List of Vertex objects to add
	 */
	public void addVertices(ArrayList<Vertex> in) {
		if (in == null || in.isEmpty()) {
			return;
		}
		addVertices(in, 0);
	}

	/**
	 * Add vertices to this route's Vertex list
	 * 
	 * @param in
	 *            A Vertex list
	 * @param id
	 *            The ID of the Vertex list
	 */
	public void addVertices(ArrayList<Vertex> in, int id) {
		addVertices(_vertices, in, id);
	}

	/**
	 * Get the agency ID
	 * 
	 * @return Agency ID
	 */
	public String get_agency_id() {
		return _agency_id;
	}

	/**
	 * Get the route color
	 * 
	 * @return Route color
	 */
	public String get_route_color() {
		return _route_color;
	}

	/**
	 * Get the desciption
	 * 
	 * @return Description
	 */
	public String get_route_desc() {
		return _route_desc;
	}

	/**
	 * Get the route ID
	 * 
	 * @return Route ID
	 */
	public String get_route_id() {
		return _route_id;
	}

	/**
	 * Get the long name
	 * 
	 * @return Long name
	 */
	public String get_route_long_name() {
		return _route_long_name;
	}

	/**
	 * Get the short name
	 * 
	 * @return Short name
	 */
	public String get_route_short_name() {
		return _route_short_name;
	}

	/**
	 * Get the text color
	 * 
	 * @return Text color
	 */
	public String get_route_text_color() {
		return _route_text_color;
	}

	/**
	 * Get the route type
	 * 
	 * @return Route type
	 */
	public int get_route_type() {
		return _route_type;
	}

	/**
	 * Get the route URL
	 * 
	 * @return URL
	 */
	public String get_route_url() {
		return _route_url;
	}

	/**
	 * Get the Agency object
	 * 
	 * @return Agency object
	 */
	public Agency getAgency() {
		return mAgency;
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
		return _route_id.hashCode();
	}

	/**
	 * Get the best valid name
	 * 
	 * @return Name
	 */
	public String getName() {
		if (_route_short_name == null) {
			return _route_long_name;
		} else {
			return _route_short_name;
		}
	}

	/**
	 * Get number of services in route
	 * 
	 * @return Number of services
	 */
	public int getServices() {
		return _trips.size();
	}

	/**
	 * Get list of Trips
	 * 
	 * @return List of Trips
	 */
	public ArrayList<Trip> getTrips() {
		return _trips.get(0);
	}

	/**
	 * Get Trips from a specific Trips sub-list
	 * 
	 * @param in
	 *            Sub-list index
	 * @return Trips sub-list
	 */
	public ArrayList<Trip> getTrips(int in) {
		if (in > -1 && in < _trips.size()) {
			return _trips.get(in);
		} else {
			return null;
		}
	}

	/**
	 * Get Number of Trips in route
	 * 
	 * @param id
	 *            Sub-list index
	 * @return Number of Trips
	 */
	public int getTripsCount(int id) {
		if (id >= 0 && id < _trips.size()) {
			return _trips.get(id).size();
		} else {
			return 0;
		}
	}

	/**
	 * Get vertices in route
	 * 
	 * @return List of vertices in route
	 */
	public ArrayList<Vertex> getVertices() {
		if (!_vertices.isEmpty()) {
			return _vertices.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Set agency ID
	 * 
	 * @param in
	 *            Agency ID
	 */
	public void set_agency_id(String in) {
		_agency_id = in;
	}

	/**
	 * Set route color
	 * 
	 * @param in
	 *            Color
	 */
	public void set_route_color(String in) {
		_route_color = in;
	}

	/**
	 * Set route description
	 * 
	 * @param in
	 *            Description
	 */
	public void set_route_desc(String in) {
		_route_desc = in;
	}

	/**
	 * Set route ID
	 * 
	 * @param in
	 *            Route ID
	 */
	public void set_route_id(String in) {
		_route_id = in;
	}

	/**
	 * Set long name
	 * 
	 * @param in
	 *            Long name
	 */
	public void set_route_long_name(String in) {
		_route_long_name = in.trim().replaceAll("\"", "");
	}

	/**
	 * Set short name
	 * 
	 * @param in
	 *            Short name
	 */
	public void set_route_short_name(String in) {
		_route_short_name = in.trim().replaceAll("\"", "");
	}

	/**
	 * Set text color
	 * 
	 * @param in
	 *            Color
	 */
	public void set_route_text_color(String in) {
		_route_text_color = in;
	}

	/**
	 * Set route type
	 * 
	 * @param in
	 *            Type
	 */
	public void set_route_type(int in) {
		_route_type = in;
	}

	/**
	 * Set route type
	 * 
	 * @param in
	 *            Type
	 */
	public void set_route_type(String in) {
		try {
			_route_type = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set route's URL
	 * 
	 * @param in
	 *            URL
	 */
	public void set_route_url(String in) {
		_route_url = in;
	}

	/**
	 * Set route's Agency object
	 * 
	 * @param in
	 *            Agency object
	 */
	public void setAgency(Agency in) {
		mAgency = in;
	}

	@Override
	public String toString() {
		return _route_id + "," + _route_short_name + "," + _route_long_name
				+ "," + _route_desc + "," + _route_type + "," + _route_url
				+ "," + _route_color + "," + _route_text_color + "\n";
	}

}
