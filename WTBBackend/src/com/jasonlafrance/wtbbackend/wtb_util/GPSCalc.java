package com.jasonlafrance.wtbbackend.wtb_util;

/**
 *
 * @author Jason LaFrance
 */
public class GPSCalc {

    public final static double metersPerLat = 111300.0;
    public final static double metersPerLon = 85300.0;

    public static double getDistanceInMeters(double aLat, double aLon, double bLat, double bLon) {
        double dLat = (aLat - bLat) * metersPerLat;
        dLat *= dLat;
        double dLon = (aLon - bLon) * metersPerLon;
        dLon *= dLon;
        return Math.sqrt(dLat + dLon);
    }
    
    public static double getRawDistanceInMeters(double aLat, double aLon, double bLat, double bLon) {
        double dLat = (aLat - bLat) * metersPerLat;
        dLat *= dLat;
        double dLon = (aLon - bLon) * metersPerLon;
        dLon *= dLon;
        return dLat + dLon;
    }

    public static double getDistanceInMetersNew(double aLat, double aLon, double bLat, double bLon) {
        final double R = 6371000; // Earth's radius in meters

        aLat = Math.toRadians(aLat);
        aLon = Math.toRadians(aLon);
        bLat = Math.toRadians(bLat);
        bLon = Math.toRadians(bLon);

        double dLat = bLat - aLat;
        double dLon = bLon - aLon;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(aLat) * Math.cos(bLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    public static double getBearing(double aLat, double aLon, double bLat, double bLon) {
        // needs to be more accurate!
        /*
         double dX = (bLon - aLon) * metersPerLon;
         double dY = (bLat - aLat) * metersPerLat;
         double r = Math.sqrt(dX * dX + dY * dY);
         return (Math.acos(dX / r) * 180.0 / Math.PI);
         */
        /* 
         aLat = Math.toRadians(aLat);
         aLon = Math.toRadians(aLon);
         bLat = Math.toRadians(bLat);
         bLon = Math.toRadians(bLon);
         double dY = Math.sin(aLon) * Math.cos(bLat);
         double dX = Math.cos(aLat) * Math.sin(bLat)
         - Math.sin(aLat) * Math.cos(bLat) * Math.cos(aLon);
         double heading = Math.toDegrees(Math.atan2(dY, dX));
         if(heading < 0) heading += 360.0;
         return heading;
         */
        aLat = Math.toRadians(aLat);
        aLon = Math.toRadians(aLon);
        bLat = Math.toRadians(bLat);
        bLon = Math.toRadians(bLon);
        double dLon = bLon - aLon;
        double y = Math.sin(dLon) * Math.cos(bLat);
        double x = Math.cos(aLat) * Math.sin(bLat)
                - Math.sin(aLat) * Math.cos(bLat) * Math.cos(dLon);
        double bearing = Math.atan2(y, x);
        bearing = Math.toDegrees(bearing);

        if (bearing < 0.0) {
            bearing += 360.0;
        }

        return bearing;
    }

}
