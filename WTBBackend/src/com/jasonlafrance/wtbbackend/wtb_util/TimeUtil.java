package com.jasonlafrance.wtbbackend.wtb_util;

/**
 *
 * @author Jason LaFrance
 */
public class TimeUtil {

    public static int timeToMinutes(String in) {
        // time strings are stored as HH:MM:SS
        int mins;
        //System.out.println("timeToMinutes: " + in);
        // dirty implementation, but a little faster 

        if (in.length() == 8 && in.charAt(2) == ':' && in.charAt(5) == ':') {
            mins = (in.charAt(0) & 0xF) * 600
                    + (in.charAt(1) & 0xF) * 60
                    + (in.charAt(3) & 0xF) * 10
                    + (in.charAt(4) & 0xF);

        } else {
            mins = 0;
        }
        // clean implementation
        /*
         String[] n = in.split(":");
         mins = 0;

         if (n.length == 3) {
         try {
         mins = Integer.parseInt(n[0]) * 60 + Integer.parseInt(n[1]);
         } catch (NumberFormatException e) {;

         }
         }
         */
        return mins;
    }

    public static int timeToSeconds(String in) {
        // time strings are stored as HH:MM:SS
        int secs;

        // dirty implementation, but a little faster 
        /*
         if (in.length() == 8 && in.charAt(2) == ':' && in.charAt(5) == ':') {
         mins = (in.charAt(0) & 0xF) * 36000
         + (in.charAt(1) & 0xF) * 3600
         + (in.charAt(3) & 0xF) * 600
         + (in.charAt(4) & 0xF) * 60
         + (in.charAt(6) & 0xF) * 10
         + (in.charAt(7) & 0xF);
            
         } else {
         mins = 0;
         }
         */
        // clean implementation
        String[] n = in.split(":");
        secs = 0;

        if (n.length == 3) {
            try {
                secs = Integer.parseInt(n[0]) * 3600 + Integer.parseInt(n[1]) * 60 + Integer.parseInt(n[2]);
            } catch (NumberFormatException e) {;

            }
        }
        return secs;
    }

    public static String minutesToTime(int inTime) {
        if (inTime < 0) {
            return "";
        }
        int min = inTime % 60;
        inTime /= 60;
        int hour = inTime;

        String H = ("00" + Integer.toString(hour));
        H = H.substring(H.length() - 2);
        String M = ("00" + Integer.toString(min));
        M = M.substring(M.length() - 2);

        return H + ":" + M + ":00";
    }
}
