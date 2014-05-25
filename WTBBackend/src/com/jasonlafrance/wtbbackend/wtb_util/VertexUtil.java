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

package com.jasonlafrance.wtbbackend.wtb_util;

import com.jasonlafrance.wtbbackend.gtfs.Vertex;

/**
 * Vertex calculation fuction class
 * 
 * @author Jason LaFrance
 */
public class VertexUtil {

	/**
	 * Given a line connecting a pair of vertices, find the point on that line
	 * that when connected to a third given point produces a line perpendicular
	 * to the first, ie, the point of line AB that is closest to point c.
	 * 
	 * @param a
	 *            First point of given line
	 * @param b
	 *            Second point of given line
	 * @param c
	 *            Point to find connectiong point for
	 * @return The Vertex representing the intersection point which gives the
	 *         shortest line to point c
	 */
	public static Vertex getLineSegmentIntersect(Vertex a, Vertex b, Vertex c) {

		final double xD = b.get_shape_pt_lat() - a.get_shape_pt_lat();
		final double yD = b.get_shape_pt_lon() - a.get_shape_pt_lon();
		final double u = ((c.get_shape_pt_lat() - a.get_shape_pt_lat()) * xD + (c
				.get_shape_pt_lon() - a.get_shape_pt_lon()) * yD)
				/ (xD * xD + yD * yD);
		final Vertex intersect;

		if (u < 0) {
			intersect = a;
		} else if (u > 1) {
			intersect = b;
		} else {
			intersect = new Vertex(a.get_shape_pt_lat() + u * xD,
					a.get_shape_pt_lon() + u * yD);
		}

		return intersect;
	}
	/*
	 * public void removeRedundantVertices(LinkedList<LinkedList<Trip>> paths) {
	 * if (mVertices.isEmpty()) { return; }
	 * 
	 * LinkedList<Vertex> newVertices = new LinkedList<>(); ListIterator li =
	 * mVertices.listIterator(); Vertex last = (Vertex) li.next();
	 * newVertices.add(last); Vertex current; while (li.hasNext()) { current =
	 * (Vertex) li.next(); if (last.get_shape_pt_lat() !=
	 * current.get_shape_pt_lat() && last.get_shape_pt_lon() !=
	 * current.get_shape_pt_lon()) { newVertices.add(current); } last = current;
	 * } mVertices = newVertices; }
	 */
}
