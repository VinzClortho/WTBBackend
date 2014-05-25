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
import java.util.Collections;
import java.util.ListIterator;

import static com.jasonlafrance.wtbbackend.gtfs.GTFSParser._headers;
import static com.jasonlafrance.wtbbackend.wtb_util.VertexUtil.getLineSegmentIntersect;

import java.util.Arrays;
import java.util.Comparator;

import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 * GTFS Trip class
 * 
 * @author Jason LaFrance
 */
public final class Trip extends GTFSParser implements Comparable<Trip> {

	/**
	 * Trip.Insertion class to help manage inserting vertices into a trip list
	 * by associating a Vertex with an index value.
	 * 
	 * @author Jason LaFrance
	 * 
	 */
	private class Insertion {

		private int index;
		private Vertex item;

		/**
		 * Create an Insertion object with given index and Vertex
		 * 
		 * @param index
		 *            Index value
		 * @param item
		 *            The Vertex to insert
		 */
		public Insertion(int index, Vertex item) {
			this.index = index;
			this.item = item;
		}

		/**
		 * Get the index value
		 * 
		 * @return Index
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * Get the Vertex
		 * 
		 * @return Vertex
		 */
		public Vertex getVertex() {
			return item;
		}
	}

	private static final String _filename = "//trips.txt";

	private String _route_id = null;

	private String _service_id = null;
	private String _trip_id = null;
	private String _trip_headsign = null;
	private String _trip_short_name = null;
	private int _direction_id = -1;
	private String _block_id = null;
	private String _shape_id = null;
	private String _trip_type = null;
	private int _wheelchair_accessible = -1;
	private ArrayList<Vertex> mVertices;

	private ArrayList<StopAdapter> mStops;
	private Route mRoute;
	private int mStartTime, mEndTime;
	private int mStartStopID, mEndStopID;

	/**
	 * Stub constructor
	 * 
	 * @param inLine A String
	 */
	public Trip(String inLine) {
	}

	/**
	 * Create a Trip object with a supplied line from a GTFS trips.txt table.
	 * 
	 * @param inLine
	 *            Line from a GTFS trips.txt table.
	 * @param id
	 *            The GTFS ID from the GTFS multition that this object belongs
	 *            to.
	 */
	public Trip(String inLine, int id) {
		ArrayList<String> h = _headers.get(id).get(_filename);
		String[] f = CSVParser.parseLine(inLine);

		if (f.length != h.size()) {
			return;
		}

		for (int i = 0; i < f.length; i++) {
			switch (h.get(i)) {
			case "block_id":
				set_block_id(f[i]);
				break;
			case "direction_id":
				set_direction_id(f[i]);
				break;
			case "route_id":
				set_route_id(f[i]);
				break;
			case "service_id":
				set_service_id(f[i]);
				break;
			case "shape_id":
				set_shape_id(f[i]);
				break;
			case "trip_headsign":
				set_trip_headsign(f[i]);
				break;
			case "trip_id":
				set_trip_id(f[i]);
				break;
			case "trip_short_name":
				set_trip_short_name(f[i]);
				break;
			case "trip_type":
				set_trip_type(f[i]);
				break;
			case "wheelchair_accessible":
				set_wheelchair_accessible(f[i]);
				break;
			default:
				break;
			}
		}
		resetStructure();
	}

	/**
	 * Create a Trip object from given values
	 * 
	 * @param inRouteID
	 *            The route ID
	 * @param inServiceID
	 *            The service ID
	 * @param inTripID
	 *            The Trip ID
	 * @param inHeadsign
	 *            The headsign text
	 * @param inDirection
	 *            The trip direction
	 * @param inBlockID
	 *            The block ID
	 * @param inShapeID
	 *            The shape ID associated with this trip
	 */
	public Trip(String inRouteID, String inServiceID, String inTripID,
			String inHeadsign, int inDirection, String inBlockID,
			String inShapeID) {
		_route_id = inRouteID;
		_service_id = inServiceID;
		_trip_id = inTripID;
		_trip_headsign = inHeadsign;
		_trip_short_name = "";
		_direction_id = inDirection;
		_block_id = inBlockID;
		_shape_id = inShapeID;

		resetStructure();
	}

