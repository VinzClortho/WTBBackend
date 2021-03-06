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

import java.util.HashSet;

/**
 * Class for dealing with graph structures
 * 
 * @author Jason LaFrance
 */
public class Graph {

	/**
	 * Edge class
	 * 
	 * @author Jason LaFrance
	 * 
	 */
	public static class Edge {

		private final Node A, B;
		private final int eHashcode;
		private String eColor;

		/**
		 * Create an Edge connecting two Nodes
		 * 
		 * @param a
		 *            Node A
		 * @param b
		 *            Node B
		 */
		public Edge(Node a, Node b) {
			if (a.hashCode() < b.hashCode()) {
				A = a;
				B = b;
			} else {
				A = b;
				B = a;
			}
			eHashcode = A.hashCode() * 3131 + B.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Edge other = (Edge) obj;
			if (this.eHashcode != other.eHashcode) {
				return false;
			}
			return true;
		}

		/**
		 * Get Node A
		 * 
		 * @return Node A
		 */
		public Node getA() {
			return A;
		}

		/**
		 * Get Node B
		 * 
		 * @return Node B
		 */
		public Node getB() {
			return B;
		}

		/**
		 * Get the edge's color
		 * 
		 * @return Edge color
		 */
		public String getColor() {
			return eColor;
		}

		@Override
		public int hashCode() {
			return eHashcode;
		}

		/**
		 * Set the edge's color
		 * 
		 * @param in
		 *            Color value to set
		 */
		public void setColor(String in) {
			eColor = in;
		}

	}

	/**
	 * Node class
	 * 
	 * @author Jason LaFrance
	 * 
	 */
	public static class Node {

		private final double nLat, nLon;
		private final HashSet<Node> nLinks;
		private final int nHashcode;

		/**
		 * Create a Node at the given coordinates
		 * 
		 * @param inLat
		 *            Latitude
		 * @param inLon
		 *            Longitude
		 */
		public Node(double inLat, double inLon) {
			nLat = inLat;
			nLon = inLon;
			nLinks = new HashSet<>();
			// Generate the hash value
			nHashcode = (int) (Double.doubleToRawLongBits(nLat) * 31 + Double
					.doubleToRawLongBits(nLon));
		}

		/**
		 * Add a link to another Node
		 * 
		 * @param in
		 *            Node to link to
		 */
		public void addLink(Node in) {
			nLinks.add(in);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Node other = (Node) obj;
			if (this.nHashcode != other.nHashcode) {
				return false;
			}
			return true;
		}

		/**
		 * Get the latitude
		 * 
		 * @return Latidude
		 */
		public double getLat() {
			return nLat;
		}

		/**
		 * Get a list of Nodes linked to this one
		 * 
		 * @return List of Nodes
		 */
		public Node[] getLinks() {
			return nLinks.toArray(new Node[nLinks.size()]);
		}

		/**
		 * Get the longitude
		 * 
		 * @return Longitude
		 */
		public double getLon() {
			return nLon;
		}

		@Override
		public int hashCode() {
			return nHashcode;
		}
	}
}
