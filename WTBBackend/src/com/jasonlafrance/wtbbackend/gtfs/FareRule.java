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
 * GTFS FareRule class.
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

	/**
	 * Stub constructor
	 */
	public FareRule() {
	}

	/**
	 * Stub constructor
	 * 
	 * @param inLine
	 *            A String.
	 */
	public FareRule(String inLine) {
	}

	/**
	 * Create a FareRule object with the given values.
	 * 
	 * @param inID
	 *            ID
	 * @param inRouteID
	 *            Route ID
	 * @param inOriginID
	 *            Origin ID
	 * @param inDestID
	 *            Destination ID
	 * @param inContID
	 *            Contains ID
	 */
	public FareRule(String inID, String inRouteID, String inOriginID,
			String inDestID, String inContID) {
		_fare_id = inID;
		_route_id = inRouteID;
		_origin_id = inOriginID;
		_destination_id = inDestID;
		_contains_id = inContID;
	}

	/**
	 * Get the contains ID
	 * @return Contains ID
	 */
	public String get_contains_id() {
		return _contains_id;
	}

	/**
	 * Get the Destination ID
	 * @return Destination ID
	 */
	public String get_destination_id() {
		return _destination_id;
	}

	/**
	 * Get the fare ID
	 * @return Fare ID
	 */
	public String get_fare_id() {
		return _fare_id;
	}

	/**
	 * Get the origin ID
	 * @return Origin ID
	 */
	public String get_origin_id() {
		return _origin_id;
	}

	/**
	 * Get the route ID
	 * @return Route ID
	 */
	public String get_route_id() {
		return _route_id;
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
		return _fare_id.hashCode();
	}

	/**
	 * Set the contains ID
	 * @param in Contains ID
	 */
	public void set_contains_id(String in) {
		_contains_id = in;
	}

	/**
	 * Set the destination ID
	 * @param in Destination ID
	 */
	public void set_destination_id(String in) {
		_destination_id = in;
	}

	/**
	 * Set the fare ID
	 * @param in Fare ID
	 */
	public void set_fare_id(String in) {
		_fare_id = in;
	}

	/**
	 * Set the origin ID
	 * @param in Origin ID
	 */
	public void set_origin_id(String in) {
		_origin_id = in;
	}

	/**
	 * Set the route ID
	 * @param in Route ID
	 */
	public void set_route_id(String in) {
		_route_id = in;
	}

	@Override
	public String toString() {
		return _fare_id + "," + _route_id + "," + _origin_id + ","
				+ _destination_id + "," + _contains_id + "\n";
	}
}
