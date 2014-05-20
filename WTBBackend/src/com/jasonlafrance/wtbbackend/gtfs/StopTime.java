package com.jasonlafrance.wtbbackend.gtfs;

import static com.jasonlafrance.wtbbackend.wtb_util.TimeUtil.minutesToTime;
import static com.jasonlafrance.wtbbackend.wtb_util.TimeUtil.timeToMinutes;

import java.util.ArrayList;

import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 *
 * @author Jason LaFrance
 */
public final class StopTime extends GTFSParser implements Comparable<StopTime> {

    private static final String _filename = "//stop_times.txt";

    private String _trip_id = null;
    //private String _arrival_time = "";
    private int _arrivalTimecode = -1;
    //private String _departure_time = "";
    private int _departureTimecode = -1;

    private String _stop_id = null;
    private int _stop_sequence = -1;
    private String _stop_headsign = null;
    private int _pickup_type = -1;
    private int _drop_off_type = -1;
    private double _shape_dist_traveled = 0.0;
    private String _timepoint = null;
    private String _continuous_stops = null;

    public StopTime() {;
    }

    public StopTime(
            String inTripID,
            String inArrival,
            String inDepart,
            String inStopID,
            int inStopSequence,
            String inHeadsign,
            int inPickupType,
            int inDropoffType,
            double inDistTraveled
    ) {
        _trip_id = inTripID;
        //_arrival_time = inArrival;
        _arrivalTimecode = timeToMinutes(inArrival);
        //_departure_time = inDepart;
        _departureTimecode = timeToMinutes(inDepart);
        _stop_id = inStopID;
        _stop_sequence = inStopSequence;
        _stop_headsign = inHeadsign;
        _pickup_type = inPickupType;
        _drop_off_type = inDropoffType;
        _shape_dist_traveled = inDistTraveled;
    }

    public StopTime(String inLine) {

    }

    // do this without reflection!
    public StopTime(String inLine, int id) {
        ArrayList<String> h = _headers.get(id).get(_filename);
        String[] f = CSVParser.parseLine(inLine);

        if (f.length != h.size()) {
            return;
        }

        for (int i = 0; i < f.length; i++) {
            switch (h.get(i)) {
                case "arrival_time":
                    set_arrival_time(f[i]);
                    break;
                case "continuous_stops":
                    set_continuous_stops(f[i]);
                    break;
                case "departure_time":
                    set_departure_time(f[i]);
                    break;
                case "drop_off_type":
                    set_drop_off_type(f[i]);
                    break;
                case "pickup_type":
                    set_pickup_type(f[i]);
                    break;
                case "shape_dist_traveled":
                    set_shape_dist_traveled(f[i]);
                    break;
                case "stop_headsign":
                    set_stop_headsign(f[i]);
                    break;
                case "stop_id":
                    set_stop_id(f[i]);
                    break;
                case "stop_sequence":
                    set_stop_sequence(f[i]);
                    break;
                case "timepoint":
                    set_timepoint(f[i]);
                    break;
                case "trip_id":
                    set_trip_id(f[i]);
                    break;
                default:
                    break;
            }
        }
    }

    public void set_trip_id(String in) {
        _trip_id = in;
    }

    public void set_arrival_time(String in) {

        _arrivalTimecode = timeToMinutes(in);

    }

    public void set_departure_time(String in) {

        _departureTimecode = timeToMinutes(in);

    }

    public void set_stop_id(String in) {
        _stop_id = in;
    }

    public void set_stop_sequence(int in) {
        _stop_sequence = in;
    }

    public void set_stop_sequence(String in) {
        try {
            _stop_sequence = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_stop_headsign(String in) {
        _stop_headsign = in;
    }

    public void set_pickup_type(int in) {
        _pickup_type = in;
    }

    public void set_pickup_type(String in) {
        try {
            _pickup_type = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_drop_off_type(int in) {
        _drop_off_type = in;
    }

    public void set_drop_off_type(String in) {
        try {
            _drop_off_type = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_shape_dist_traveled(double in) {
        _shape_dist_traveled = in;
    }

    public void set_shape_dist_traveled(String in) {
        try {
            _shape_dist_traveled = Double.parseDouble(in);
        } catch (NumberFormatException e) {;
        }
    }

    public String get_trip_id() {
        return _trip_id;
    }

    @Override
    public int getID() {
        return _trip_id.hashCode();
    }

    public String get_arrival_time() {
        return minutesToTime(_arrivalTimecode);
    }

    public int getArrivalTimecode() {
        return _arrivalTimecode;
    }

    public String get_departure_time() {
        return minutesToTime(_departureTimecode);
    }

    public int getDepartureTimecode() {
        return _departureTimecode;
    }

    public String get_stop_id() {
        return _stop_id;
    }

    public int get_stop_sequence() {
        return _stop_sequence;
    }

    public String get_stop_headsign() {
        return _stop_headsign;
    }

    public int get_pickup_type() {
        return _pickup_type;
    }

    public int get_drop_off_type() {
        return _drop_off_type;
    }

    public double get_shape_dist_traveled() {
        return _shape_dist_traveled;
    }

    @Override
    public String toString() {
        return _trip_id + ","
                + minutesToTime(_arrivalTimecode) + ","
                + minutesToTime(_arrivalTimecode) + ","
                + _stop_id + ","
                + _stop_sequence + ","
                + _stop_headsign + ","
                + _pickup_type + ","
                + _drop_off_type + ","
                + _shape_dist_traveled + "\n";
    }

    @Override
    public int compareTo(StopTime o) {
        // compare _trip_id
        int ret = _trip_id.compareTo(o.get_trip_id());
        if (ret == 0) {
            // compare _stop_sequence
            if (o.get_stop_sequence() < _stop_sequence) {
                ret = 1;
            }
            if (o.get_stop_sequence() > _stop_sequence) {
                ret = -1;
            }
        }
        // if we make it here, then the two are congruent
        return ret;
    }

    @Override
    public String getFilename() {
        return _filename;
    }

    /**
     * @return the _timepoint
     */
    public String get_timepoint() {
        return _timepoint;
    }

    /**
     * @param _timepoint the _timepoint to set
     */
    public void set_timepoint(String _timepoint) {
        this._timepoint = _timepoint;
    }

    /**
     * @return the _continuous_stops
     */
    public String get_continuous_stops() {
        return _continuous_stops;
    }

    /**
     * @param _continuous_stops the _continuous_stops to set
     */
    public void set_continuous_stops(String _continuous_stops) {
        this._continuous_stops = _continuous_stops;
    }
}
