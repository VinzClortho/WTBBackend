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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 * GTFS table parser interface.
 * 
 * @author Jason LaFrance
 */
public abstract class GTFSParser {

	protected static final HashMap<Integer, HashMap<String, ArrayList<String>>> _headers = new HashMap<>();

	/**
	 * Get a constructor object based on a given formal class name.
	 * 
	 * @param inClass
	 *            The full class name
	 * @param touch
	 *            Set to true to just touch the class and return a blank stub
	 *            constructor.
	 * @return A constructor for the given class, or null if invalid.
	 */
	public static Constructor<?> getConstructor(String inClass, boolean touch) {
		// Create a class object from the class matching the table name
		Class<?> clazz = null;
		Constructor<?> cons = null;
		try {
			clazz = Class.forName(inClass);
		} catch (ClassNotFoundException ex) {
			// Logger.getLogger(GTFSParser.class.getName()).log(Level.SEVERE,
			// null, ex);
		}
		try {
			if (clazz != null) {
				if (touch) {
					cons = clazz.getConstructor(String.class);
				} else {
					cons = clazz.getConstructor(String.class, int.class);
				}
			}
		} catch (NoSuchMethodException | SecurityException ex) {
			// Logger.getLogger(GTFSParser.class.getName()).log(Level.SEVERE,
			// null, ex);
		}
		return cons;
	}

	/**
	 * Get the GTFS filename for a given class name
	 * 
	 * @param inClass
	 *            The full class name to match
	 * @return The tables file name
	 */
	public static String getFilename(String inClass) {
		String ret = null;

		Constructor<?> cons = GTFSParser.getConstructor(inClass, true);

		GTFSParser parser = null;
		try {
			if (cons != null) {
				parser = (GTFSParser) cons.newInstance("");
			}
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException ex) {
			// Logger.getLogger(GTFSParser.class.getName()).log(Level.SEVERE,
			// null, ex);
		}
		if (parser != null) {
			ret = parser.getFilename();
		}
		return ret;
	}

	/**
	 * Get the in-order row of headers for the specified GTFS table in GTFS
	 * object mapped to ID
	 * 
	 * @param inID
	 *            The ID of the GTFS object
	 * @param inFilename
	 *            The filename of the table to get header for
	 * @return The list of header titles
	 */
	public static synchronized ArrayList<String> getHeader(int inID,
			String inFilename) {
		return _headers.get(inID).get(inFilename);
	}

	/**
	 * Store and parse GTFS table header data
	 * 
	 * @param inID
	 *            The ID of the GTFS object
	 * @param inFilename
	 *            The filename of the table to store header for
	 * @param inHeader
	 *            The full, raw header String
	 */
	public static void setHeader(int inID, String inFilename, String inHeader) {
		synchronized (_headers) {
			if (_headers.get(inID) == null) { // add new header map for GTFS id
				_headers.put(inID, new HashMap<String, ArrayList<String>>());
			}

			HashMap<String, ArrayList<String>> map = _headers.get(inID);
			ArrayList<String> list = new ArrayList<>();

			String[] f = CSVParser.parseLine(inHeader);
			for (int i = 0; i < f.length; i++) {
				f[i] = f[i].trim().replaceAll("\"", "");
			}
			list.addAll(Arrays.asList(f));
			map.put(inFilename, list);
			_headers.put(inID, map);
		}
	}

	private int sGTFS_ID;

	/**
	 * Get the filename for the implemented class
	 * 
	 * @return The GTFS table file name
	 */
	public abstract String getFilename();

	/**
	 * Get this class' GTFS object
	 * 
	 * @return The GTFS object
	 */
	public GTFS getGTFS() {
		return GTFS.getGTFS(sGTFS_ID);
	}

	/**
	 * Get this class' GTFS object ID
	 * 
	 * @return The GTFS ID
	 */
	public int getGTFS_ID() {
		return sGTFS_ID;
	}

	/**
	 * Get a unique ID for this entry
	 * 
	 * @return A unique ID
	 */
	public abstract int getID();

	/**
	 * Parse a table row for the given GTFS ID using the Reflection API
	 * 
	 * @param inID
	 *            The GTFS ID
	 * @param inLine
	 *            The raw table row
	 */
	public void parse(int inID, String inLine) {
		String[] f = CSVParser.parseLine(inLine);

		sGTFS_ID = inID;

		// System.out.println("Parsing: " + getGTFS_ID() + "   " + inLine);
		ArrayList<String> inHeader = _headers.get(inID).get(this.getFilename());

		if (inHeader == null || f == null || f.length == 0
				|| f.length != inHeader.size()) {
			return;
		}

		Class clazz = this.getClass();

		for (int i = 0; i < f.length; i++) {
			if (f[i] != null && f[i].length() > 0) {
				try {
					clazz.getMethod("set_" + inHeader.get(i), String.class)
							.invoke(this, f[i].replace("\"", ""));
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException
						| SecurityException ex) {
					Logger.getLogger(GTFSParser.class.getName()).log(
							Level.SEVERE, null, ex);
				}

			}
		}
	}

	/**
	 * Set the GTFS ID
	 * 
	 * @param id
	 *            the GTFS ID to set
	 */
	protected void setGTFS_ID(int id) {
		sGTFS_ID = id;
	}

}
