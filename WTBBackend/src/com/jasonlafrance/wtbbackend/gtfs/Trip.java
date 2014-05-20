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
 *
 * @author Jason LaFrance
 */
public final class Trip extends GTFSParser implements Comparable<Trip> {

    private static final String _filename = "//trips.txt";

    private class Insertion {

        private int index;
        private Vertex item;

        public Insertion(int index, Vertex item) {
            this.index = index;
            this.item = item;
        }

        public int getIndex() {
            return index;
        }

        public Vertex getVertex() {
            return item;
        }
    }

    public void addStopsToVertexList(ArrayList<Vertex> vertices, ArrayList<StopAdapter> stops) {
        // bail if either vertex or stop lists are empty
        if (vertices.isEmpty() || stops.isEmpty()) {
            return;
        }

        ArrayList<Insertion> toInsert = new ArrayList<>();
        // find points close to edges
        Vertex a, b, c;
        ListIterator li;

        for (int i = 0; i < stops.size(); i++) {
            //System.out.println("Adding stop " + i + "...");
            StopAdapter s = stops.get(i);
            c = new Vertex(s.getStop().get_stop_lat(), s.getStop().get_stop_lon());

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
            //s.setCoordinates(adjustedPosition.get_shape_pt_lat(), adjustedPosition.get_shape_pt_lon());
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

            //ArrayList<Vertex> newList = new ArrayList<>();
            int newSize = vertices.size() + toInsert.size();

            //newList.ensureCapacity(newSize);

            /*
             int origIndex = 0;
             int insertIndex = 0;
             int newIndex = 0;
             int nextInsertIndex = toInsert.get(0).getIndex();
            
             while (newIndex < newSize) {
             if (newIndex == nextInsertIndex) {
             newList.add(toInsert.get(insertIndex).getVertex());
             insertIndex++;
             if (insertIndex < toInsert.size()) {
             nextInsertIndex = toInsert.get(insertIndex).getIndex() + insertIndex;
             } else {
             nextInsertIndex = -1;
             }
             } else if (origIndex < vertices.size()) {
             newList.add(vertices.get(origIndex));
             origIndex++;
             }
             newIndex++;
             }
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
                        nextInsertIndex = toInsert.get(stopIndex).getIndex() + stopIndex;
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
    
    public void resetStructure(){
        mVertices = new ArrayList<>();
        mStops = new ArrayList<>();
        mRoute = null;
        mStartTime = 0;
        mEndTime = 0;
        mStartStopID = -1;
        mEndStopID = -1;
    }

    public Trip(
            String inRouteID,
            String inServiceID,
            String inTripID,
            String inHeadsign,
            int inDirection,
            String inBlockID,
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

    public Trip(String inLine) {
    }

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

    public void set_route_id(String in) {
        _route_id = in;
    }

    public void set_service_id(String in) {
        _service_id = in;
    }

    public void set_trip_id(String in) {
        _trip_id = in;
    }

    public void set_trip_headsign(String in) {
        _trip_headsign = in;
    }

    public void set_trip_short_name(String in) {
        _trip_short_name = in;
    }

    public void set_direction_id(int in) {
        _direction_id = in;
    }

    public void set_direction_id(String in) {
        try {
            _direction_id = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_block_id(String in) {
        _block_id = in;
    }

    public void set_shape_id(String in) {
        _shape_id = in;
    }

    public void set_wheelchair_accessible(int in) {
        _wheelchair_accessible = in;
    }

    public void set_trip_type(String in) {
        _trip_type = in;
    }

    public void set_wheelchair_accessible(String in) {
        try {
            _wheelchair_accessible = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void addVertex(Vertex in) {
        mVertices.add(in);
    }

    public void setVertexList(ArrayList<Vertex> in) {
/*
        if (Config.get().getBooleanOption(Config.DRONES_ACTIVE)) {
            mVertices.clear();
            mVertices.addAll(in);
        } else {
    */
            mVertices = in;
      /*  }
    */
    }

    public void addStop(StopAdapter in) {
        if (mStartStopID == -1) {
            mStartStopID = in.getStop().getID();
        }
        mEndStopID = in.getStop().getID();
        mStops.add(in);
    }

    public void setRoute(Route in) {
        mRoute = in;
    }

    public void flipVertices() {
        Collections.reverse(mVertices);
    }

    public void addStopTime(int in) {
        // only consider non-null and non-zero times
        //System.out.println("Trying to set start time!");
        if (in == 0) {
            return;
        }
        if (mStartTime == 0) {
            //System.out.println("Setting start time!");
            mStartTime = in;
        }
        mEndTime = in;
    }

    public String get_route_id() {
        return _route_id;
    }

    @Override
    public int getID() {
        return _route_id.hashCode();
    }

    public String get_service_id() {
        return _service_id;
    }

    public String get_trip_id() {
        return _trip_id;
    }

    public String get_trip_headsign() {
        return _trip_headsign;
    }

    public String get_trip_short_name() {
        return _trip_short_name;
    }

    public int get_direction_id() {
        return _direction_id;
    }

    public String get_block_id() {
        return _block_id;
    }

    public String get_shape_id() {
        return _shape_id;
    }

    public int get_wheelchair_accessible() {
        return _wheelchair_accessible;
    }

    public String get_trip_type() {
        return _trip_type;
    }

    public ArrayList<Vertex> getVertices() {
        return mVertices;
    }

    public ArrayList<StopAdapter> getStops() {
        return mStops;
    }

    public Route getRoute() {
        return mRoute;
    }

    public int getStartTime() {
        return mStartTime;
    }

    public int getEndTime() {
        return mEndTime;
    }

    public int getStartStopID() {
        return mStartStopID;
    }

    public int getEndStopID() {
        return mEndStopID;
    }

    public StopAdapter getFirstStop() {
        if (mStops.size() > 0) {
            return mStops.get(0);
        } else {
            return null;
        }
    }

    public StopAdapter getLastStop() {
        if (mStops.size() > 0) {
            return mStops.get(mStops.size() - 1);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return _route_id + ","
                + _service_id + ","
                + _trip_id + ","
                + _trip_headsign + ","
                + _direction_id + ","
                + _block_id + ","
                + _shape_id + "\n";
    }

    public void addStopsToVertexList() {
        addStopsToVertexList(mVertices, mStops);
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

    @Override
    public String getFilename() {
        return _filename;
    }
}
