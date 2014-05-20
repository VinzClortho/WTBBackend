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
 *
 * @author Jason LaFrance
 */
public class RoutePath {

    private static int sNextID = 0;
    private static final HashMap<Integer, RoutePath> sRoutePathMap = new HashMap<>();

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

    private final double closenessThreshold = Config.get().getDoubleOption(Config.CLOSENESS_THRESHOLD); // in meters

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

    public String getCSVTripList() {
        String ret = "";
        for (int i = 0; i < mPath.size() - 1; i++) {
            ret += '"' + mPath.get(i).get_trip_id() + '"' + ',';
        }
        if (mPath.size() > 0) {
            ret += '"' + mPath.get(mPath.size() - 1).get_trip_id() + '"' + "\r\n";
        }
        return ret;
    }

    private void buildStopList() {
        for (Trip t : mPath) {
            mStops.addAll(t.getStops());
        }

        StopAdapter prevStop = null;
        for (int i = 0; i < mStops.size(); i++) {
            StopAdapter s = mStops.get(i);

            if (s.getStopTime() instanceof StopTime) {
                if (mStartTimecode <= 0 && s.getStopTime().getArrivalTimecode() > 0) {
                    mStartTime = s.getStopTime().get_arrival_time();
                    mStartTimecode = s.getStopTime().getArrivalTimecode();
                }
                if (s.getStopTime().getDepartureTimecode() > 0 && s.getStopTime().getDepartureTimecode() > mEndTimecode) {
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

    public int getRouteID() {
        return mRoute.getID();
    }
    
    public String getRouteName(){
        return mRoute.getName();
    }

    public String getRouteShortName() {
        return mRoute.getName();
    }

    public String getColor() {
        return mRoute.get_route_color();
    }

    public String getRouteLongName() {
        return mRoute.get_route_long_name();
    }

    public String getServiceID() {
        return mPath.get(0).get_service_id();
    }

    public ArrayList<StopAdapter> getStops() {
        return mStops;
    }

    public ArrayList<StopAdapter> getStopsWithTimes() {
        return mStopsWithTimes;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public int getStartTimecode() {
        return mStartTimecode;
    }

    public int getEndTimecode() {
        return mEndTimecode;
    }

    public ArrayList<Trip> getPath() {
        return mPath;
    }

    public boolean sameStops(RoutePath b) {
        boolean same = true;

        ListIterator aLi = mStops.listIterator();
        ListIterator bLi = b.getStops().listIterator();

        while (aLi.hasNext() && bLi.hasNext()) {
            Stop aS = (Stop) aLi.next();
            Stop bS = (Stop) bLi.next();
            System.out.println(aS.getID() + " -> " + bS.getID() + "   distance: " + aS.getVertex().getDistanceInMeters(bS.getVertex()));
            if (aS.getID() != bS.getID() && aS.getVertex().getDistanceInMeters(bS.getVertex()) > closenessThreshold) {
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
        String ret = "Route: " + mRoute.getName()
                + "   Service: " + mPath.get(0).get_service_id()
                + "   StartTime: " + mStartTime
                + "   EndTime: " + mEndTime
                + "   Points in path: " + vertices
                + "   Trips: " + tripsList;

        return ret;
    }
}
