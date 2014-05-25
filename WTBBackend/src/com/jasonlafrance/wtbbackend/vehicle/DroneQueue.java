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

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * This class manages a queue of Drones. Drones are started as their start times
 * are passed
 * 
 * @author Jason LaFrance
 */
public class DroneQueue {

	private final LinkedList<Drone> runningDrones;
	private final LinkedList<Drone> waitingDrones;

	// should be a priority queue, but I don't care enough for testing
	/**
	 * Create a DroneQueue object
	 * 
	 * @param inDrones
	 *            List of Drones to add to the queue
	 */
	public DroneQueue(LinkedList<Drone> inDrones) {
		runningDrones = inDrones;
		waitingDrones = new LinkedList<>();
	}

	/**
	 * Add a Drone to the queue
	 * 
	 * @param inDrone
	 *            The Drone to add
	 */
	public synchronized void addDrone(Drone inDrone) {
		waitingDrones.add(inDrone);
	}

	/**
	 * Check if it's time to start any Drones based in a given time code
	 * 
	 * @param inTimecode
	 *            The time code to check against
	 */
	public synchronized void check(int inTimecode) {
		ListIterator li = waitingDrones.listIterator();

		while (li.hasNext()) {
			Drone d = (Drone) li.next();
			if (d.getStartTimcode() <= inTimecode) {
				runningDrones.add(d);
				li.remove();
				d.startDrone();
			}
		}
	}

	/**
	 * Check if the queue is empty
	 * 
	 * @return True if the queue is empty
	 */
	public boolean isEmpty() {
		return waitingDrones.isEmpty();
	}
}
