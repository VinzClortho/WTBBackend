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
