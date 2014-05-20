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

package com.jasonlafrance.wtbbackend.vehicle;

import static com.jasonlafrance.wtbbackend.wtb_util.GPSCalc.getBearing;
import static com.jasonlafrance.wtbbackend.wtb_util.TimeUtil.timeToSeconds;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import com.jasonlafrance.wtbbackend.config.Config;
import com.jasonlafrance.wtbbackend.gtfs.GTFS;
import com.jasonlafrance.wtbbackend.gtfs.Route;
import com.jasonlafrance.wtbbackend.gtfs.StackedStopList;
import com.jasonlafrance.wtbbackend.gtfs.StopAdapter;
import com.jasonlafrance.wtbbackend.gtfs.Vertex;

/**
 *
 * @author Jason LaFrance
 */
public class Vehicle implements Comparable<Vehicle> {

    private static final int COORD_BUFFER_SIZE = Config.get().getIntOption(Config.COORD_BUFFER_SIZE);
    private static final double MILES_PER_METER = 0.000621371;
    private static final double MPH_TO_MPS = 0.44704;

    private static HashMap<Integer, Vehicle> mVehicles = new HashMap<>();
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");

    public static double mphToMetersPerSec(double in) {
        return in * MPH_TO_MPS;
    }

    // lazt initialize vehicles
    public static void updateVehicle(short id, float lat, float lon) {
        Vehicle v = mVehicles.get((int) id);
        //System.out.println("updateVehicle: " + v);
        if (v == null) {
            synchronized (mVehicles) {
                v = new Vehicle(id, 0, "");
                mVehicles.put((int) id, v);
            }
        }
        v.updatePosition(lat, lon, timeToSeconds(dateFormatter.format(new Date())));
    }

    public static synchronized void removeVehicle(short id) {
        Vehicle v = mVehicles.get((int) id);
        if (v != null) {
            mVehicles.remove((int) id);
        }
    }

    public static LinkedList<Vehicle> getVehicles() {
        LinkedList<Vehicle> copy = new LinkedList<>();
        synchronized (mVehicles) {
            copy.addAll(mVehicles.values());
        }
        return copy;
    }

    public static synchronized void cleanUp(int timeout) {
        int now = timeToSeconds(dateFormatter.format(new Date()));
        LinkedList<Integer> removeList = new LinkedList<>();
        for (Vehicle v : mVehicles.values()) {
            if (now - v.getLatestTimecode() > timeout) {
                removeList.add(v.getID());
            }
        }
        for (int i : removeList) {
            mVehicles.remove(i);
        }
    }

    private final HashSet<Route> mProbableRoutes = new HashSet<>();
    private final HashSet<Route> mRejectedRoutes = new HashSet<>();

    private int mID;
    private int mAgencyID;
    // lat/lon ring buffer
    private double[] mLat;
    private double[] mLon;
    // timecode is in seconds
    private int[] mTimeCode;
    private int mCoordIndex;

    //private Stop[] mStopHistory;
    private ArrayList<LinkedList<StopAdapter>> mStopHistory;

    private int mStopHistoryIndex;

    private double mHeading;
    private double mSpeed;
    private double mSpeedMPH;
    private String mDesc;
    private boolean isActive;
    private String mColor;

    public Vehicle() {
        this(-1, -1, "");
    }

    private Vehicle(int inID, int inAgencyID, String inDesc) {
        mID = inID;
        mAgencyID = inAgencyID;

        mLat = new double[COORD_BUFFER_SIZE];
        mLon = new double[COORD_BUFFER_SIZE];
        mTimeCode = new int[COORD_BUFFER_SIZE];
        //mStopHistory = new Stop[COORD_BUFFER_SIZE];
        mStopHistory = new ArrayList<>();
        mDesc = inDesc;
        mColor = "000000";

        this.reset();
    }

    public boolean isReady() {
        if (mCoordIndex > -1) {
            return true;
        } else {
            return false;
        }
    }

    public final void reset() {
        mCoordIndex = -1;
        mStopHistoryIndex = -1;
        mStopHistory.clear();

        for (int i = 0; i < COORD_BUFFER_SIZE; i++) {
            mLat[i] = Double.NaN;
            mLon[i] = Double.NaN;

            mStopHistory.add(null);
            mTimeCode[i] = -1;
        }

        mHeading = Double.NaN;
        mSpeed = Double.NaN;
        isActive = false;
    }

