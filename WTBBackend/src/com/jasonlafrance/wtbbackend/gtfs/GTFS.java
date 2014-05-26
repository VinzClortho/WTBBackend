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

import static com.jasonlafrance.wtbbackend.wtb_util.TimeUtil.minutesToTime;
import static com.jasonlafrance.wtbbackend.wtb_util.TimeUtil.timeToMinutes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jasonlafrance.wtbbackend.config.Config;
import com.jasonlafrance.wtbbackend.vehicle.Vehicle;

/**
 * The main GTFS data table and structure class
 * 
 * @author Jason LaFrance
 */
public final class GTFS {

	private static final int WINDOW_MARGIN = Config.getInstance().getIntOption(
			Config.STOP_WINDOW_MARGIN);

	private static ArrayList<ArrayList<Vertex>> sVertexSnapshot;
	private static ArrayList<ArrayList<Stop>> sStopSnapshot;

	private static int _nextID = 0;
	private static final HashMap<Integer, GTFS> sActiveGTFS = new HashMap<>();
	private static final HashMap<Integer, ArrayList<ArrayList<StopAdapter>>> sStopsMap = new HashMap<>();
	private static final StackedStopList sMasterSSL = StackedStopList
			.getInstance();

	private final static String _gtfsClasses[] = new String[] {
			Agency.class.getName(), Calendar.class.getName(),
			CalendarDate.class.getName(), FareAttribute.class.getName(),
			FareRule.class.getName(), Route.class.getName(),
			Stop.class.getName(), StopTime.class.getName(),
			Transfer.class.getName(), Trip.class.getName(),
			Vertex.class.getName() };

	/**
	 * Find the closest stop within the current time frame to a vehicle
	 * 
	 * @param inVehicle
	 *            The vehicle to find the closest stop to
	 * @return Closest Stop to given Vehicle
	 */
	public static Stop closestStop(Vehicle inVehicle) {
		Stop closest = null;
		// TODO
		return closest;
	}

	/**
	 * Calculate the current possible stops based on the current time, a
	 * configured stop time range, and GTFS calendar data
	 * 
	 * @return A list of Stops wrapped in StopAdapters
	 */
	public static ArrayList<StopAdapter> getCurrentStopWindow() {
		ArrayList<StopAdapter> allStops = new ArrayList<>();
		synchronized (sStopsMap) {
			for (int id = 0; id < _nextID; id++) {
				ArrayList<ArrayList<StopAdapter>> sList = sStopsMap.get(id);
				if (sList != null) {
					ListIterator outer = sList.listIterator();
					while (outer.hasNext()) {
						ArrayList<Stop> sFragment = (ArrayList<Stop>) outer
								.next();
						ListIterator inner = sFragment.listIterator();
						while (inner.hasNext()) {
							StopAdapter thisStop = (StopAdapter) inner.next();
							allStops.add(thisStop);
						}
					}
				}
			}
		}

		return allStops;
	}

	/**
	 * Get a GTFS object from a given index
	 * 
	 * @param id
	 *            The index of the GTFS object
	 * @return The GTFS object, or null if doesn't exist
	 */
	public static GTFS getGTFS(int id) {
		return sActiveGTFS.get(id);
	}

	/**
	 * Get the list of current stops
	 * 
	 * @return A StackedStopList of the current Stops
	 */
	public static StackedStopList getMasterStackedStopList() {
		return sMasterSSL;
	}

	/**
	 * Update the current StackedStopsList based on current time and GTFS data
	 */
	private static void updateMasterStackedStopList() {
		StackedStopList ssl = StackedStopList.getInstance();

		synchronized (sStopsMap) {
			ArrayList<Integer> ids = new ArrayList<>(sStopsMap.keySet());

			for (int idKey : ids) {
				ArrayList<ArrayList<StopAdapter>> nowStops = sStopsMap
						.get(idKey);
				if (nowStops != null) {
					for (int o = 0; o < nowStops.size(); o++) {
						ArrayList<StopAdapter> list = nowStops.get(o);
						for (int i = 0; i < list.size(); i++) {
							ssl.addStop(list.get(i));
						}
					}
				}
			}
		}

		synchronized (sMasterSSL) {
			sMasterSSL.set(ssl);
			ssl.recycle();
		}
	}

	/*
	 * public static Agency getAgency(String key){ for(int i = 0; i <
	 * mAgencies.length; i++){ if(mAgencies[i].get_agency_id().equals(key)){
	 * return mAgencies[i]; } } return null; }
	 */

	/**
	 * Add a list of Stops wrapped in StopAdapters to the current master stop
	 * list for a GTFS object matching the given ID
	 * 
	 * @param inStops
	 *            A list of Stops wrapped in StopAdapters
	 * @param inID
	 *            The ID of the GTFS object
	 */
	private static void updateMasterStopWindow(
			ArrayList<ArrayList<StopAdapter>> inStops, int inID) {
		synchronized (sStopsMap) {
			sStopsMap.put(inID, inStops);
		}
		updateMasterStackedStopList();
	}

	private final int _id;
	private final String _Dir;

