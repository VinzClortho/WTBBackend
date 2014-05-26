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
 * GTFS Transfer class
 * 
 * @author Jason LaFrance
 */
public final class Transfer extends GTFSParser {

	private static final String _filename = "//transfers.txt";

	private String _from_stop_id = null, _to_stop_id = null;
	private int _transfer_type = -1;
	private double _min_transfer_time = 0.0;

	/**
	 * Stub constructor
	 */
	public Transfer() {
	}

	/**
	 * Stub constructor
	 * 
	 * @param inLine
	 *            A String
	 */
	public Transfer(String inLine) {
	}


	/**
	 * Create a Transfer object from given values
	 * 
	 * @param inFrom
	 *            Stop ID coming from
	 * @param inTo
	 *            Stop ID going to
	 * @param inType
	 *            Transfer type
	 * @param minTrans
	 *            Minimum transfer time
	 */
	public Transfer(String inFrom, String inTo, int inType, double minTrans) {
		_from_stop_id = inFrom;
		_to_stop_id = inTo;
		_transfer_type = inType;
		_min_transfer_time = minTrans;
	}

	/**
	 * Get ID of stop coming from
	 * 
	 * @return From stop ID
	 */
	public String get_from_stop_id() {
		return _from_stop_id;
	}

	/**
	 * Get minimum transfer time
	 * 
	 * @return Minimum transfer time
	 */
	public double get_min_transfer_time() {
		return _min_transfer_time;
	}

	/**
	 * Get ID of stop going to
	 * 
	 * @return To stop ID
	 */
	public String get_to_stop_id() {
		return _to_stop_id;
	}

	/**
	 * Get transfer tpe
	 * 
	 * @return Transfer type
	 */
	public int get_transfer_type() {
		return _transfer_type;
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
		return _from_stop_id.hashCode();
	}

	/**
	 * Set stop ID coming from
	 * 
	 * @param in
	 *            From stop ID
	 */
	public void set_from_stop_id(String in) {
		_from_stop_id = in;
	}

	/**
	 * Set minimum transfer time
	 * 
	 * @param in
	 *            Minimum transfer time
	 */
	public void set_min_transfer_time(double in) {
		_min_transfer_time = in;
	}

	/**
	 * Set minimum transfer time
	 * 
	 * @param in
	 *            Minimum transfer time
	 */
	public void set_min_transfer_time(String in) {
		try {
			_min_transfer_time = Double.parseDouble(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set stop ID going to next
	 * 
	 * @param in
	 *            To stop ID
	 */
	public void set_to_stop_id(String in) {
		_to_stop_id = in;
	}

	/**
	 * Set transfer type
	 * 
	 * @param in
	 *            Transfer type
	 */
	public void set_transfer_type(int in) {
		_transfer_type = in;
	}

	/**
	 * Set transfer type
	 * 
	 * @param in
	 *            Transfer type
	 */
	public void set_transfer_type(String in) {
		try {
			_transfer_type = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	@Override
	public String toString() {
		return _from_stop_id + "," + _to_stop_id + "," + _transfer_type + ","
				+ _min_transfer_time + "\n";
	}
}
