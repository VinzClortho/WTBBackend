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
 * GTFS calendar class.
 * 
 * @author Jason LaFrance
 */
public final class Calendar extends GTFSParser {

	/**
	 * Enumeration of Weekdays
	 * @author Jason LaFrance
	 *
	 */
	public static enum Weekdays {
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
	}

	private static final String _filename = "//calendar.txt";
	private String _service_id = null;
	private final boolean[] _weekday = new boolean[7]; // monday = 0
	private String _start_date = null;

	private String _end_date = null;

	/**
	 * Stub constructor
	 */
	public Calendar() {
		;
	}

	/**
	 * Stub constructor
	 * 
	 * @param inLine
	 *            A String
	 */
	public Calendar(String inLine) {
	}

	/**
	 * Create a Calendar object with the given values.
	 * 
	 * @param inID
	 *            Calendar ID
	 * @param inDays
	 *            Calendar run days, stored as seven boolean values represented
	 *            in a seven bit value
	 * @param inStart
	 *            Calendar start date
	 * @param inEnd
	 *            Calendar end date
	 */
	public Calendar(String inID, int inDays, String inStart, String inEnd) {
		_service_id = inID;
		_start_date = inStart;
		_end_date = inEnd;

		int mask = 1;
		for (int i = 0; i < 7; i++) {
			if ((inDays & mask) > 0) {
				_weekday[i] = true;
			}
			mask <<= 1;
		}
	}

	/**
	 * Get end date
	 * @return End date
	 */
	public String get_end_date() {
		return _end_date;
	}

	/**
	 * Get service ID
	 * @return Service ID
	 */
	public String get_service_id() {
		return _service_id;
	}

	/**
	 * Get start date
	 * @return Start date
	 */
	public String get_start_date() {
		return _start_date;
	}

	/**
     *	Get the GTFS file name associated with this object.
     * @return The GTFS file name associated with this object.
     */
	@Override
	public String getFilename() {
		return _filename;

	}

	/**
	 * Get the unique ID for this object
	 * @return Unique ID for this object
	 */
	@Override
	public int getID() {
		return _service_id.hashCode();
	}

	/**
	 * Get a list of active weekdays for this calendar item
	 * @return A List of active weekdays.
	 */
	public ArrayList<Calendar.Weekdays> getWeekdays() {
		ArrayList<Calendar.Weekdays> out = new ArrayList<>();

		for (int i = 0; i < 7; i++) {
			if (_weekday[i]) {
				out.add(Calendar.Weekdays.values()[i]);
			}
		}

		return out;
	}

	/**
	 * Check if Friday is set
	 * @return If this day is set.
	 */
	public boolean isFriday() {
		return _weekday[4];
	}

	/**
	 * Check if Monday is set
	 * @return If this day is set.
	 */
	public boolean isMonday() {
		return _weekday[0];
	}

	/**
	 * Check if Saturday is set
	 * @return If this day is set.
	 */
	public boolean isSaturday() {
		return _weekday[5];
	}

	/**
	 * Check if Sunday is set
	 * @return If this day is set.
	 */
	public boolean isSunday() {
		return _weekday[6];
	}

	/**
	 * Check if Thursday is set
	 * @return If this day is set.
	 */
	public boolean isThursday() {
		return _weekday[3];
	}

	/**
	 * Check if Tuesday is set
	 * @return If this day is set.
	 */
	public boolean isTuesday() {
		return _weekday[1];
	}

	/**
	 * Check if Wednesday is set
	 * @return If this day is set.
	 */
	public boolean isWednesday() {
		return _weekday[2];
	}

	/**
	 * Set end date
	 * @param in End date
	 */
	public void set_end_date(String in) {
		_end_date = in;
	}

	/**
	 * Set Friday value
	 * @param in Boolean value for if the service is active on this day.
	 */
	public void set_friday(boolean in) {
		_weekday[4] = in;
	}

	/**
	 * Set Friday value
	 * @param in Bit value for if the service is active on this day.
	 */
	public void set_friday(String in) {
		_weekday[4] = in.equals("1");
	}

	/**
	 * Set Monday value
	 * @param in Boolean value for if the service is active on this day.
	 */
	public void set_monday(boolean in) {
		_weekday[0] = in;
	}

	/**
	 * Set Monday value
	 * @param in Binary value for if the service is active on this day.
	 */
	public void set_monday(String in) {
		_weekday[0] = in.equals("1");
	}

	/**
	 * Set Saturday value
	 * @param in Boolean value for if the service is active on this day.
	 */
	public void set_saturday(boolean in) {
		_weekday[5] = in;
	}

	/**
	 * Set Saturday value
	 * @param in Bit value for if the service is active on this day.
	 */
	public void set_saturday(String in) {
		_weekday[5] = in.equals("1");
	}

	/**
	 * Set service ID
	 * @param in Service ID
	 */
	public void set_service_id(String in) {
		_service_id = in;

	}

	/**
	 * Set start date
	 * @param in Start date
	 */
	public void set_start_date(String in) {
		_start_date = in;
	}

	/**
	 * Set Sunday value
	 * @param in Boolean value for if the service is active on this day.
	 */
	public void set_sunday(boolean in) {
		_weekday[6] = in;
	}

	/**
	 * Set Sunday value
	 * @param in Bit value for if the service is active on this day.
	 */
	public void set_sunday(String in) {
		_weekday[6] = in.equals("1");
	}

	/**
	 * Set Thursday value
	 * @param in Boolean value for if the service is active on this day.
	 */
	public void set_thursday(boolean in) {
		_weekday[3] = in;
	}

	/**
	 * Set Thursday value
	 * @param in Bit value for if the service is active on this day.
	 */
	public void set_thursday(String in) {
		_weekday[3] = in.equals("1");
	}

	/**
	 * Set Tuesday value
	 * @param in Boolean value for if the service is active on this day.
	 */
	public void set_tuesday(boolean in) {
		_weekday[1] = in;
	}

	/**
	 * Set Tuesday value
	 * @param in Bit value for if the service is active on this day.
	 */
	public void set_tuesday(String in) {
		_weekday[1] = in.equals("1");
	}

	/**
	 * Set Wednesday value
	 * @param in Boolean value for if the service is active on this day.
	 */
	public void set_wednesday(boolean in) {
		_weekday[2] = in;
	}

	/**
	 * Set Wednesday value
	 * @param in Bit value for if the service is active on this day.
	 */
	public void set_wednesday(String in) {
		_weekday[2] = in.equals("1");
	}

	@Override
	public String toString() {
		String ret = "";
		ret += _service_id + ",";

		for (int i = 0; i < 7; i++) {
			ret += _weekday[i] ? "1," : "0,";
		}

		ret += _start_date + ",";
		ret += _end_date + "\n";

		return ret;
	}
}
