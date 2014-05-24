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
import java.util.HashMap;
import java.util.ListIterator;

import com.jasonlafrance.wtbbackend.config.Config;
import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 * RoutePath class for managing directed Stop lists for routes
 * 
 * @author Jason LaFrance
 */
public class RoutePath {

	private static int sNextID = 0;
	private static final HashMap<Integer, RoutePath> sRoutePathMap = new HashMap<>();

	/**
	 * Get a RoutePath from the multition by ID
	 * 
	 * @param id
	 *            The ID of the RoutePath to get
	 * @return The requested RoutePath, or null if doesn't exist
	 */
	public static RoutePath getRoutePath(int id) {
		return sRoutePathMap.get(id);
	}

	private final int mID;
	private final ArrayList<Trip> mPath;
	private final Route mRoute;
	private final ArrayList<StopAdapter> mStops = new ArrayList<>();
	private final ArrayList<StopAdapter> mStopsWithTimes = new ArrayList<>();

	private String mStartTime, mEndTime;
	private int mStartTimecode, mEndTimecode;

	private final double closenessThreshold = Config.getInstance()
			.getDoubleOption(Config.CLOSENESS_THRESHOLD); // in meters

	/**
	 * Construct a RoutePath object from given data
	 * 
	 * @param inPath
	 *            List of Trips to build path from
	 * @param inRoute
	 *            Route to associate path to
	 */
	public RoutePath(ArrayList<Trip> inPath, Route inRoute) {
		mID = sNextID;
		sNextID++;
		sRoutePathMap.put(mID, this);

		mPath = inPath;
		mRoute = inRoute;
		mStartTimecode = -1;
		mEndTimecode = Integer.MIN_VALUE;

		buildStopList();
	}

	/**
	 * Construct a RoutePath from a stored CSV String and a HashMap of Trips and
	 * their IDs
	 * 
	 * @param inLine
	 *            The raw CSV String
	 * @param tripMap
	 *            The TripMap HashSet
	 */
	public RoutePath(String inLine, HashMap<String, Trip> tripMap) {
		mID = sNextID;
		sNextID++;
		sRoutePathMap.put(mID, this);

		mPath = new ArrayList<>();

		String[] f = CSVParser.parseLine(inLine);
		for (String s : f) {
			s = s.replaceAll("\"", "");
			if (tripMap.get(s) != null) {
				mPath.add(tripMap.get(s));
			}
		}

		if (mPath.size() > 0) {
			mRoute = mPath.get(0).getRoute();
		} else {
			mRoute = null;
		}

		mStartTimecode = -1;
		mEndTimecode = Integer.MIN_VALUE;

		buildStopList();
	}

	/**
	 * Build a list of stops from the stored data
	 */
	private void buildStopList() {
		for (Trip t : mPath) {
			mStops.addAll(t.getStops());
		}

		StopAdapter prevStop = null;
		for (int i = 0; i < mStops.size(); i++) {
			StopAdapter s = mStops.get(i);

			if (s.getStopTime() instanceof StopTime) {
				if (mStartTimecode <= 0
						&& s.getStopTime().getArrivalTimecode() > 0) {
					mStartTime = s.getStopTime().get_arrival_time();
					mStartTimecode = s.getStopTime().getArrivalTimecode();
				}
				if (s.getStopTime().getDepartureTimecode() > 0
						&& s.getStopTime().getDepartureTimecode() > mEndTimecode) {
					mEndTime = s.getStopTime().get_departure_time();
					mEndTimecode = s.getStopTime().getDepartureTimecode();
				}

			}

			if (s.getStopTime() != null) {
				mStopsWithTimes.add(s);
			}
			if (prevStop != null) {
				prevStop.setNextStop(s);
			}
			prevStop = s;
		}
	}

	/**
	 * Get the route color
	 * 
	 * @return The route color
	 */
	public String getColor() {
		return mRoute.get_route_color();
	}

