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

import static com.jasonlafrance.wtbbackend.wtb_util.TimeUtil.timeToMinutes;
import static com.jasonlafrance.wtbbackend.wtb_util.VertexUtil.getLineSegmentIntersect;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.jasonlafrance.wtbbackend.gps_portal.GPSPacket;
import com.jasonlafrance.wtbbackend.gtfs.RoutePath;
import com.jasonlafrance.wtbbackend.gtfs.StopAdapter;
import com.jasonlafrance.wtbbackend.gtfs.Trip;
import com.jasonlafrance.wtbbackend.gtfs.Vertex;
import com.jasonlafrance.wtbbackend.wtb_util.HexUtil;

/**
 * Drone class for vehicle tracking testing. Drones mimic vehicles based on
 * supplied GTFS routing and scheduling. The drones also only interact with the
 * backend server via the GPS input portal, so as far as the server knows, these
 * are real vehicles.
 * 
 * @author Jason LaFrance
 */
public final class Drone extends Thread {

	/**
	 * Wrapper class for pairing vertices with specific sequence ordering
	 * 
	 * @author Jason LaFrance
	 * 
	 */
	private class Insertion {

		private final int index;
		private final Vertex item;

		/**
		 * Create an Insertion object with given data
		 * 
		 * @param index
		 *            Index to set to Vertex
		 * @param item
		 *            The Vertex to store
		 */
		public Insertion(int index, Vertex item) {
			this.index = index;
			this.item = item;
		}

		/**
		 * Get the index
		 * 
		 * @return The index value
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * Get the Vertex
		 * 
		 * @return The Vertex object
		 */
		public Vertex getVertex() {
			return item;
		}
	}

	private final ArrayList<Vertex> mPath = new ArrayList<>();
	private boolean isActive;
	private final int mID;

	private final String serverURL;
	private final double mSpeed;
	private double mDX, mDY;
	private double mLon, mLat;
	private final double mPushWait;
	private double segDistance;
	private double segTraveledDistance;
	private double distPerTick;
	private int waitMillis;
	private int mStartTimecode;

	private Cipher cipher;

	/**
	 * Create a Drone object from given data
	 * 
	 * @param inID
	 *            The Drone's ID for tracking
	 * @param inRoute
	 *            The Drone's route
	 * @param startSpeed
	 *            The Drone's steady speed
	 * @param inPushWait
	 *            The time between GPS beacons
	 * @param inServer
	 *            The backend server URL
	 * @param inPassword
	 *            The backend server password
	 * @param randomStart
	 *            If the Drone should start randomly instead of as scheduled
	 *            (DEBUGGING)
	 */
	public Drone(int inID, RoutePath inRoute, double startSpeed,
			double inPushWait, String inServer, String inPassword,
			boolean randomStart) {
		mID = inID;
		mPushWait = inPushWait;
		serverURL = inServer;
		mSpeed = Vehicle.mphToMetersPerSec(startSpeed);

		// System.out.println("new drone: " + inID);
		buildVertexPath(inRoute, randomStart);

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA");
			digest.update(inPassword.getBytes());
			SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeyException e) {
			System.out
					.println("Drone: Can't initialize cipher key for some reason!");
			return;
		}

		setup();