	/**
	 * Add a stop to this trip
	 * 
	 * @param in
	 *            A Stop object wrapped in a StopAdapter
	 */
	public void addStop(StopAdapter in) {
		if (mStartStopID == -1) {
			mStartStopID = in.getStop().getID();
		}
		mEndStopID = in.getStop().getID();
		mStops.add(in);
	}

	/**
	 * Add stop coordinates to the Vertex list
	 */
	public void addStopsToVertexList() {
		addStopsToVertexList(mVertices, mStops);
	}

	/**
	 * Add stop coordinates as vertices to the Vertex list
	 * 
	 * @param vertices
	 *            A Vertex list
	 * @param stops
	 *            A list of Stops wrapped in StopAdapters
	 */
	public void addStopsToVertexList(ArrayList<Vertex> vertices,
			ArrayList<StopAdapter> stops) {
		// bail if either vertex or stop lists are empty
		if (vertices.isEmpty() || stops.isEmpty()) {
			return;
		}

		ArrayList<Insertion> toInsert = new ArrayList<>();
		// find points close to edges
		Vertex a, b, c;
		ListIterator li;

		for (int i = 0; i < stops.size(); i++) {
			// System.out.println("Adding stop " + i + "...");
			StopAdapter s = stops.get(i);
			c = new Vertex(s.getStop().get_stop_lat(), s.getStop()
					.get_stop_lon());

			double minDistance = Double.MAX_VALUE;
			Vertex adjustedPosition = null;
			Vertex addAfter = null;

			li = vertices.listIterator();
			b = (Vertex) li.next();

			while (li.hasNext()) {
				a = b;
				b = (Vertex) li.next();
				Vertex testV = getLineSegmentIntersect(a, b, c);
				double dist = c.getRawDistance(testV);
				if (dist < minDistance) {
					minDistance = dist;
					adjustedPosition = testV;
					addAfter = a;
				}
			}

			// insert a new vertex
			// s.setCoordinates(adjustedPosition.get_shape_pt_lat(),
			// adjustedPosition.get_shape_pt_lon());
			adjustedPosition.set_shape_pt_lat(s.getStop().get_stop_lat());
			adjustedPosition.set_shape_pt_lon(s.getStop().get_stop_lon());
			adjustedPosition.setStop(s);
			int insert = vertices.indexOf(addAfter) + 1;
			toInsert.add(new Insertion(insert, adjustedPosition));

		}

		Collections.sort(toInsert, new Comparator<Insertion>() {
			@Override
			public int compare(Insertion a, Insertion b) {
				if (a.getIndex() < b.getIndex()) {
					return -1;
				} else if (a.getIndex() > b.getIndex()) {
					return 1;
				}
				return 0;
			}
		});

		if (!toInsert.isEmpty()) {

			// ArrayList<Vertex> newList = new ArrayList<>();
			int newSize = vertices.size() + toInsert.size();

			// newList.ensureCapacity(newSize);

			/*
			 * int origIndex = 0; int insertIndex = 0; int newIndex = 0; int
			 * nextInsertIndex = toInsert.get(0).getIndex();
			 * 
			 * while (newIndex < newSize) { if (newIndex == nextInsertIndex) {
			 * newList.add(toInsert.get(insertIndex).getVertex());
			 * insertIndex++; if (insertIndex < toInsert.size()) {
			 * nextInsertIndex = toInsert.get(insertIndex).getIndex() +
			 * insertIndex; } else { nextInsertIndex = -1; } } else if
			 * (origIndex < vertices.size()) {
			 * newList.add(vertices.get(origIndex)); origIndex++; } newIndex++;
			 * }
			 */

			int newIndex = 0;
			int origIndex = 0;

			Vertex[] from = vertices.toArray(new Vertex[vertices.size()]);
			Vertex[] to = new Vertex[newSize];

			int stopIndex = 0;
			int nextInsertIndex = toInsert.get(stopIndex).getIndex();

			while (newIndex < newSize) {
				if (newIndex == nextInsertIndex) {
					newIndex++;
					to[nextInsertIndex] = toInsert.get(stopIndex).getVertex();
					stopIndex++;
					if (stopIndex < stops.size()) {
						nextInsertIndex = toInsert.get(stopIndex).getIndex()
								+ stopIndex;
					} else {
						nextInsertIndex = -1;
					}
				} else {
					int length;
					if (nextInsertIndex != -1) {
						length = nextInsertIndex - newIndex;
					} else {
						length = newSize - newIndex;
					}
					if (length > 0) {
						System.arraycopy(from, origIndex, to, newIndex, length);
						origIndex += length;
						newIndex += length;
					}
				}
			}
			vertices.clear();
			vertices.addAll(Arrays.asList(to));
		}
	}

