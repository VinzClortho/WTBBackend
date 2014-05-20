package com.jasonlafrance.wtbbackend.vehicle;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Jason LaFrance
 */
public class DroneQueue {

    private final LinkedList<Drone> runningDrones;
    private final LinkedList<Drone> waitingDrones;

    // should be a priority queue, but I don't care enough for testing
    public DroneQueue(LinkedList<Drone> inDrones) {
        runningDrones = inDrones;
        waitingDrones = new LinkedList<>();
    }

    public synchronized void addDrone(Drone inDrone) {
        waitingDrones.add(inDrone);
    }

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

    public boolean isEmpty() {
        return waitingDrones.isEmpty();
    }
}