    public int getID() {
        return mID;
    }

    public double getLat() {
        return mLat[mCoordIndex];
    }

    public double getLon() {
        return mLon[mCoordIndex];
    }

    public int getLatestTimecode() {
        return mTimeCode[mCoordIndex];
    }

    public void setColor(String in) {
        mColor = in;
    }

    public String getColor() {
        return mColor;
    }

    public void setID(int in) {
        mID = in;
    }

    public void setAgencyID(int in) {
        mAgencyID = in;
    }

    public void setActive() {
        isActive = true;
    }

    public void setInactive() {
        isActive = false;
    }

    public void setDesc(String in) {
        mDesc = in;
    }

    private int incIndex(int in) {
        in++;
        if (in >= COORD_BUFFER_SIZE) {
            in = 0;
        }
        return in;
    }

    private synchronized void updatePosition(double inLat, double inLon, int inTimeCode) {
        // update coord index
        mCoordIndex = incIndex(mCoordIndex);
        mLat[mCoordIndex] = inLat;
        mLon[mCoordIndex] = inLon;
        mTimeCode[mCoordIndex] = inTimeCode;

        calcHeading();
        calcSpeed();
        updateStopHistory();

        //System.out.println("[" + inTimeCode + "] Bus " + mID + ":\tHeading: " + mHeading + "\tSpeed: " + mSpeedMPH + " MpH");
    }

