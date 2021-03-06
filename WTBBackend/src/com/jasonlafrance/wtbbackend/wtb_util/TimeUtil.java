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

/**
 * Time conversion function class
 * 
 * @author Jason LaFrance
 */
public class TimeUtil {

	/**
	 * Convert a time code in minutes to a time String
	 * 
	 * @param inTime
	 *            Time code to convert
	 * @return The time String
	 */
	public static String minutesToTime(int inTime) {
		if (inTime < 0) {
			return "";
		}
		int min = inTime % 60;
		inTime /= 60;
		int hour = inTime;

		String H = ("00" + Integer.toString(hour));
		H = H.substring(H.length() - 2);
		String M = ("00" + Integer.toString(min));
		M = M.substring(M.length() - 2);

		return H + ":" + M + ":00";
	}

	/**
	 * Convert a time String to a time code in minutes
	 * 
	 * @param in
	 *            The String to convert
	 * @return The time code
	 */
	public static int timeToMinutes(String in) {
		// time strings are stored as HH:MM:SS
		int mins;
		// System.out.println("timeToMinutes: " + in);
		// dirty implementation, but a little faster

		if (in.length() == 8 && in.charAt(2) == ':' && in.charAt(5) == ':') {
			mins = (in.charAt(0) & 0xF) * 600 + (in.charAt(1) & 0xF) * 60
					+ (in.charAt(3) & 0xF) * 10 + (in.charAt(4) & 0xF);

		} else {
			mins = 0;
		}
		// clean implementation
		/*
		 * String[] n = in.split(":"); mins = 0;
		 * 
		 * if (n.length == 3) { try { mins = Integer.parseInt(n[0]) * 60 +
		 * Integer.parseInt(n[1]); } catch (NumberFormatException e) {;
		 * 
		 * } }
		 */
		return mins;
	}

	/**
	 * Convert a time String to seconds
	 * 
	 * @param in
	 *            The time String to convert
	 * @return Time code in seconds
	 */
	public static int timeToSeconds(String in) {
		// time strings are stored as HH:MM:SS
		int secs;

		// dirty implementation, but a little faster
		/*
		 * if (in.length() == 8 && in.charAt(2) == ':' && in.charAt(5) == ':') {
		 * mins = (in.charAt(0) & 0xF) * 36000 + (in.charAt(1) & 0xF) * 3600 +
		 * (in.charAt(3) & 0xF) * 600 + (in.charAt(4) & 0xF) * 60 +
		 * (in.charAt(6) & 0xF) * 10 + (in.charAt(7) & 0xF);
		 * 
		 * } else { mins = 0; }
		 */
		// clean implementation
		String[] n = in.split(":");
		secs = 0;

		if (n.length == 3) {
			try {
				secs = Integer.parseInt(n[0]) * 3600 + Integer.parseInt(n[1])
						* 60 + Integer.parseInt(n[2]);
			} catch (NumberFormatException e) {
				;

			}
		}
		return secs;
	}
}
