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
 * GTFS FareAttribute class
 * 
 * @author Jason LaFrance
 */
public final class FareAttribute extends GTFSParser {

	private static final String _filename = "//fare_attributes.txt";

	private String _fare_id = null;
	private double _price = 0.0;
	private String _currency_type = null;
	private int _payment_method = -1;
	private int _transfers = -1;
	private double _transfer_duration = 0.0;

	/**
	 * Stub constructor
	 */
	public FareAttribute() {
	}

	/**
	 * Stub constructor
	 * 
	 * @param inLine
	 *            A String.
	 */
	public FareAttribute(String inLine) {
	}

	/**
	 * Create a FareAttribute object from the given values.
	 * 
	 * @param inID
	 *            ID
	 * @param inPrice
	 *            Fare price
	 * @param inType
	 *            Fare type
	 * @param inMethod
	 *            Fare method
	 * @param inTransfers
	 *            Number of transfers
	 * @param inTransDur
	 *            Transfer duration
	 */
	public FareAttribute(String inID, double inPrice, String inType,
			int inMethod, int inTransfers, double inTransDur) {
		_fare_id = inID;
		_price = inPrice;
		_currency_type = inType;
		_payment_method = inMethod;
		_transfers = inTransfers;
		_transfer_duration = inTransDur;
	}

	/**
	 * Get the currency type
	 * 
	 * @return currency type
	 */
	public String get_currency_type() {
		return _currency_type;
	}

	/**
	 * Get the fare ID
	 * 
	 * @return Fare ID
	 */
	public String get_fare_id() {
		return _fare_id;
	}

	/**
	 * Get the payment method
	 * 
	 * @return Payment method
	 */
	public int get_payment_method() {
		return _payment_method;
	}

	/**
	 * Get the price
	 * 
	 * @return Price
	 */
	public double get_price() {
		return _price;
	}

	/**
	 * Get the transfer duration
	 * 
	 * @return Transfer duration
	 */
	public double get_transfer_duration() {
		return _transfer_duration;
	}

	/**
	 * Get the number of transfers
	 * 
	 * @return Number of transfers
	 */
	public int get_transfers() {
		return _transfers;
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
	 * Set the currency type
	 * 
	 * @param in
	 *            Currency type
	 */
	public void set_currency_type(String in) {
		_currency_type = in;
	}

	/**
	 * Set the fare ID
	 * 
	 * @param in
	 *            ID
	 */
	public void set_fare_id(String in) {
		_fare_id = in;
	}

	/**
	 * Set the payment method
	 * 
	 * @param in
	 *            Payment method
	 */
	public void set_payment_method(int in) {
		_payment_method = in;
	}

	/**
	 * Set the payment method
	 * 
	 * @param in
	 *            Payment method
	 */
	public void set_payment_method(String in) {
		try {
			_payment_method = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set the fare price
	 * 
	 * @param in
	 *            Price
	 */
	public void set_price(double in) {
		_price = in;
	}

	/**
	 * Set the fare price
	 * 
	 * @param in
	 *            Price
	 */
	public void set_price(String in) {
		try {
			_price = Double.parseDouble(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set the transfer duration
	 * 
	 * @param in
	 *            Transfer duration
	 */
	public void set_transfer_duration(double in) {
		_transfer_duration = in;
	}

	/**
	 * Set the transfer duration
	 * 
	 * @param in
	 *            Transfer duration
	 */
	public void set_transfer_duration(String in) {
		try {
			_transfer_duration = Double.parseDouble(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set the number of transfers
	 * 
	 * @param in
	 *            Number of transfers
	 */
	public void set_transfers(int in) {
		_transfers = in;
	}

	/**
	 * Set the number of transfers
	 * 
	 * @param in
	 *            Number of transfers
	 */
	public void set_transfers(String in) {
		try {
			_transfers = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	@Override
	public String toString() {
		return _fare_id + "," + _price + "," + _currency_type + ","
				+ _payment_method + "," + _transfers + "," + _transfer_duration
				+ "\n";
	}
}