		// uncomment this for non-DroneQueue use
		// this.start();
	}

	/**
	 * Add Stops to the Vertex list
	 * 
	 * @param vertices
	 *            The Vertex list
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
	 * Build a Vertex path from a RoutePath object
	 * 
	 * @param inRoute
	 *            The RoutePath to extrapolate Vertex data from
	 * @param randomStart
	 *            If the Drone should start randomly instead of as scheduled
	 *            (DEBUGGING)
	 */
	private void buildVertexPath(RoutePath inRoute, boolean randomStart) {
		mPath.clear();

		ListIterator ti = inRoute.getPath().listIterator();

		while (ti.hasNext()) {
			Trip t = (Trip) ti.next();
			if (!mPath.isEmpty()
					&& mPath.get(mPath.size() - 1).getRawDistance(
							t.getVertices().get(0)) == 0.0) {
				mPath.remove(mPath.size() - 1);
			}

			if (randomStart) {
				int start = (int) (Math.random() * t.getVertices().size());
				ListIterator vi = t.getVertices().listIterator();
				while (start > 0) {
					vi.next();
					start--;
				}

				while (vi.hasNext()) {
					mPath.add((Vertex) vi.next());
					Vertex v = new Vertex();
				}
			} else {
				if (!mPath.isEmpty()) {
					mPath.remove(mPath.size() - 1);
				}
				mPath.addAll(t.getVertices());
			}
		}
		addStopsToVertexList(mPath, inRoute.getStops());
	}

	/**
	 * Get this Drone's start time code
	 * 
	 * @return The start time code
	 */
	public int getStartTimcode() {
		return mStartTimecode;
	}

	@Override
	public void run() {
		isActive = true;

		while (isActive) {
			try {
				sleep(waitMillis);
			} catch (InterruptedException ex) {
				Logger.getLogger(Drone.class.getName()).log(Level.SEVERE, null,
						ex);
			}
			tick();
		}
	}

	/**
	 * Send a GPS packet to the backend server.
	 */
	private void sendPacket() {
		GPSPacket gp = new GPSPacket((short) mID, (float) mLat, (float) mLon);
		String outHex = "";
		try {
			byte[] outPacket = cipher.doFinal(gp.getBytes());
			outHex = HexUtil.getHex(outPacket);
		} catch (IllegalBlockSizeException | BadPaddingException ex) {
			// Logger.getLogger(Drone.class.getName()).log(Level.SEVERE, null,
			// ex);
		}
		String response;
		if (outHex.length() > 0) {
			try {
				response = com.jasonlafrance.wtbbackend.gps_portal.HTTPOutput
						.HTTPGet(serverURL + "/" + outHex);
			} catch (Exception ex) {
				Logger.getLogger(Drone.class.getName()).log(Level.SEVERE, null,
						ex);
			}
		}
	}

	/**
	 * Initialize the Drone's state
	 */
	private void setup() {
		Vertex v = null, target = null;

		int now = timeToMinutes(new SimpleDateFormat("HH:mm:ss")
				.format(new Date()));
		boolean lookingForStart = true;

		while (!mPath.isEmpty() && lookingForStart) {
			v = mPath.get(0);
			mPath.remove(0);
			if (v.isStop()
					&& v.getStop().getStopTime().getArrivalTimecode() >= now) {
				mStartTimecode = v.getStop().getStopTime().getArrivalTimecode();
				// System.out.println(mID + " start time: " +
				// v.getStop().getStopTime().get_arrival_time());
				lookingForStart = false;
			}
		}

		if (!mPath.isEmpty()) {
			target = mPath.get(0);
			mPath.remove(0);
		}

		while (v.equals(target) && !mPath.isEmpty()) {
			target = mPath.get(0);
			mPath.remove(0);
		}

		if (v instanceof Vertex && target != null && target instanceof Vertex) {
			// mSpeed = Vehicle.mphToMetersPerSec(35.0);
			mLat = v.get_shape_pt_lat();
			mLon = v.get_shape_pt_lon();

			updateMotion(mLat, mLon, target);

			waitMillis = (int) (mPushWait * 1000.0);
		}
	}

	/**
	 * Start the Drone in action
	 */
	public void startDrone() {
		if (!isActive) {
			this.setPriority(Thread.MIN_PRIORITY);
			this.start();
		}
	}

	/**
	 * Stop the Drone
	 */
	public void stopDrone() {
		isActive = false;
	}

	/**
	 * This method handles what the Drone is doing as the moment
	 */
	private void tick() {
		mLat = mLat + mDY;
		mLon = mLon + mDX;

		sendPacket();

		segTraveledDistance += distPerTick;

		if (segTraveledDistance >= segDistance) {
			// start next segment or end
			if (!mPath.isEmpty()) {
				Vertex v = mPath.get(0);
				mPath.remove(0);
				if (v instanceof Vertex) {
					updateMotion(mLat, mLon, v);
				} else {
					isActive = false;
				}
				if (v.isStop() && v.getStop().getStopTime() != null) {
					while (v.getStop().getStopTime().getDepartureTimecode() > timeToMinutes(new SimpleDateFormat(
							"HH:mm:ss").format(new Date()))) {
						try {
							Thread.sleep(60000); // wait 1 minute!
						} catch (InterruptedException ex) {
							Logger.getLogger(Drone.class.getName()).log(
									Level.SEVERE, null, ex);
						}
					}
				}
			} else {
				isActive = false;
			}
		}
	}

	/**
	 * This method handles the Drone's virtual movement based on route and
	 * scheduling data
	 */
	private void updateMotion(double aLat, double aLon, Vertex b) {
		double dist = b.getDistanceInMeters(aLat, aLon);

		double timeAtSpeed = dist / mSpeed;
		if (timeAtSpeed < 1.0) {
			timeAtSpeed = 1.0;
		}

		mDY = (b.get_shape_pt_lat() - aLat) / timeAtSpeed * mPushWait;
		mDX = (b.get_shape_pt_lon() - aLon) / timeAtSpeed * mPushWait;

		segDistance = dist;
		segTraveledDistance = 0.0;
		distPerTick = mSpeed * mPushWait;
	}

}
