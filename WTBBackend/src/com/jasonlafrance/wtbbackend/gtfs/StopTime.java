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

import static com.jasonlafrance.wtbbackend.wtb_util.TimeUtil.minutesToTime;
import static com.jasonlafrance.wtbbackend.wtb_util.TimeUtil.timeToMinutes;

import java.util.ArrayList;

import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 * GTFS StopTime class
 * 
 * @author Jason LaFrance
 */
public final class StopTime extends GTFSParser implements Comparable<StopTime> {

	private static final String _filename = "//stop_times.txt";

	private String _trip_id = null;
	// private String _arrival_time = "";
	private int _arrivalTimecode = -1;
	// private String _departure_time = "";
	private int _departureTimecode = -1;

	private String _stop_id = null;
	private int _stop_sequence = -1;
	private String _stop_headsign = null;
	private int _pickup_type = -1;
	private int _drop_off_type = -1;
	private double _shape_dist_traveled = 0.0;
	private String _timepoint = null;
	private String _continuous_stops = null;

	/**
	 * Stub constructor
	 */
	public StopTime() {
	}

	/**
	 * Stub constructor
	 * 
	 * @param inLine
	 *            A String
	 */
	public StopTime(String inLine) {
	}

	/**
	 * Create a StopTime object from given values
	 * 
	 * @param inTripID
	 *            Trip ID
	 * @param inArrival
	 *            Arrival time code
	 * @param inDepart
	 *            Departure time code
	 * @param inStopID
	 *            Stop ID
	 * @param inStopSequence
	 *            Stop sequence number
	 * @param inHeadsign
	 *            Headsign text
	 * @param inPickupType
	 *            Pickup type
	 * @param inDropoffType
	 *            Drop off type
	 * @param inDistTraveled
	 *            Distance traveled in trip
	 */
	public StopTime(String inTripID, String inArrival, String inDepart,
			String inStopID, int inStopSequence, String inHeadsign,
			int inPickupType, int inDropoffType, double inDistTraveled) {
		_trip_id = inTripID;
		// _arrival_time = inArrival;
		_arrivalTimecode = timeToMinutes(inArrival);
		// _departure_time = inDepart;
		_departureTimecode = timeToMinutes(inDepart);
		_stop_id = inStopID;
		_stop_sequence = inStopSequence;
		_stop_headsign = inHeadsign;
		_pickup_type = inPickupType;
		_drop_off_type = inDropoffType;
		_shape_dist_traveled = inDistTraveled;
	}

	@Override
	public int compareTo(StopTime o) {
		// compare _trip_id
		int ret = _trip_id.compareTo(o.get_trip_id());
		if (ret == 0) {
			// compare _stop_sequence
			if (o.get_stop_sequence() < _stop_sequence) {
				ret = 1;
			}
			if (o.get_stop_sequence() > _stop_sequence) {
				ret = -1;
			}
		}
		// if we make it here, then the two are congruent
		return ret;
	}

	/**
	 * Get the arrival time
	 * 
	 * @return Arrival time
	 */
	public String get_arrival_time() {
		return minutesToTime(_arrivalTimecode);
	}

	/**
	 * Get continuous stops
	 * 
	 * @return Continuous stops
	 */
	public String get_continuous_stops() {
		return _continuous_stops;
	}

	/**
	 * Get the departure time
	 * 
	 * @return Departure time
	 */
	public String get_departure_time() {
		return minutesToTime(_departureTimecode);
	}

	/**
	 * Get the drop off type
	 * 
	 * @return Drop off type
	 */
	public int get_drop_off_type() {
		return _drop_off_type;
	}

	/**
	 * Get the pickup type
	 * 
	 * @return Pickup type
	 */
	public int get_pickup_type() {
		return _pickup_type;
	}

	/**
	 * Get distance traveled in trip
	 * 
	 * @return Distance traveled
	 */
	public double get_shape_dist_traveled() {
		return _shape_dist_traveled;
	}

	/**
	 * Get stop headsign text
	 * 
	 * @return Headsign text
	 */
	public String get_stop_headsign() {
		return _stop_headsign;
	}