	/**
	 * Add a stop timecode as either the trip's start time or end time
	 * 
	 * @param in
	 *            The timecode
	 */
	public void addStopTime(int in) {
		// only consider non-null and non-zero times
		// System.out.println("Trying to set start time!");
		if (in == 0) {
			return;
		}
		if (mStartTime == 0) {
			// System.out.println("Setting start time!");
			mStartTime = in;
		}
		mEndTime = in;
	}

	/**
	 * Add a Vertex to the Vertex list
	 * 
	 * @param in
	 *            A Vertex
	 */
	public void addVertex(Vertex in) {
		mVertices.add(in);
	}

	@Override
	public int compareTo(Trip o) {
		// compare _trip_id
		int ret = _route_id.compareTo(o.get_route_id());
		if (ret == 0) {
			// compare stopSequence
			ret = _service_id.compareTo(o.get_service_id());
		}
		// if we make it here, then the two are congruent
		return ret;
	}

	/**
	 * Reverse the order of the trip's vertices
	 */
	public void flipVertices() {
		Collections.reverse(mVertices);
	}

	/**
	 * Get the block ID
	 * 
	 * @return Block ID
	 */
	public String get_block_id() {
		return _block_id;
	}

	/**
	 * Get the direction ID
	 * 
	 * @return Direction ID
	 */
	public int get_direction_id() {
		return _direction_id;
	}

	/**
	 * Get the route ID
	 * 
	 * @return Route ID
	 */
	public String get_route_id() {
		return _route_id;
	}

	/**
	 * Get the service ID
	 * 
	 * @return Service ID
	 */
	public String get_service_id() {
		return _service_id;
	}

	/**
	 * Get the shape ID
	 * 
	 * @return Shape ID
	 */
	public String get_shape_id() {
		return _shape_id;
	}

	/**
	 * Get the headsign text
	 * 
	 * @return Headsign text
	 */
	public String get_trip_headsign() {
		return _trip_headsign;
	}

	/**
	 * Get the trip ID
	 * 
	 * @return Trip ID
	 */
	public String get_trip_id() {
		return _trip_id;
	}

	/**
	 * Get the short name
	 * 
	 * @return Short name
	 */
	public String get_trip_short_name() {
		return _trip_short_name;
	}

	/**
	 * Get the trip type
	 * 
	 * @return Trip type
	 */
	public String get_trip_type() {
		return _trip_type;
	}

	/**
	 * Get is is wheelchair accessible
	 * 
	 * @return Is wheelchair accessible
	 */
	public int get_wheelchair_accessible() {
		return _wheelchair_accessible;
	}

	/**
	 * Get end stop ID
	 * 
	 * @return End stop ID
	 */
	public int getEndStopID() {
		return mEndStopID;
	}

	/**
	 * Get end timecode
	 * 
	 * @return End timecode
	 */
	public int getEndTime() {
		return mEndTime;
	}

	/**
	 * Get the GTFS file name associated with this object.
	 * 
	 * @return The GTFS file name associated with this object.
	 */
	@Override
	public String getFilename() {
		return _filename;
	}

