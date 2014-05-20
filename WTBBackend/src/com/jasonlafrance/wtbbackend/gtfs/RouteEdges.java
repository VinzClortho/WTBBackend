package com.jasonlafrance.wtbbackend.gtfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.jasonlafrance.wtbbackend.wtb_util.Graph.Node;

/**
 *
 * @author Jason LaFrance
 */
public class RouteEdges {

    private static final HashMap<Integer, RouteEdges> sEdges = new HashMap<>();

    public static void addEdge(Node a, Node b, Route inRoute) {
        RouteEdges re = new RouteEdges(a, b, inRoute);
        if (sEdges.get(re.hashCode()) == null) {
            sEdges.put(re.hashCode(), re);
        }
        sEdges.get(re.hashCode()).addRoute(inRoute);
    }

    public static int genHashCode(Node a, Node b) {
        return a.hashCode() * 31 + b.hashCode();
    }

    public static void clearEdges() {
        sEdges.clear();
    }

    private final Node mA, mB;
    private final HashSet<Route> mRoutes = new HashSet<>();
    private final int mHashCode;

    private RouteEdges(Node a, Node b, Route inRoute) {
        if (a.hashCode() < b.hashCode()) {
            mA = a;
            mB = b;
        } else {
            mA = b;
            mB = a;
        }
        mRoutes.add(inRoute);
        mHashCode = genHashCode(mA, mB);
        sEdges.put(mHashCode, this);
    }

    private void addRoute(Route in) {
        mRoutes.add(in);
    }

    private ArrayList<Route> getRoutes() {
        ArrayList<Route> ret = new ArrayList<>();
        ret.addAll(Arrays.asList(mRoutes.toArray(new Route[mRoutes.size()])));
        return ret;
    }

    @Override
    public int hashCode() {
        return mHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RouteEdges other = (RouteEdges) obj;
        return this.mHashCode == other.mHashCode;
    }

}
