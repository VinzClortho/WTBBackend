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

/**
 * StopAdapter class to allow redundant Stop representation without needing to
 * allocate entirely new Stop objects. This is basically just a wrapper class
 * for additional values: a stop time, a route association, and a reference to
 * the next stop in the route.
 * 
 * @author Jason LaFrance
 */
public class StopAdapter {

	private final Stop mStop;
	private StopTime mStopTime;
	private Route mRoute;
	private StopAdapter mNextStop;

	/**
	 * Create a new StopAdapter object to wrap the given Stop
	 * 
	 * @param inStop
	 *            The Stop to wrap up
	 */
	public StopAdapter(Stop inStop) {
		mStop = inStop;
		mStopTime = null;
		mRoute = null;
		mNextStop = null;
	}

	/**
	 * Get the next stop in the route
	 * 
	 * @return The next Stop in the path wrapped in a StopAdapter
	 */
	public StopAdapter getNextStop() {
		return mNextStop;
	}

	/**
	 * Get the associated Route
	 * 
	 * @return The Route object
	 */
	public Route getRoute() {
		return mRoute;
	}

	/**
	 * Get the raw Stop
	 * 
	 * @return The Stop object
	 */
	public Stop getStop() {
		return mStop;
	}

	/**
	 * Get the stop time
	 * 
	 * @return The StopTime object
	 */
	public StopTime getStopTime() {
		return mStopTime;
	}

	/**
	 * Set the next stop in the path
	 * 
	 * @param in
	 *            Next Stop in path wrapped in a StopAdapter
	 */
	public void setNextStop(StopAdapter in) {
		mNextStop = in;
	}

	/**
	 * Set the Route
	 * 
	 * @param in
	 *            Route object
	 */
	public void setRoute(Route in) {
		mRoute = in;
	}

	/**
	 * Set the stop time
	 * 
	 * @param in
	 *            The StopTime to set to
	 */
	public void setStopTime(StopTime in) {
		mStopTime = in;
	}
}
