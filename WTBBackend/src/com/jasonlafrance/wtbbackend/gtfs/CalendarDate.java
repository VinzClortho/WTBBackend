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
 * GTFS CalendarDate class.
 * 
 * @author Jason LaFrance
 */
public final class CalendarDate extends GTFSParser {

	private static final String _filename = "//calendar_dates.txt";

	private String _service_id = null;
	private String _date = null;
	private int _exception_type = -1;

	/**
	 * Stub constructor
	 */
	public CalendarDate() {
	}

	/**
	 * Stub constructor
	 * 
	 * @param inLine
	 *            A String.
	 */
	public CalendarDate(String inLine) {
	}

	/**
	 * Create a CalendarDate object from the given values.
	 * 
	 * @param inID
	 *            The ID.
	 * @param inDate
	 *            The date.
	 * @param inType
	 *            The type.
	 */
	public CalendarDate(String inID, String inDate, int inType) {
		_service_id = inID;
		_date = inDate;
		_exception_type = inType;
	}

	/**
	 * Get the date
	 * 
	 * @return The date
	 */
	public String get_date() {
		return _date;
	}

	/**
	 * Get the exception type
	 * 
	 * @return The exception type
	 */
	public int get_exception_type() {
		return _exception_type;
	}

	/**
	 * Get the service ID
	 * 
	 * @return Service ID
	 */
	public String get_service_id() {
		return _service_id;
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
		return _service_id.hashCode();
	}

	/**
	 * Set date
	 * 
	 * @param in
	 *            Date
	 */
	public void set_date(String in) {
		_date = in;
	}

	/**
	 * Set exception type
	 * 
	 * @param in
	 *            exception type
	 */
	public void set_exception_type(int in) {
		_exception_type = in;
	}

	/**
	 * Set exception type
	 * 
	 * @param in
	 *            Exception type
	 */
	public void set_exception_type(String in) {
		try {
			_exception_type = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set the service ID
	 * 
	 * @param in
	 *            Service ID
	 */
	public void set_service_id(String in) {
		_service_id = in;
	}

	@Override
	public String toString() {
		return _service_id + "," + _date + "," + _exception_type + "\n";
	}

}
