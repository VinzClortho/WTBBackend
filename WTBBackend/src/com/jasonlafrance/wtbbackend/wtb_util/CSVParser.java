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

package com.jasonlafrance.wtbbackend.wtb_util;

import java.util.ArrayList;

/**
 * Comma seperated value parsing function class
 * 
 * @author Jason LaFrance
 */
public class CSVParser {

	/**
	 * Parse a line of CSV data into a String array
	 * 
	 * @param in
	 *            The raw CSV String to parse
	 * @return A String array of the seperated data
	 */
	public static String[] parseLine(String in) {
		ArrayList<String> list = new ArrayList<>();

		boolean notInsideComma = true;
		int start = 0, end = 0;
		for (int i = 0; i < in.length(); i++) {
			if (in.charAt(i) == ',' && notInsideComma) {
				list.add(in.substring(start, i));
				start = i + 1;
			} else if (in.charAt(i) == '"') {
				notInsideComma = !notInsideComma;
			}
		}
		list.add(in.substring(start));
		return list.toArray(new String[list.size()]);
	}
}