	private final HashMap<String, Trip> _tripMap;
	private final HashMap<String, Route> _routeMap;
	private final HashMap<String, Stop> _stopMap;
	private boolean isSorted = false;

	private ArrayList<Agency> mAgencies = new ArrayList<>();
	private ArrayList<Calendar> mCalendar = new ArrayList<>();
	private ArrayList<CalendarDate> mCalendarDates = new ArrayList<>();
	private ArrayList<FareAttribute> mFareAttributes = new ArrayList<>();
	private ArrayList<FareRule> mFareRules = new ArrayList<>();
	private ArrayList<Route> mRoutes = new ArrayList<>();
	private ArrayList<Stop> mStops = new ArrayList<>();
	private ArrayList<StopTime> mStopTimes = new ArrayList<>();
	private ArrayList<Transfer> mTransfers = new ArrayList<>();
	private ArrayList<Trip> mTrips = new ArrayList<>();
	private ArrayList<Vertex> mVertices = new ArrayList<>();

	private final HashMap<String, ArrayList<? extends GTFSParser>> _classListMap = new HashMap<>();
	// private final HashMap<String, Object> _classListMap = new HashMap<>();

	private final SimpleDateFormat _dateFormatter = new SimpleDateFormat(
			"yyyyMMdd");
	private Calendar.Weekdays _inDay;

	private final HashSet<String> _validServices = new HashSet<>();

	private final double _threshold = 0.00025; // ?
	private final double _thresholdMeters = Config.getInstance()
			.getDoubleOption(Config.CLOSENESS_THRESHOLD);
	// this is the compiled master path list!

	private ArrayList<RoutePath> _paths;

	private Double _minLat = null, _maxLat = null;
	private Double _minLon = null, _maxLon = null;
	private final int _maxTimeGap;

