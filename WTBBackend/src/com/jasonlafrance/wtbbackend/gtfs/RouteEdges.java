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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.jasonlafrance.wtbbackend.wtb_util.Graph.Node;

/**
 * RouteEdges class used for vehicle route determination
 * 
 * @author Jason LaFrance
 */
public class RouteEdges {

	private static final HashMap<Integer, RouteEdges> sEdges = new HashMap<>();

	/**
	 * Add an edge to the current route
	 * 
	 * @param a
	 *            Node A
	 * @param b
	 *            Node B
	 * @param inRoute
	 *            The route to add the edge to
	 */
	public static void addEdge(Node a, Node b, Route inRoute) {
		RouteEdges re = new RouteEdges(a, b, inRoute);
		if (sEdges.get(re.hashCode()) == null) {
			sEdges.put(re.hashCode(), re);
		}
		sEdges.get(re.hashCode()).addRoute(inRoute);
	}

	/**
	 * Clear all edges from current route
	 */
	public static void clearEdges() {
		sEdges.clear();
	}

	/**
	 * Generate a psuedo-unique hashcode for a pair of Nodes
	 * 
	 * @param a
	 *            Node A
	 * @param b
	 *            Node B
	 * @return The generated hashcode
	 */
	public static int genHashCode(Node a, Node b) {
		return a.hashCode() * 31 + b.hashCode();
	}

	private final Node mA, mB;
	private final HashSet<Route> mRoutes = new HashSet<>();
	private final int mHashCode;

	/**
	 * Construct a RouteEdges object from given data
	 * 
	 * @param a
	 *            Node A
	 * @param b
	 *            Node B
	 * @param inRoute
	 *            The route to add edge to
	 */
	private RouteEdges(Node a, Node b, Route inRoute) {
		// since the direction of the edges doesn't matter here, sort them so
		// that duplicate edges shouldn't occur
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

	/**
	 * Add a route
	 * 
	 * @param in
	 *            The route to add
	 */
	private void addRoute(Route in) {
		mRoutes.add(in);
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

	/**
	 * Get a list of Routes
	 * 
	 * @return The list of Routes
	 */
	private ArrayList<Route> getRoutes() {
		ArrayList<Route> ret = new ArrayList<>();
		ret.addAll(Arrays.asList(mRoutes.toArray(new Route[mRoutes.size()])));
		return ret;
	}

	@Override
	public int hashCode() {
		return mHashCode;
	}

}
