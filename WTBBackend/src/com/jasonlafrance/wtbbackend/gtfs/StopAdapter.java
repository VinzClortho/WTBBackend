package com.jasonlafrance.wtbbackend.gtfs;

/**
 *
 * @author Jason LaFrance
 */
public class StopAdapter {

    private final Stop mStop;
    private StopTime mStopTime;
    private Route mRoute;
    private StopAdapter mNextStop;
    //private float mLat, mLon;

    public StopAdapter(Stop inStop) {
        mStop = inStop;
        //mLat = (float)inStop.get_stop_lat();
        //mLon = (float)inStop.get_stop_lon();
        mStopTime = null;
        mRoute = null;
        mNextStop = null;
    }
    /*
    public double getLat() {return mLat;}
    public double getLon() {return mLon;}
    
    public void setCoordinates(double inLat, double inLon){
        mLat = (float)inLat;
        mLon = (float)inLon;
    }
    */

    public Stop getStop() {
        return mStop;
    }

    public StopTime getStopTime() {
        return mStopTime;
    }

    public Route getRoute() {
        return mRoute;
    }

    public StopAdapter getNextStop() {
        return mNextStop;
    }

    public void setStopTime(StopTime in) {
        mStopTime = in;
    }

    public void setRoute(Route in) {
        mRoute = in;
    }

    public void setNextStop(StopAdapter in) {
        mNextStop = in;
    }
}
