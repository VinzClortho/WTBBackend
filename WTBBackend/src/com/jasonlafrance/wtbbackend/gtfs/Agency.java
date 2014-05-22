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

import java.util.ArrayList;

import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 * GTFS Agency class
 * 
 * @author Jason LaFrance
 */
public final class Agency extends GTFSParser {

	private static final String _filename = "//agency.txt";

	private String _agency_id = null;
	private String _agency_name = null;
	private String _agency_url = null;
	private String _agency_timezone = null;
	private String _agency_lang = null;
	private String _agency_phone = null;
	private String _agency_fare_url = null;
	private String _bikes_policy_url = null;

	/**
	 * This is just a stub constructor
	 * 
	 * @param inLine
	 *            A String
	 */
	public Agency(String inLine) {
	}

	// do this without reflection!
	/**
	 * Create an Agency object with a supplied line from a GTFS agency.txt
	 * table.
	 * 
	 * @param inLine
	 *            Line from a GTFS agency.txt table.
	 * @param id
	 *            The GTFS ID from the GTFS multition that this object belongs
	 *            to.
	 */
	public Agency(String inLine, int id) {
		ArrayList<String> h = _headers.get(id).get(_filename);
		String[] f = CSVParser.parseLine(inLine);

		if (f.length != h.size()) {
			return;
		}

		for (int i = 0; i < f.length; i++) {
			switch (h.get(i)) {
			case "agency_fare_url":
				set_agency_fare_url(f[i]);
				break;
			case "agency_id":
				set_agency_id(f[i]);
				break;
			case "agency_lang":
				set_agency_lang(f[i]);
				break;
			case "agency_name":
				set_agency_name(f[i]);
				break;
			case "agency_phone":
				set_agency_phone(f[i]);
				break;
			case "agency_timezone":
				set_agency_timezone(f[i]);
				break;
			case "agency_url":
				set_agency_url(f[i]);
				break;
			case "bikes_policy_url":
				set_bikes_policy_url(f[i]);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Create an Agency object with given values.
	 * 
	 * @param inID
	 *            Agency ID
	 * @param inName
	 *            Agency name
	 * @param inUrl
	 *            Agency URL
	 * @param inTimezone
	 *            Agency time zone
	 * @param inLang
	 *            Agency language
	 * @param inPhone
	 *            Agency phone number
	 */
	public Agency(String inID, String inName, String inUrl, String inTimezone,
			String inLang, String inPhone) {
		_agency_id = inID;
		_agency_name = inName;
		_agency_url = inUrl;
		_agency_timezone = inTimezone;
		_agency_lang = inLang;
		_agency_phone = inPhone;
	}

	/**
	 * Get this agency's fare URL
	 * 
	 * @return The agency fare URL
	 */
	public String get_agency_fare_url() {
		return _agency_fare_url;
	}

	/**
	 * Get this agency's ID
	 * 
	 * @return The agency ID
	 */
	public String get_agency_id() {
		return _agency_id;
	}

	/**
	 * Get this agency's language
	 * 
	 * @return The agency language
	 */
	public String get_agency_lang() {
		return _agency_lang;
	}

	/**
	 * Get this agency's name
	 * 
	 * @return The agency name.
	 */
	public String get_agency_name() {
		return _agency_name;
	}

	/**
	 * Get this agency's phone number
	 * 
	 * @return The agency phone number
	 */
	public String get_agency_phone() {
		return _agency_phone;
	}

	/**
	 * Get this agency's time zone
	 * 
	 * @return The agency time zone
	 */
	public String get_agency_timezone() {
		return _agency_timezone;
	}

	/**
	 * Get this agency's URL
	 * 
	 * @return The agency URL
	 */
	public String get_agency_url() {
		return _agency_url;
	}

	/**
	 * Get this agency's bikes policy URL
	 * 
	 * @return The agency bikes policy URL
	 */
	public String get_bikes_policy_url() {
		return _bikes_policy_url;
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
		return _agency_name.hashCode();
	}

	/**
	 * Sets the fare URL
	 * 
	 * @param in
	 *            New fare URL.
	 */
	public void set_agency_fare_url(String in) {
		_agency_fare_url = in;
	}

	/**
	 * Sets the ID
	 * 
	 * @param in
	 *            New ID value.
	 */
	public void set_agency_id(String in) {
		_agency_id = in;
	}

	/**
	 * Sets the language
	 * 
	 * @param in
	 *            New language.
	 */
	public void set_agency_lang(String in) {
		_agency_lang = in;
	}

	/**
	 * Sets the name
	 * 
	 * @param in
	 *            New name.
	 */
	public void set_agency_name(String in) {
		_agency_name = in;
	}

	/**
	 * Sets the phone number
	 * 
	 * @param in
	 *            New phone number.
	 */
	public void set_agency_phone(String in) {
		_agency_phone = in;
	}

	/**
	 * Sets the time zone
	 * 
	 * @param in
	 *            New time zone.
	 */
	public void set_agency_timezone(String in) {
		_agency_timezone = in;
	}

	/**
	 * Sets the URL
	 * 
	 * @param in
	 *            New URL.
	 */
	public void set_agency_url(String in) {
		_agency_url = in;
	}

	/**
	 * Sets the bikes policy URL
	 * 
	 * @param in
	 *            New bikes policy URL
	 */
	public void set_bikes_policy_url(String in) {
		_bikes_policy_url = in;
	}

	@Override
	public String toString() {
		return _agency_id + "," + _agency_name + "," + _agency_url + ","
				+ _agency_timezone + "," + _agency_lang + "," + _agency_phone
				+ "\n";
	}

}
