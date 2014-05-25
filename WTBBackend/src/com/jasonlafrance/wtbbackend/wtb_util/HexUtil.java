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
 * Function class for converting to and from hexadecimal strings
 * 
 * @author Jason LaFrance
 */
public class HexUtil {

	static final String HEXES = "0123456789ABCDEF";

	/**
	 * Convert a hexadecimal String into a byte array
	 * 
	 * @param hex
	 *            a Hexadecimal String
	 * @return A byte array
	 */
	public static byte[] getBytes(String hex) {
		if (hex == null) {
			return null;
		}
		byte[] bytes = new byte[(hex.length() / 2)];
		byte value = 0;
		int index = 0;
		int nybble = 0;
		for (final char b : hex.toCharArray()) {
			value += HEXES.indexOf(b);
			if (nybble == 0) {
				value <<= 4;
				nybble = 1;
			} else {
				bytes[index] = value;
				value = 0;
				nybble = 0;
				index++;
			}
		}

		return bytes;
	}

	/**
	 * Convert a byte array into a hexadecimal String
	 * 
	 * @param raw
	 *            The byte array to convert
	 * @return A hexadecimal String
	 */
	public static String getHex(byte[] raw) {
		if (raw == null) {
			return null;
		}

		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}

		return hex.toString();
	}
}