	/**
	 * Generate a CSV String from the stored trip list
	 * 
	 * @return A CSV String of the trip list
	 */
	public String getCSVTripList() {
		String ret = "";
		for (int i = 0; i < mPath.size() - 1; i++) {
			ret += '"' + mPath.get(i).get_trip_id() + '"' + ',';
		}
		if (mPath.size() > 0) {
			ret += '"' + mPath.get(mPath.size() - 1).get_trip_id() + '"'
					+ "\r\n";
		}
		return ret;
	}

	/**
	 * Get the overall end time
	 * 
	 * @return End time
	 */
	public String getEndTime() {
		return mEndTime;
	}

	/**
	 * Get the overall end time code
	 * 
	 * @return End time code
	 */
	public int getEndTimecode() {
		return mEndTimecode;
	}

	/**
	 * Get a list of Trips for the overall path
	 * 
	 * @return List of Trips in the path
	 */
	public ArrayList<Trip> getPath() {
		return mPath;
	}

	/**
	 * Get the associated route ID
	 * 
	 * @return The Route ID
	 */
	public int getRouteID() {
		return mRoute.getID();
	}

	/**
	 * Get the long name
	 * 
	 * @return The lone name
	 */
	public String getRouteLongName() {
		return mRoute.get_route_long_name();
	}

	/**
	 * Get the route name
	 * 
	 * @return The route name
	 */
	public String getRouteName() {
		return mRoute.getName();
	}

	/**
	 * Get the route short name
	 * 
	 * @return The short name
	 */
	public String getRouteShortName() {
		return mRoute.getName();
	}

	/**
	 * Get the service ID
	 * 
	 * @return Service ID
	 */
	public String getServiceID() {
		return mPath.get(0).get_service_id();
	}

	/**
	 * Get the start time
	 * 
	 * @return The start time
	 */
	public String getStartTime() {
		return mStartTime;
	}

	/**
	 * Get the start time code
	 * 
	 * @return Start time code
	 */
	public int getStartTimecode() {
		return mStartTimecode;
	}

	/**
	 * Get the list of Stops in this path
	 * 
	 * @return A list if Stops wrapped in StopAdapters
	 */
	public ArrayList<StopAdapter> getStops() {
		return mStops;
	}

	/**
	 * Get an in-order list of just the stops with scheduled times
	 * 
	 * @return A list of Stops wrapped in StopAdapters
	 */
	public ArrayList<StopAdapter> getStopsWithTimes() {
		return mStopsWithTimes;
	}

	/**
	 * Check if this RoutePath shares all of the same stops in the same order as
	 * another RoutePath
	 * 
	 * @param b
	 *            The RoutePath to check against
	 * @return True if they match, otherwise false
	 */
	public boolean sameStops(RoutePath b) {
		boolean same = true;

		ListIterator aLi = mStops.listIterator();
		ListIterator bLi = b.getStops().listIterator();

		while (aLi.hasNext() && bLi.hasNext()) {
			Stop aS = (Stop) aLi.next();
			Stop bS = (Stop) bLi.next();
			System.out.println(aS.getID() + " -> " + bS.getID()
					+ "   distance: "
					+ aS.getVertex().getDistanceInMeters(bS.getVertex()));
			if (aS.getID() != bS.getID()
					&& aS.getVertex().getDistanceInMeters(bS.getVertex()) > closenessThreshold) {
				same = false;
				break;
			}
		}

		if (same && (aLi.hasNext() || bLi.hasNext())) {
			System.out.println("Stops left!");
			same = false;
		}

		return same;
	}

	@Override
	public String toString() {
		int vertices = 0;
		String tripsList = "";
		for (Trip t : mPath) {
			vertices += t.getVertices().size() - 1;
			tripsList += t.get_trip_id() + " ";
		}
		String ret = "Route: " + mRoute.getName() + "   Service: "
				+ mPath.get(0).get_service_id() + "   StartTime: " + mStartTime
				+ "   EndTime: " + mEndTime + "   Points in path: " + vertices
				+ "   Trips: " + tripsList;

		return ret;
	}
}