	/**
	 * Create a GTFS object with data tables stored in a given directory
	 * 
	 * @param inDir
	 *            The path to the GTFS tables
	 * @param inTimeGap
	 *            Time gap
	 * @throws Exception
	 *             If any critical load errors occur
	 */
	public GTFS(String inDir, int inTimeGap) throws Exception {

		_Dir = inDir;

		_id = _nextID;
		_nextID++;

		_maxTimeGap = inTimeGap;
		_paths = new ArrayList<>();

		_classListMap.put(Agency.class.getName(), mAgencies);
		_classListMap.put(Calendar.class.getName(), mCalendar);
		_classListMap.put(CalendarDate.class.getName(), mCalendarDates);
		_classListMap.put(FareAttribute.class.getName(), mFareAttributes);
		_classListMap.put(FareRule.class.getName(), mFareRules);
		_classListMap.put(Route.class.getName(), mRoutes);
		_classListMap.put(Stop.class.getName(), mStops);
		_classListMap.put(StopTime.class.getName(), mStopTimes);
		_classListMap.put(Transfer.class.getName(), mTransfers);
		_classListMap.put(Trip.class.getName(), mTrips);
		_classListMap.put(Vertex.class.getName(), mVertices);

		// generate and initialize all of the aggregate objects
		// with abstract building via reflection API
		for (String table : _gtfsClasses) {

			String filename = GTFSParser.getFilename(table);
			FileInputStream fin = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			// load in the table
			try {

				fin = new FileInputStream(inDir + filename);
				isr = new InputStreamReader(fin, "UTF-8");
				br = new BufferedReader(isr);

				Constructor<?> cons = GTFSParser.getConstructor(table, true);
				String line = br.readLine();

				if (line != null) {
					// grab header line
					GTFSParser.setHeader(_id, filename, line.trim());

					line = br.readLine();
					while (line != null && line.trim().length() > 0) {
						// instantiate a new object of the iterated class type
						GTFSParser e = (GTFSParser) cons.newInstance("");
						e.parse(_id, line);

						if (e.getID() != 0) {
							// if it's valid, do a little recasting dance to get
							// the object into its respective list
							Object o = _classListMap
									.get(e.getClass().getName());
							if (o != null) {
								ArrayList<GTFSParser> p = (ArrayList<GTFSParser>) o;
								p.add(e);
							}
						}
						line = br.readLine();
					}
				}
				System.out.println(table + ": "
						+ _classListMap.get(table).size());

			} catch (FileNotFoundException e) {
			} finally {
				if (br != null) {
					br.close();
				}
				if (isr != null) {
					isr.close();
				}
				if (fin != null) {
					fin.close();
				}
			}

		}

		// check table requirements!
		boolean ok = true;
		String missingError = "\n";
		if (mCalendar.isEmpty() && mCalendarDates.isEmpty()) {
			missingError += "Missing calendar.txt and calendar_dates.txt files!\n";
			ok = false;
		}
		if (mStopTimes.isEmpty()) {
			missingError += "Missing stop_times.txt file!\n";
			ok = false;
		}
		if (mStops.isEmpty()) {
			missingError += "Missing stops.txt file!\n";
			ok = false;
		}
		if (mTrips.isEmpty()) {
			missingError += "Missing trips.txt file!\n";
			ok = false;
		}
		if (mVertices.isEmpty()) {
			missingError += "Missing shapes.txt file!\n";
			ok = false;
		}
		if (mRoutes.isEmpty()) {
			missingError += "Missing routes.txt file!\n";
			ok = false;
		}

		if (!ok) {
			throw new Exception(missingError);
		}

		for (String table : _gtfsClasses) {
			Object o = _classListMap.get(table);
			if (o != null) {
				ArrayList<GTFSParser> p = (ArrayList<GTFSParser>) o;
				p.trimToSize();
			}
		}

		_tripMap = new HashMap<>();
		_routeMap = new HashMap<>();
		_stopMap = new HashMap<>();

		for (Trip e : mTrips) {
			_tripMap.put(e.get_trip_id(), e);
		}
		for (int i = 0; i < mRoutes.size(); i++) {
			Route e = mRoutes.get(i);

			Agency agency = null;

			if (mAgencies.size() == 1) {
				agency = mAgencies.get(0);
			} else if (mAgencies.size() > 0) {

				for (int a = 0; a < mAgencies.size(); a++) {
					if (mAgencies.get(a).get_agency_id()
							.equals(e.get_agency_id())) {
						agency = mAgencies.get(a);
						break;
					}
				}
			}

			e.setAgency(agency);

			_routeMap.put(e.get_route_id(), e);
		}
		for (Stop e : mStops) {
			_stopMap.put(e.get_stop_id(), e);
		}

		resetDate();

		// add this GTFS to the active map
		sActiveGTFS.put(_id, this);

		// start the window timer
		Timer windowTimer = new Timer();

		windowTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateStopWindow();
			}
		}, 1000, 60 * 1000); // run every minute

	}

	/**
	 * Add all stops to their respective trips
	 */
	private void addStopsToTrips() {
		for (int s = 0; s < mStopTimes.size(); s++) {
			StopTime st = mStopTimes.get(s);
			if (_validServices.contains(_tripMap.get(st.get_trip_id())
					.get_service_id())) {

				// clone the stop...
				StopAdapter stop = new StopAdapter(_stopMap.get(st
						.get_stop_id()));

				// and add stop time to new stop
				stop.setStopTime(st);
				stop.setRoute(_tripMap.get(st.get_trip_id()).getRoute());

				_tripMap.get(st.get_trip_id()).addStop(stop);
				_tripMap.get(st.get_trip_id()).addStopTime(
						st.getArrivalTimecode());
			}

			// insert all stops also as vertices into their trip's vertex list
			/*
			 * if (Config.get().getBooleanOption(Config.DRONES_ACTIVE)) { for
			 * (int t = 0; t < mTrips.length; t++) {
			 * mTrips[t].addStopsToVertexList(); } }
			 */
		}

	}

	/**
	 * Attach all trips into continuous route paths where possible
	 * 
	 * @param paths
	 *            A nested list of Trips
	 * @param timeThreshold
	 *            Maximum time threshold difference between start and stop times
	 *            between two trip sections that determin wether or not they can
	 *            be joined together
	 * @param matchStops
	 *            Only attach trip segments with identical star/stop stop IDs or
	 *            not. Some physical stops may have multiple instances with
	 *            different IDs.
	 * @return A list of RoutePaths
	 */
	private ArrayList<RoutePath> attachTrips(
			LinkedList<LinkedList<Trip>> paths, int timeThreshold,
			boolean matchStops) {
		final ArrayList<LinkedList<Trip>> newPaths = new ArrayList<>();
		final ArrayList<LinkedList<Trip>> donePaths = new ArrayList<>();

		double rawThresh = _thresholdMeters * _thresholdMeters;

		ArrayList<RoutePath> outPaths = new ArrayList<>();

		HashSet<String> startTimes = new HashSet<>();
		HashSet<String> endTimes = new HashSet<>();

		LinkedList<Trip> piece;
		boolean foundMatch;

		// parse paths list intil we don't find a match
		do {
			foundMatch = false;
			newPaths.clear();

			// loop until first list is empty;
			while (!paths.isEmpty()) {
				// grab the first one
				piece = paths.pop();
				while (piece.getFirst().getVertices().isEmpty()
						&& !paths.isEmpty()) {
					piece = paths.pop();
				}
				boolean addedTo = false;
				ListIterator li = paths.listIterator();
				while (!paths.isEmpty() && li.hasNext()) {
					LinkedList<Trip> t = (LinkedList<Trip>) li.next();
					if (t.getFirst().getVertices().isEmpty()) {
						continue;
					}
					// check if t fits before piece
					boolean leftStop = true, rightStop = true;

					if (matchStops) {
						leftStop = t.getLast().getEndStopID() == piece
								.getFirst().getStartStopID()
								|| t.getLast()
										.getVertices()
										.get(t.getLast().getVertices().size() - 1)
										.getRawDistanceInMeters(
												piece.getFirst().getVertices()
														.get(0)) <= rawThresh;
						rightStop = t.getFirst().getStartStopID() == piece
								.getLast().getEndStopID()
								|| t.getFirst()
										.getVertices()
										.get(0)
										.getRawDistanceInMeters(
												piece.getLast()
														.getVertices()
														.get(piece.getLast()
																.getVertices()
																.size() - 1)) <= rawThresh;
					}

					if (leftStop) {
						int time = piece.getFirst().getStartTime()
								- t.getLast().getEndTime();
						leftStop = time <= _threshold && time >= 0;
					}

					if (rightStop) {
						int time = t.getFirst().getStartTime()
								- piece.getLast().getEndTime();
						rightStop = time <= _threshold && time >= 0;
					}

					if (leftStop
							&& (t.getLast().get_service_id() == null ? piece
									.getFirst().get_service_id() == null : t
									.getLast().get_service_id()
									.equals(piece.getFirst().get_service_id()))
							&& (t.getLast().get_route_id() == null ? piece
									.getFirst().get_route_id() == null : t
									.getLast().get_route_id()
									.equals(piece.getFirst().get_route_id()))) {
						piece.addAll(0, t);
						addedTo = true;
						li.remove();
						foundMatch = true;
						// or check of t fits after piece
					} else if (rightStop
							&& (t.getFirst().get_service_id() == null ? piece
									.getLast().get_service_id() == null : t
									.getFirst().get_service_id()
									.equals(piece.getLast().get_service_id()))
							&& (t.getFirst().get_route_id() == null ? piece
									.getLast().get_route_id() == null : t
									.getFirst().get_route_id()
									.equals(piece.getLast().get_route_id()))) {
						piece.addAll(t);
						addedTo = true;
						li.remove();
						foundMatch = true;
					}
				}
				if (addedTo) {
					newPaths.add(piece);
				} else {
					donePaths.add(piece);
				}
			}
			paths.addAll(newPaths);

		} while (foundMatch);

		paths.addAll(donePaths);
		ListIterator li = paths.listIterator();
		while (li.hasNext()) {
			ArrayList<Trip> tl = new ArrayList<Trip>(
					(LinkedList<Trip>) li.next());
			outPaths.add(new RoutePath(tl, tl.get(0).getRoute()));
		}

		return outPaths;
	}

	/**
	 * Initialize and build the RoutePaths from current Trips
	 */
	private void buildRoutePaths() {
		LinkedList<LinkedList<Trip>> paths = new LinkedList<>();
		// initialize the list
		for (int t = 0; t < mTrips.size(); t++) {
			if (_validServices.contains(mTrips.get(t).get_service_id())) {
				LinkedList<Trip> nt = new LinkedList<>();
				nt.add(mTrips.get(t));
				paths.add(nt);
			}
		}

		// sort the trips list!
		Collections.sort(paths, new Comparator<LinkedList<Trip>>() {
			@Override
			public int compare(LinkedList<Trip> t1, LinkedList<Trip> t2) {
				int timeDiff = t1.getFirst().getStartTime()
						- t2.getFirst().getStartTime();
				if (timeDiff < 0) {
					return -1;
				} else if (timeDiff > 0) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		// link exact matches
		System.out.println("Starting attachTrips...");
		_paths = attachTrips(paths, 0, true);
		System.out.println("Done attachTrips...");

		// link matches within treshold
		// if(maxTimeGap > 0) attachTrips(paths, maxTimeGap, false);
		// print out the path info
		// dump KML file for each path
		/*
		 * int i = 1; for (LinkedList<Trip> e : paths) { if (e != null) { String
		 * name = e.get(0).getRoute().get_route_short_name(); name =
		 * name.replace('/', '-'); KMLUtil.KMLExport.writeKML(name + "_" + i +
		 * ".kml", e); } i++; }
		 */
	}

	/**
	 * Calculate latitude and longitude extreme values
	 */
	private void calcExtremes() {
		_minLat = Double.MAX_VALUE;
		_minLon = Double.MAX_VALUE;
		_maxLat = -Double.MAX_VALUE;
		_maxLon = -Double.MAX_VALUE;

		for (int i = 0; i < mVertices.size(); i++) {
			Vertex e = mVertices.get(i);
			if (e.get_shape_pt_lat() < _minLat) {
				_minLat = e.get_shape_pt_lat();
			}
			if (e.get_shape_pt_lat() > _maxLat) {
				_maxLat = e.get_shape_pt_lat();
			}
			if (e.get_shape_pt_lon() < _minLon) {
				_minLon = e.get_shape_pt_lon();
			}
			if (e.get_shape_pt_lon() > _maxLon) {
				_maxLon = e.get_shape_pt_lon();
			}
		}
	}

	/**
	 * Find all Stops with a time frame
	 * 
	 * @param inStart
	 *            Start time
	 * @param inEnd
	 *            End time
	 * @return A nested list of Stops wrapped in StopAdapters
	 */
	public ArrayList<ArrayList<StopAdapter>> getAllStopsInTimeframe(
			String inStart, String inEnd) {
		ArrayList<ArrayList<StopAdapter>> output = new ArrayList<>();

		for (Route r : mRoutes) {
			// System.out.println(r.getName());
			ArrayList<ArrayList<StopAdapter>> list = getStopsInTimeframe(
					r.getName(), inStart, inEnd);
			output.addAll(list);
		}

		return output;
	}

	/**
	 * Find all vertices in a given time frame
	 * 
	 * @param inStart
	 *            Start time
	 * @param inEnd
	 *            End time
	 * @return A nested list of all vertices in the given time frame
	 */
	public ArrayList<ArrayList<Vertex>> getAllVerticesInTimeframe(
			String inStart, String inEnd) {
		ArrayList<ArrayList<Vertex>> output = new ArrayList<>();

		for (Route r : mRoutes) {
			ArrayList<ArrayList<Vertex>> list = getVerticesInTimeframe(
					r.getName(), inStart, inEnd);
			output.addAll(list);
		}

		return output;
	}

	/**
	 * Get current day of the week
	 * 
	 * @return A String representation of the day of the week
	 */
	public String getDayOfWeek() {
		return _inDay.toString();
	}

	/**
	 * Get the path to the GTFS directory
	 * 
	 * @return The GTFS directory
	 */
	public String getDir() {
		return _Dir;
	}

	/**
	 * Get this GTFS object's ID
	 * 
	 * @return The GTFS ID
	 */
	public int getID() {
		return _id;
	}

	/**
	 * Get maximum latitude
	 * 
	 * @return Maximum latitude
	 */
	public double getMaxLat() {
		if (_maxLat == null)
			calcExtremes();
		return _maxLat;
	}

	/**
	 * Get maximum longitude
	 * 
	 * @return Maximum longitude
	 */
	public double getMaxLon() {
		if (_maxLon == null)
			calcExtremes();
		return _maxLon;
	}

	/**
	 * Get minimum latitude
	 * 
	 * @return Minimum latitude
	 */
	public double getMinLat() {
		if (_minLat == null)
			calcExtremes();
		return _minLat;
	}

	/**
	 * Get minimum longitude
	 * 
	 * @return Minimum longitude
	 */
	public double getMinLon() {
		if (_minLon == null)
			calcExtremes();
		return _minLon;
	}

	/**
	 * Get the current RoutePaths
	 * 
	 * @return A list of RoutePaths
	 */
	public ArrayList<RoutePath> getPaths() {
		return _paths;
	}

	/**
	 * Get the current routes
	 * 
	 * @return List of Routes
	 */
	public ArrayList<Route> getRoutes() {
		return mRoutes;
	}

	/**
	 * Get Vertex list for a specific route ID
	 * 
	 * @param inRouteID
	 *            Route ID to match
	 * @return A list of vertices
	 */
	public ArrayList<Vertex> getRouteVertices(int inRouteID) {
		ArrayList<Vertex> list = new ArrayList<>();

		for (int r = 0; r < mRoutes.size(); r++) {
			if (mRoutes.get(r).getID() == inRouteID) {
				list = mRoutes.get(r).getVertices();
			}
		}

		return list;
	}

	/**
	 * Get stops in a specific trip
	 * 
	 * @param tripID
	 *            The trip ID to match
	 * @return A list of Stops wrapped in StopAdapters
	 */
	public ArrayList<StopAdapter> getStops(int tripID) {
		return _tripMap.get(tripID).getStops();
	}

	/**
	 * Get all stops within a certain time frame for a specific route
	 * 
	 * @param inRoute
	 *            Route ID to match
	 * @param inStart
	 *            Start time code
	 * @param inEnd
	 *            End time code
	 * @return A nested list of Stops wrapped in StopAdapters
	 */
	public ArrayList<ArrayList<StopAdapter>> getStopsInTimeframe(
			String inRoute, String inStart, String inEnd) {
		ArrayList<ArrayList<Trip>> foundPoints = new ArrayList<>();
		ArrayList<ArrayList<StopAdapter>> output = new ArrayList<>();

		// convert time strings to ints
		final int iStart = timeToMinutes(inStart);
		final int iEnd = timeToMinutes(inEnd);

		for (int i = 0; i < _paths.size(); i++) {
			RoutePath checkPath = _paths.get(i);
			// found a matching route
			// now find its relative points
			if (checkPath.getRouteName() != null
					&& checkPath.getRouteName().equals(inRoute)
					&& _validServices.contains(checkPath.getServiceID())) {
				// check if there are points within the time frame

				int cStart = checkPath.getStartTimecode();
				int cEnd = checkPath.getEndTimecode();

				// if start is within time range
				if (cStart <= iEnd && cEnd >= iStart) {
					ArrayList<StopAdapter> sPath = new ArrayList<>();

					// move to starting trip/vertex
					boolean inRange = false;
					boolean done = false;

					ListIterator ti = checkPath.getPath().listIterator();

					while (ti.hasNext() && !done) {
						Trip checkTrip = (Trip) ti.next();
						ListIterator si = checkTrip.getStops().listIterator();
						while (si.hasNext() && !done) {
							StopAdapter s = (StopAdapter) si.next();

							int sTime = s.getStopTime().getArrivalTimecode();
							// System.out.println(v.getStop().getStopTime().get_arrival_time()
							// + " -> " +
							// v.getStop().getStopTime().getArrivalTimecode());
							if (!inRange && sTime >= iStart) {
								// back track
								si.previous();
								while (si.hasPrevious()) {
									StopAdapter sc = (StopAdapter) si
											.previous();
									if (sc.getStopTime() != null) {
										break;
									}
								}

								inRange = true;
							} else if (inRange && sTime != 0 && sTime > iEnd) {
								inRange = false;
								done = true;
							}
							if (inRange || done) {
								sPath.add(s);
							}
						}
					}
					if (!sPath.isEmpty()) {
						output.add(sPath);
					}
				}
			}
		}
		return output;
	}

	/**
	 * Get a specific Trip from the master list of Trips
	 * 
	 * @param in
	 *            The ID of the Trip
	 * @return The specified Trip, or null if doesn't exist
	 */
	public Trip getTrip(int in) {
		if (in > -1 && in < mTrips.size()) {
			return _tripMap.get(in);
		} else {
			return null;
		}
	}

	/**
	 * Get the maximum trip ID
	 * 
	 * @return Maximum trip ID
	 */
	public String getTripIDMax() {
		return mTrips.get(mTrips.size() - 1).get_trip_id();
	}

	/**
	 * Get the minimum trip ID
	 * 
	 * @return Minimum trip ID
	 */
	public String getTripIDMin() {
		return mTrips.get(0).get_trip_id();
	}

	/**
	 * Get a list of all of the Trips
	 * 
	 * @return List of trips
	 */
	public ArrayList<Trip> getTrips() {
		return mTrips;
	}

	/**
	 * Get a list of all of the vertices from a given trip ID
	 * 
	 * @param inTripID
	 *            The trip ID to match
	 * @return A list of vertices
	 */
	public ArrayList<Vertex> getTripVertices(String inTripID) {
		for (int t = 0; t < mTrips.size(); t++) {
			if (mTrips.get(t).get_trip_id().compareTo(inTripID) == 0) {
				return mTrips.get(t).getVertices();
			}
		}

		return null;
	}

	/*
	 * public ArrayList<RouteEdges> getAllRouteEdgesInTimeframe(String inStart,
	 * String inEnd) { ArrayList<RouteEdges> output = new ArrayList<>();
	 * 
	 * for (Route r : mRoutes) { ArrayList<ArrayList<Vertex>> list =
	 * getVerticesInTimeframe(r.getName(), inStart, inEnd); output.addAll(list);
	 * }
	 * 
	 * return output; }
	 */

	/**
	 * Generate vertex list for given route within time range
	 * 
	 * @param inRoute
	 *            String of routes short name
	 * @param inStart
	 *            String of start time in the format HH:MM:SS
	 * @param inEnd
	 *            String of end time in the format HH:MM:SS
	 * @return A nested ArrayList of Vertex describing the path in range
	 */
	public ArrayList<ArrayList<Vertex>> getVerticesInTimeframe(String inRoute,
			String inStart, String inEnd) {
		ArrayList<ArrayList<Trip>> foundPoints = new ArrayList<>();
		ArrayList<ArrayList<Vertex>> output = new ArrayList<>();

		// convert time strings to ints
		final int iStart = timeToMinutes(inStart);
		final int iEnd = timeToMinutes(inEnd);

		for (int i = 0; i < _paths.size(); i++) {
			RoutePath checkPath = _paths.get(i);
			// found a matching route
			// now find its relative points
			if (checkPath.getRouteName().equals(inRoute)
					&& _validServices.contains(checkPath.getServiceID())) {
				// check if there are points within the time frame

				int cStart = checkPath.getStartTimecode();
				int cEnd = checkPath.getEndTimecode();

				// if start is within time range
				if (cStart <= iEnd && cEnd >= iStart) {
					ArrayList<Vertex> vPath = new ArrayList<>();

					// move to starting trip/vertex
					boolean inRange = false;
					boolean done = false;

					ListIterator ti = checkPath.getPath().listIterator();

					while (ti.hasNext() && !done) {
						Trip checkTrip = (Trip) ti.next();
						ListIterator vi = checkTrip.getVertices()
								.listIterator();
						while (vi.hasNext() && !done) {
							Vertex v = (Vertex) vi.next();
							if (v.isStop()) {
								int vTime = v.getStop().getStopTime()
										.getArrivalTimecode();
								// System.out.println(v.getStop().getStopTime().get_arrival_time()
								// + " -> " +
								// v.getStop().getStopTime().getArrivalTimecode());
								if (!inRange && vTime >= iStart) {
									inRange = true;
								} else if (inRange && vTime != 0
										&& vTime > iEnd) {
									inRange = false;
									done = true;
								}
							}
							if (inRange) {
								vPath.add(v);
							}
						}
					}
					if (!vPath.isEmpty()) {
						output.add(vPath);
					}
				}
			}
		}
		return output;
	}

	/**
	 * Get if a service ID is valid right now
	 * 
	 * @param inServiceID
	 *            The service ID to check
	 * @return True if the service is currently valid, otherwise false
	 */
	public boolean isValidService(String inServiceID) {
		return _validServices.contains(inServiceID);
	}

	/**
	 * After all of the raw GTFS data is initialized, this method orchestrates
	 * the creation of the extra internal referencing and association data
	 * structures.
	 */
	private void linkStructure() {
		System.out.println("Starting linkVerticesToTrips...");
		linkVerticesToTrips();
		Collections.sort(mStopTimes);
		System.out.println("Starting addStopsToTrips...");
		addStopsToTrips();
		System.out.println("Done addStopsToTrips.");

		// release the original stops...
		mStops = null;
		System.gc();

		/*
		 * // check for current route_paths.txt file FileInputStream fin = null;
		 * InputStreamReader isr = null; BufferedReader br = null; // load in
		 * the table boolean routePathExists = false; try { fin = new
		 * FileInputStream(_Dir + "\\route_path.txt"); routePathExists = true; }
		 * catch (FileNotFoundException ex) {
		 * //Logger.getLogger(GTFS.class.getName()).log(Level.SEVERE, null, ex);
		 * } // read file if (routePathExists) { _paths = new ArrayList<>();
		 * System.out.println("route_path.txt found.  Using..."); try { isr =
		 * new InputStreamReader(fin, "UTF-8"); br = new BufferedReader(isr);
		 * 
		 * String line = br.readLine();
		 * 
		 * while (line != null) { RoutePath rp = new RoutePath(line, _tripMap);
		 * if (_validServices.contains(rp.getServiceID())) { _paths.add(rp); }
		 * line = br.readLine();
		 * 
		 * } } catch (UnsupportedEncodingException ex) {
		 * Logger.getLogger(GTFS.class .getName()).log(Level.SEVERE, null, ex);
		 * } catch (IOException ex) { Logger.getLogger(GTFS.class
		 * .getName()).log(Level.SEVERE, null, ex); } } else {
		 */
		// doesn't exist, so generate...
		// System.out.println("route_path.txt not found.  Generating...");
		System.out.println("Starting buildRoutePaths...");
		buildRoutePaths();

		// System.out.println("Starting removeDuplicateRoutePaths...");
		// removeDuplicateRoutePaths();
		// System.out.println("Done removeDuplicateRoutePaths.");
		// write the file

		/*
		 * BufferedWriter writer = null; try { writer = new BufferedWriter(new
		 * FileWriter(_Dir + "\\route_path.txt")); for (RoutePath rp : _paths) {
		 * writer.write(rp.getCSVTripList()); } } catch (IOException e) { }
		 * finally { try { if (writer != null) { writer.close(); } } catch
		 * (IOException e) { } } //}
		 */
		showRoutePaths();

	}

	/*
	 * Links all of the vertices to their respective trips.
	 */
	private void linkVerticesToTrips() {
		// sort the vertices by shape_id and sequence!!
		if (!isSorted) {
			Collections.sort(mVertices, new Comparator<Vertex>() {
				public int compare(Vertex a, Vertex b) {
					int ret = a.get_shape_id().compareToIgnoreCase(
							b.get_shape_id());
					if (ret == 0) {
						if (a.get_shape_pt_sequence() < b
								.get_shape_pt_sequence()) {
							ret = -1;
						} else if (a.get_shape_pt_sequence() > b
								.get_shape_pt_sequence()) {
							ret = 1;
						}
					}
					return ret;
				}
			});

			// sort trips by shape_id!!
			Collections.sort(mTrips, new Comparator<Trip>() {
				@Override
				public int compare(Trip a, Trip b) {
					if (a.get_shape_id() == null && b.get_shape_id() != null) {
						return 1;
					} else if (b.get_shape_id() == null
							&& a.get_shape_id() != null) {
						return -1;
					} else if (a.get_shape_id() == null
							&& a.get_shape_id() == null) {
						return 0;
					}
					return a.get_shape_id().compareToIgnoreCase(
							b.get_shape_id());
				}
			});
			isSorted = true;
		}

		ArrayList<Vertex> shape;
		HashMap<String, ArrayList<Vertex>> shapeMap = new HashMap<>();

		for (int t = 0; t < mTrips.size(); t++) {
			Trip thisTrip = mTrips.get(t);

			thisTrip.resetStructure();

			if (_validServices.contains(thisTrip.get_service_id())) {
				// System.out.println("Linking trip " + thisTrip.get_trip_id());
				String shapeID = thisTrip.get_shape_id();

				// link the trip to it's route
				thisTrip.setRoute(_routeMap.get(thisTrip.get_route_id()));

				if (shapeMap.get(shapeID) != null) {
					thisTrip.setVertexList(shapeMap.get(shapeID));
				} else {
					shape = new ArrayList<>();

					boolean done = false;
					boolean inShape = false;
					// add all vertices to their respective trip's vertex list
					for (int i = 0; i < mVertices.size(); i++) {
						Vertex v = mVertices.get(i);
						if (v.get_shape_id().equals(shapeID)) {
							inShape = true;
							shape.add(v);
						} else {
							if (inShape) {
								break;
							}
						}
					}
					thisTrip.setVertexList(shape);
					shapeMap.put(shapeID, shape);
				}
			}
		}
		shapeMap.clear();
	}

	/**
	 * Removes duplicate route paths, which happens sometimes due to redundant
	 * GTFS data.
	 */
	private void removeDuplicateRoutePaths() {
		// get rid of any duplicate routes, because it seems to happen?
		ListIterator li;
		if (_paths != null) {
			li = _paths.listIterator();
			if (li.hasNext()) {
				RoutePath keep = (RoutePath) li.next();
				while (li.hasNext()) {
					RoutePath check = (RoutePath) li.next();
					// assuming that two paths on the same route/service with
					// identical start and end times must be the same path
					if (keep.getRouteID() == check.getRouteID()
							&& (keep.getServiceID() == null ? check
									.getServiceID() == null : keep
									.getServiceID()
									.equals(check.getServiceID()))
							&& keep.getStartTimecode() == check
									.getStartTimecode()
							&& keep.getEndTimecode() == check.getEndTimecode()) {
						// && keep.sameStops(check)) {
						// System.out.println("Removing a duplicate path!");
						li.remove();
					} else {
						keep = check;
					}
				}
			}
		}
	}

	/**
	 * Reset the date to current and revalidate all calendar related data.
	 */
	public void resetDate() {
		java.util.Calendar currentDate = java.util.Calendar.getInstance();
		java.util.Calendar checkDate = java.util.Calendar.getInstance();
		Date nowDate = new Date();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		String stringDate = sdf.format(new Date());

		// String currentDate = sdf.format(new Date());
		currentDate.setTime(nowDate);
		currentDate.set(java.util.Calendar.HOUR, 0);
		currentDate.set(java.util.Calendar.MINUTE, 0);
		currentDate.set(java.util.Calendar.SECOND, 0);

		_validServices.clear();

		// 1=sunday, 2=monday, 3=Calendar.WEDNESDAY...
		int dayOfWeek = (currentDate.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7;

		_inDay = Calendar.Weekdays.values()[dayOfWeek];

		System.out.println("Checking Calendar...");
		if (mCalendar != null) {
			for (Calendar c : mCalendar) {
				if (c == null) {
					continue;
				}
				try {
					// check if calendar date is in range
					// commenting out because our test GTFS files are expired
					checkDate.setTime(_dateFormatter.parse(c.get_start_date()));
					if (currentDate.compareTo(checkDate) < 0) {
						continue;
					}

					checkDate.setTime(_dateFormatter.parse(c.get_end_date()));
					if (currentDate.compareTo(checkDate) > 0) {
						continue;
					}

					if (c.getWeekdays().contains(_inDay)) {
						_validServices.add(c.get_service_id());

					}
				} catch (ParseException ex) {
					Logger.getLogger(GTFS.class.getName()).log(Level.SEVERE,
							null, ex);
				}

			}
		}

		System.out.println("Checking CalendarDates...");
		if (mCalendarDates != null) {
			for (CalendarDate c : mCalendarDates) {
				if (c == null) {
					continue;
				}

				if (stringDate.equals(c.get_date())) {
					if (c.get_exception_type() == 1) {
						_validServices.add(c.get_service_id());
					} else if (c.get_exception_type() == 2) {
						_validServices.remove(c.get_service_id());

					}
				}
			}
		}
		System.out.println(_validServices.size() + " services valid today!");

		System.out.println("Starting linkStructure...");

		linkStructure();

	}

	/**
	 * Show RoutePaths for debugging
	 */
	private void showRoutePaths() {
		for (RoutePath rp : _paths) {
			int i = 0;
			for (Trip t : rp.getPath()) {
				if (i == 0) {
					System.out.print(t.getRoute().getName() + " (");
					System.out.print(t.get_service_id() + ") | ");
					System.out.print(minutesToTime(t.getStartTime()) + "(");
					System.out.print(t.getFirstStop().getStop().getID()
							+ ") -- > ");
				}
				i++;
				System.out.print(t.get_trip_id() + " ");
			}
			System.out.println("<-- ("
					+ rp.getPath().get(rp.getPath().size() - 1).getLastStop()
							.getStop().getID()
					+ ") "
					+ minutesToTime(rp.getPath().get(rp.getPath().size() - 1)
							.getEndTime()));
		}
	}

	/**
	 * Update the current stop window to right now.
	 */
	private synchronized void updateStopWindow() {
		int now = timeToMinutes(new SimpleDateFormat("HH:mm:ss")
				.format(new Date()));
		ArrayList<ArrayList<StopAdapter>> nowStops = getAllStopsInTimeframe(
				minutesToTime(now - (WINDOW_MARGIN * 2)), // hysterisis for the
															// window margins
				minutesToTime(now + WINDOW_MARGIN));
		/*
		 * System.out.println(minutesToTime(now - WINDOW_MARGIN) + " - " +
		 * minutesToTime(now) + " - " + minutesToTime(now + WINDOW_MARGIN) );
		 */

		updateMasterStopWindow(nowStops, _id);

	}
}
