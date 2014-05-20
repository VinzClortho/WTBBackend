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
 *
 * @author Jason LaFrance
 */
public class StopAdapter {

    private final Stop mStop;
    private StopTime mStopTime;
    private Route mRoute;
    private StopAdapter mNextStop;
    //private float mLat, mLon;

    public StopAdapter(Stop inStop) {
        mStop = inStop;
        //mLat = (float)inStop.get_stop_lat();
        //mLon = (float)inStop.get_stop_lon();
        mStopTime = null;
        mRoute = null;
        mNextStop = null;
    }
    /*
    public double getLat() {return mLat;}
    public double getLon() {return mLon;}
    
    public void setCoordinates(double inLat, double inLon){
        mLat = (float)inLat;
        mLon = (float)inLon;
    }
    */

    public Stop getStop() {
        return mStop;
    }

    public StopTime getStopTime() {
        return mStopTime;
    }

    public Route getRoute() {
        return mRoute;
    }

    public StopAdapter getNextStop() {
        return mNextStop;
    }

    public void setStopTime(StopTime in) {
        mStopTime = in;
    }

    public void setRoute(Route in) {
        mRoute = in;
    }

    public void setNextStop(StopAdapter in) {
        mNextStop = in;
    }
}