    private void findProbableRoutes() {
        ArrayList<StopAdapter> list = new ArrayList<>();
        if (mProbableRoutes.size() == 0) {
            mRejectedRoutes.clear();
        }
        synchronized (mStopHistory) {
            int index = mStopHistoryIndex;
            for (int i = 0; i < mStopHistory.size(); i++) {
                index = incIndex(index);
                if (mStopHistory.get(index) != null) {
                    ListIterator li = mStopHistory.get(index).listIterator();
                    while (li.hasNext()) {
                        list.add((StopAdapter) li.next());
                    }
                }
            }
        }

        HashSet<Route> probableCheck = new HashSet<>();

        //mProbableRoutes.clear();
        for (int i = 0; i < list.size() - 1; i++) {
            StopAdapter checkStop = list.get(i).getNextStop();
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j) == checkStop) {
                    probableCheck.add(checkStop.getRoute());
                    list.remove(j);
                    break;
                }
            }
        }

        // any old possibilities missing?
        HashSet<Route> probableClone = (HashSet<Route>) mProbableRoutes.clone();
        probableClone.removeAll(probableCheck);

        if (!probableClone.isEmpty()) {
            Iterator i = probableClone.iterator();
            while (i.hasNext()) {
                mRejectedRoutes.add((Route) i.next());
            }
        }

        mProbableRoutes.addAll(probableCheck);
        mProbableRoutes.removeAll(mRejectedRoutes);

    }

    private LinkedList<StopAdapter> calcClosestStops() {
        Vertex closest = null;
        StackedStopList ssl = GTFS.getMasterStackedStopList();

        double shortestDist = Double.MAX_VALUE;
        ArrayList<Vertex> list = ssl.getStopVertices();
        for (int i = 0; i < list.size(); i++) {
            if (closest == null) {
                closest = list.get(i);
                shortestDist = rawDist(list.get(i));
            } else {
                double dist = rawDist(list.get(i));
                if (dist < shortestDist) {
                    closest = list.get(i);
                    shortestDist = dist;
                }
            }
        }

        return ssl.getStopsForVertex(closest);
    }

    private void updateStopHistory() {
        ArrayList<StopAdapter> stops = GTFS.getCurrentStopWindow();
        LinkedList<StopAdapter> closest = calcClosestStops();
        if (closest != null) {
            if (mStopHistoryIndex == -1) {
                mStopHistory.set(0, closest);
                mStopHistoryIndex = 0;
            } else {
                boolean duplicate = false;
                for (int i = 0; i < COORD_BUFFER_SIZE; i++) {
                    if (closest.hashCode() == mStopHistory.get(mStopHistoryIndex).hashCode()) {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    mStopHistoryIndex = incIndex(mStopHistoryIndex);
                    mStopHistory.set(mStopHistoryIndex, closest);
                    findProbableRoutes();
                }
            }
        }
    }

    public LinkedList<StopAdapter> getClosestStop() {
        return mStopHistory.get(mStopHistoryIndex);
    }

    // calculate a raw, unrooted distance for comparison
    public double rawDist(Vertex in) {
        double dist = in.get_shape_pt_lat() - this.getLat();
        dist *= dist;
        double temp = in.get_shape_pt_lon() - this.getLon();
        temp *= temp;
        dist += temp;

        return dist;
    }

    // calculate heading in degrees
    private void calcHeading() {
        int lastCoordIndex = mCoordIndex - 1;
        if (lastCoordIndex < 0) {
            lastCoordIndex = COORD_BUFFER_SIZE - 1;
        }

        // only continue if all of the coords exist
        if (!Double.isNaN(mLat[mCoordIndex])
                && !Double.isNaN(mLon[mCoordIndex])
                && !Double.isNaN(mLat[lastCoordIndex])
                && !Double.isNaN(mLon[lastCoordIndex])) {

            double tempHeading = getBearing(mLat[lastCoordIndex],
                    mLon[lastCoordIndex],
                    mLat[mCoordIndex],
                    mLon[mCoordIndex]);

            if (Double.isNaN(mHeading)) {
                mHeading = tempHeading;
            } else {
                mHeading = (mHeading + tempHeading) / 2.0;
            }
        }
    }

    public double getHeading() {
        return mHeading;
    }

    private void calcSpeed() {
        double distTraveled = 0.0;

        int minTimeCode = Integer.MAX_VALUE;
        int maxTimeCode = 0;

        // check all of the points;
        int index[] = new int[2];
        index[0] = mCoordIndex + 1;
        index[1] = mCoordIndex + 2;

        if (index[0] >= COORD_BUFFER_SIZE) {
            index[0] -= COORD_BUFFER_SIZE;
        }
        if (index[1] >= COORD_BUFFER_SIZE) {
            index[1] -= COORD_BUFFER_SIZE;
        }

        for (int i = 0; i < COORD_BUFFER_SIZE - 1; i++) {
            if (!Double.isNaN(mLat[index[0]])
                    && !Double.isNaN(mLon[index[0]])
                    && !Double.isNaN(mLat[index[1]])
                    && !Double.isNaN(mLon[index[1]])) {
                distTraveled += com.jasonlafrance.wtbbackend.wtb_util.GPSCalc.getDistanceInMeters(
                        mLat[index[0]], mLon[index[0]],
                        mLat[index[1]], mLon[index[1]]);
            }
            index[0] = index[1];
            index[1]++;
            if (index[1] >= COORD_BUFFER_SIZE) {
                index[1] = 0;
            }

            if (mTimeCode[i] >= 0) {
                if (mTimeCode[i] < minTimeCode) {
                    minTimeCode = mTimeCode[i];
                }
                if (mTimeCode[i] > maxTimeCode) {
                    maxTimeCode = mTimeCode[i];
                }
            }
        }

        // now calculate the average speed in meters per sec
        mSpeed = distTraveled / (maxTimeCode - minTimeCode);
        mSpeedMPH = mSpeed * MILES_PER_METER * 3600;
    }

    public double getSpeedInMPH() {
        return mSpeedMPH;
    }

    @Override
    public int compareTo(Vehicle v) {
        if (v.getID() < mID) {
            return -1;
        } else if (v.getID() > mID) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        String output = "ID: " + mID + "   "
                + String.format("%.7g%n", mLat[mCoordIndex]) + ", "
                + String.format("%.7g%n", mLon[mCoordIndex])
                + "   Speed: " + String.format("%.4g%n", mSpeedMPH);

        if (mStopHistoryIndex >= 0 && mStopHistoryIndex < mStopHistory.size() && mStopHistory.get(mStopHistoryIndex) != null) {
            output += "   Closest Stop: " + mStopHistory.get(mStopHistoryIndex).getFirst().getStop().get_stop_name() + " ( ";
        }
        for (Route r : mProbableRoutes.toArray(new Route[mProbableRoutes.size()])) {
            if (r.getAgency() != null && r.getAgency().get_agency_name() != null) {
                output += r.getAgency().get_agency_name() + ": ";
            }
            if (r.getName() != null) {
                output += r.getName() + " ";
            }
        }

        output += ")";
        return output;
    }
}
