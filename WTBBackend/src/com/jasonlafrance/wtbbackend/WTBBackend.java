package com.jasonlafrance.wtbbackend;

import static com.jasonlafrance.wtbbackend.wtb_util.TimeUtil.timeToMinutes;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jasonlafrance.wtbbackend.config.Config;
import com.jasonlafrance.wtbbackend.gps_portal.GPSPortal;
import com.jasonlafrance.wtbbackend.gtfs.GTFS;
import com.jasonlafrance.wtbbackend.vehicle.Drone;
import com.jasonlafrance.wtbbackend.vehicle.DroneQueue;
import com.jasonlafrance.wtbbackend.vehicle.Vehicle;

/**
 *
 * @author Gozer
 */
public class WTBBackend {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean ok = false;

        long startTime, endTime;
        LinkedList<Drone> mDroneArm = new LinkedList<>();
        DroneQueue droneQueue = new DroneQueue(mDroneArm);

        // load the config options
        Config config = Config.get();
        config.load("config.xml");

        // erase old kmls
        clearKMLs();

        String dirs[] = config.getOption(Config.GTFS_DIRS).split(",");
        int timeGap = config.getIntOption(Config.GTFS_TIME_GAP);

        GraphFrame gp = null;
        if (config.getBooleanOption(Config.DEBUG_DISPLAY)) {
            gp = new GraphFrame();
        }

        final String password = Config.get().getOption(Config.SERVER_PASSWORD);
        final int port = Config.get().getIntOption(Config.SERVER_PORT);

        GPSPortal g = new GPSPortal(port, password);

        int id = 1;
        final double busSpeed = Config.get().getDoubleOption(Config.DRONE_SPEED);
        final double busUpdateTime = Config.get().getDoubleOption(Config.DRONE_UPDATE_SPEED);

        boolean dronesActive = config.getBooleanOption(Config.DRONES_ACTIVE);

        if (dronesActive) {
            System.out.println("Drones Active!");
        }

        ArrayList<GTFS> gtfs = new ArrayList<>();

        for (String dir : dirs) {
            dir = dir.trim();

            System.out.println(dir + "\n----------");
            try {
                startTime = System.currentTimeMillis();

                GTFS current = new GTFS(dir, timeGap);
                gtfs.add(current);

                endTime = System.currentTimeMillis();
                System.out.println(dir + " loaded and parsed in " + ((endTime - startTime) / 1000.0) + " seconds");

                current.calcExtremes();

                System.out.println(dir + " id: " + current.getID());

                if (config.getBooleanOption(Config.DEBUG_DISPLAY)) {
                    gp.adjustViewport(current.getMinLon(), current.getMinLat(),
                            current.getMaxLon(), current.getMaxLat());
                    gp.addPaths(current.getPaths());
                }
                if (dronesActive) {
                    for (int i = 0; i < current.getPaths().size(); i++) {
                        // generate timecode for right now
                        int now = timeToMinutes(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        if (current.isValidService(current.getPaths().get(i).getServiceID())
                                && current.getPaths().get(i).getEndTimecode() > now) {
                            System.out.println(dir + ": " + current.getPaths().get(i));
                            droneQueue.addDrone(new Drone(id,
                                    current.getPaths().get(i),
                                    busSpeed,
                                    busUpdateTime,
                                    "http://127.0.0.1:8080",
                                    password,
                                    false));
                            id++;
                        }
                    }
                }

                ok = true;
            } catch (Exception e) {
                System.out.println(e.toString());
                System.exit(-1);
            }
            System.out.println();
        }

        if (config.getBooleanOption(Config.DEBUG_DISPLAY)) {
            gp.setVisible(true);
        }
        boolean running = true;
        int vListTimeout = Config.get().getIntOption(Config.VEHICLE_LIST_TIMEOUT);
        
        System.out.println("Running...");
        
       
        while (running) {
            int now = timeToMinutes(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            Vehicle.cleanUp(vListTimeout);
            droneQueue.check(now);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WTBBackend.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.gc();
            running = !droneQueue.isEmpty();
        }
    }

    private static void clearKMLs() {
        File dir = new File("./");

        for (File f : dir.listFiles()) {
            if (f.isFile() && f.getName().toLowerCase().endsWith(".kml")) {
                f.delete();
                System.out.println("Deleting " + f.getName());
            }
        }
    }
}