	/**
	 * Get the stop ID
	 * 
	 * @return Stop ID
	 */
	public String get_stop_id() {
		return _stop_id;
	}

	/**
	 * Get the stop sequence
	 * 
	 * @return Stop sequence
	 */
	public int get_stop_sequence() {
		return _stop_sequence;
	}

	/**
	 * Get the time point
	 * 
	 * @return Time point
	 */
	public String get_timepoint() {
		return _timepoint;
	}

	/**
	 * Get the trip ID
	 * 
	 * @return Trip ID
	 */
	public String get_trip_id() {
		return _trip_id;
	}

	/**
	 * Get the arrival time code
	 * 
	 * @return Arrival time code
	 */
	public int getArrivalTimecode() {
		return _arrivalTimecode;
	}

	/**
	 * Get the departure time code
	 * 
	 * @return Departure time code
	 */
	public int getDepartureTimecode() {
		return _departureTimecode;
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
		return _trip_id.hashCode();
	}

	/**
	 * Set the arrival time
	 * 
	 * @param in
	 *            Arrival time
	 */
	public void set_arrival_time(String in) {
		_arrivalTimecode = timeToMinutes(in);
	}

	/**
	 * Set continuous stops
	 * 
	 * @param _continuous_stops
	 *            The continuous stops value to set
	 */
	public void set_continuous_stops(String _continuous_stops) {
		this._continuous_stops = _continuous_stops;
	}

	/**
	 * Set the departure time
	 * 
	 * @param in
	 *            Departure time
	 */
	public void set_departure_time(String in) {
		_departureTimecode = timeToMinutes(in);
	}

	/**
	 * Set the drop off type
	 * 
	 * @param in
	 *            Drop off type
	 */
	public void set_drop_off_type(int in) {
		_drop_off_type = in;
	}

	/**
	 * Set the drop off type
	 * 
	 * @param in
	 *            Drop off type
	 */
	public void set_drop_off_type(String in) {
		try {
			_drop_off_type = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set the pickup type
	 * 
	 * @param in
	 *            Pickup type
	 */
	public void set_pickup_type(int in) {
		_pickup_type = in;
	}

	/**
	 * Set the pickup type
	 * 
	 * @param in
	 *            Pickup type
	 */
	public void set_pickup_type(String in) {
		try {
			_pickup_type = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set distance traveled
	 * 
	 * @param in
	 *            Distance
	 */
	public void set_shape_dist_traveled(double in) {
		_shape_dist_traveled = in;
	}

	/**
	 * Set distance traveled
	 * 
	 * @param in
	 *            Distance
	 */
	public void set_shape_dist_traveled(String in) {
		try {
			_shape_dist_traveled = Double.parseDouble(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set headsign text
	 * 
	 * @param in
	 *            Headsign text
	 */
	public void set_stop_headsign(String in) {
		_stop_headsign = in;
	}

	/**
	 * Set stop ID
	 * 
	 * @param in
	 *            Stop ID
	 */
	public void set_stop_id(String in) {
		_stop_id = in;
	}

	/**
	 * Set stop sequence order
	 * 
	 * @param in
	 *            Sequence order
	 */
	public void set_stop_sequence(int in) {
		_stop_sequence = in;
	}

	/**
	 * Set stop sequence order
	 * 
	 * @param in
	 *            Sequence order
	 */
	public void set_stop_sequence(String in) {
		try {
			_stop_sequence = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set time point
	 * 
	 * @param _timepoint
	 *            The time point to set
	 */
	public void set_timepoint(String _timepoint) {
		this._timepoint = _timepoint;
	}

	/**
	 * Set the trip ID
	 * 
	 * @param in
	 *            Trip ID
	 */
	public void set_trip_id(String in) {
		_trip_id = in;
	}

	@Override
	public String toString() {
		return _trip_id + "," + minutesToTime(_arrivalTimecode) + ","
				+ minutesToTime(_arrivalTimecode) + "," + _stop_id + ","
				+ _stop_sequence + "," + _stop_headsign + "," + _pickup_type
				+ "," + _drop_off_type + "," + _shape_dist_traveled + "\n";
	}
}
