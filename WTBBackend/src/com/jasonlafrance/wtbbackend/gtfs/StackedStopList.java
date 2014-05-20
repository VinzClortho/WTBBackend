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
import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 * @author Jason LaFrance
 */
public class StackedStopList {

    private final HashMap<Vertex, LinkedList<StopAdapter>> mStackedStops;
    private final HashSet<String> badRoutes = new HashSet<>();
    private static final LinkedList<StackedStopList> sRecycleBin = new LinkedList<>();

    private StackedStopList() {
        mStackedStops = new HashMap<>();
    }

    public static StackedStopList getInstance() {
        StackedStopList ret;

        if (!sRecycleBin.isEmpty()) {
            synchronized (sRecycleBin) {
                ret = sRecycleBin.pop();
            }
        } else {
            ret = new StackedStopList();
        }

        return ret;
    }

    public void recycle() {
        mStackedStops.clear();
        sRecycleBin.push(this);
    }

    public void set(StackedStopList in) {
        mStackedStops.clear();
        for (Vertex key : in.getStopVertices()) {
            mStackedStops.put(key, in.getStopsForVertex(key));
        }
    }

    public void addStop(StopAdapter in) {
        Vertex pos = new Vertex(in.getStop().get_stop_lat(), in.getStop().get_stop_lon());

        if (mStackedStops.get(pos) == null) {
            mStackedStops.put(pos, new LinkedList<StopAdapter>());
        }

        mStackedStops.get(pos).add(in);
        if (mStackedStops.get(pos).size() > 1) {
            System.out.println("StackedStopList: " + pos.toString() + " --> #" + mStackedStops.get(pos).size());
        }
    }

    public synchronized ArrayList<Vertex> getStopVertices() {
        synchronized (mStackedStops) {
            ArrayList<Vertex> ret = new ArrayList<>(mStackedStops.keySet());
            return ret;
        }
    }

    public LinkedList<StopAdapter> getStopsForVertex(Vertex key) {
        return mStackedStops.get(key);
    }
}