	/**
	 * Get first stop in trip
	 * 
	 * @return First stop in trip
	 */
	public StopAdapter getFirstStop() {
		if (mStops.size() > 0) {
			return mStops.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Get this objects unique ID.
	 * 
	 * @return This objects unique ID
	 */
	@Override
	public int getID() {
		return _route_id.hashCode();
	}

	/**
	 * Get last stop in trip
	 * 
	 * @return Last stop in trip
	 */
	public StopAdapter getLastStop() {
		if (mStops.size() > 0) {
			return mStops.get(mStops.size() - 1);
		} else {
			return null;
		}
	}

	/**
	 * Get trip's Route object
	 * 
	 * @return This trip's route
	 */
	public Route getRoute() {
		return mRoute;
	}

	/**
	 * Get starting stop ID
	 * 
	 * @return Starting stop ID
	 */
	public int getStartStopID() {
		return mStartStopID;
	}

	/**
	 * Get starting time
	 * 
	 * @return Start time
	 */
	public int getStartTime() {
		return mStartTime;
	}

	/**
	 * Get list of Stops in trip
	 * 
	 * @return List of stops
	 */
	public ArrayList<StopAdapter> getStops() {
		return mStops;
	}

	/**
	 * Get list of vertices in trip
	 * 
	 * @return List of vertices
	 */
	public ArrayList<Vertex> getVertices() {
		return mVertices;
	}

	/**
	 * Reset all of the internal structures
	 */
	public void resetStructure() {
		mVertices = new ArrayList<>();
		mStops = new ArrayList<>();
		mRoute = null;
		mStartTime = 0;
		mEndTime = 0;
		mStartStopID = -1;
		mEndStopID = -1;
	}

	/**
	 * Set the block ID
	 * 
	 * @param in
	 *            Block ID
	 */
	public void set_block_id(String in) {
		_block_id = in;
	}

	/**
	 * Set the direction ID
	 * 
	 * @param in
	 *            Direction ID
	 */
	public void set_direction_id(int in) {
		_direction_id = in;
	}

	/**
	 * Set the direction ID
	 * 
	 * @param in
	 *            Direction ID
	 */
	public void set_direction_id(String in) {
		try {
			_direction_id = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set the route ID
	 * 
	 * @param in
	 *            Route ID
	 */
	public void set_route_id(String in) {
		_route_id = in;
	}

	/**
	 * Set the service ID
	 * 
	 * @param in
	 *            Service ID
	 */
	public void set_service_id(String in) {
		_service_id = in;
	}

	/**
	 * Set the shape ID
	 * 
	 * @param in
	 *            Shape ID
	 */
	public void set_shape_id(String in) {
		_shape_id = in;
	}

	/**
	 * Set the headsign text
	 * 
	 * @param in
	 *            Headsign text
	 */
	public void set_trip_headsign(String in) {
		_trip_headsign = in;
	}

	/**
	 * Set trip ID
	 * 
	 * @param in
	 *            Trip ID
	 */
	public void set_trip_id(String in) {
		_trip_id = in;
	}

	/**
	 * Set the short name
	 * 
	 * @param in
	 *            Short name
	 */
	public void set_trip_short_name(String in) {
		_trip_short_name = in;
	}

	/**
	 * Set the trip type
	 * 
	 * @param in
	 *            Trip type
	 */
	public void set_trip_type(String in) {
		_trip_type = in;
	}

	/**
	 * Set if wheelchair accessible
	 * 
	 * @param in
	 *            If wheelchair accessible
	 */
	public void set_wheelchair_accessible(int in) {
		_wheelchair_accessible = in;
	}

	/**
	 * Set if wheelchair accessible
	 * 
	 * @param in
	 *            If wheelchair accessible
	 */
	public void set_wheelchair_accessible(String in) {
		try {
			_wheelchair_accessible = Integer.parseInt(in);
		} catch (NumberFormatException e) {
			;
		}
	}

	/**
	 * Set Route object
	 * 
	 * @param in
	 *            Route object
	 */
	public void setRoute(Route in) {
		mRoute = in;
	}

	/**
	 * Set internal Vertex list to given Vertex list
	 * 
	 * @param in
	 *            Vertex list to set to
	 */
	public void setVertexList(ArrayList<Vertex> in) {
		/*
		 * if (Config.get().getBooleanOption(Config.DRONES_ACTIVE)) {
		 * mVertices.clear(); mVertices.addAll(in); } else {
		 */
		mVertices = in;
		/*
		 * }
		 */
	}

	@Override
	public String toString() {
		return _route_id + "," + _service_id + "," + _trip_id + ","
				+ _trip_headsign + "," + _direction_id + "," + _block_id + ","
				+ _shape_id + "\n";
	}
}
