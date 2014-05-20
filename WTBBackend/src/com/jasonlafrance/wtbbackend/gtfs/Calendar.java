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

import static com.jasonlafrance.wtbbackend.gtfs.GTFSParser._headers;

import java.util.ArrayList;

import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 *
 * @author Jason LaFrance
 */
public final class Calendar extends GTFSParser {

    private static final String _filename = "//calendar.txt";

    private String _service_id = null;
    private final boolean[] _weekday = new boolean[7]; // monday = 0
    private String _start_date = null;
    private String _end_date = null;

    public Calendar() {;
    }

    public Calendar(
            String inID,
            int inDays,
            String inStart,
            String inEnd
    ) {
        _service_id = inID;
        _start_date = inStart;
        _end_date = inEnd;

        int mask = 1;
        for (int i = 0; i < 7; i++) {
            if ((inDays & mask) > 0) {
                _weekday[i] = true;
            }
            mask <<= 1;
        }
    }

    public Calendar(String inLine) {
    }
    
    public Calendar(String inLine, int id) {
        ArrayList<String> h = _headers.get(id).get(_filename);
        String[] f = CSVParser.parseLine(inLine);

        if (f.length != h.size()) {
            return;
        }

        for (int i = 0; i < f.length; i++) {
            switch (h.get(i)) {
                case "end_date":
                    set_end_date(f[i]);
                    break;
                case "monday":
                    set_monday(f[i]);
                    break;
                case "tuesday":
                    set_tuesday(f[i]);
                    break;
                case "wednesday":
                    set_wednesday(f[i]);
                    break;
                case "thursday":
                    set_thursday(f[i]);
                    break;
                case "friday":
                    set_friday(f[i]);
                    break;
                case "saturday":
                    set_saturday(f[i]);
                    break;
                case "sunday":
                    set_sunday(f[i]);
                    break;
                case "service_id":
                    set_service_id(f[i]);
                    break;
                 case "start_date":
                    set_start_date(f[i]);
                    break;
                 default:
                     break;
            }
        }
    }


    public void set_service_id(String in) {
        _service_id = in;

    }

    public void set_monday(boolean in) {
        _weekday[0] = in;
    }

    public void set_monday(String in) {
        _weekday[0] = in.equals("1");
    }

    public void set_tuesday(boolean in) {
        _weekday[1] = in;
    }

    public void set_tuesday(String in) {
        _weekday[1] = in.equals("1");
    }

    public void set_wednesday(boolean in) {
        _weekday[2] = in;
    }

    public void set_wednesday(String in) {
        _weekday[2] = in.equals("1");
    }

    public void set_thursday(boolean in) {
        _weekday[3] = in;
    }

    public void set_thursday(String in) {
        _weekday[3] = in.equals("1");
    }

    public void set_friday(boolean in) {
        _weekday[4] = in;
    }

    public void set_friday(String in) {
        _weekday[4] = in.equals("1");
    }

    public void set_saturday(boolean in) {
        _weekday[5] = in;
    }

    public void set_saturday(String in) {
        _weekday[5] = in.equals("1");
    }

    public void set_sunday(boolean in) {
        _weekday[6] = in;
    }

    public void set_sunday(String in) {
        _weekday[6] = in.equals("1");
    }

    public void set_start_date(String in) {
        _start_date = in;
    }

    public void set_end_date(String in) {
        _end_date = in;
    }

    public String get_service_id() {
        return _service_id;
    }

    @Override
    public int getID() {
        return _service_id.hashCode();
    }

    public String get_start_date() {
        return _start_date;
    }

    public String get_end_date() {
        return _end_date;
    }

    public boolean isMonday() {
        return _weekday[0];
    }

    public boolean isTuesday() {
        return _weekday[1];
    }

    public boolean isWednesday() {
        return _weekday[2];
    }

    public boolean isThursday() {
        return _weekday[3];
    }

    public boolean isFriday() {
        return _weekday[4];
    }

    public boolean isSaturday() {
        return _weekday[5];
    }

    public boolean isSunday() {
        return _weekday[6];
    }

    public ArrayList<Calendar.Weekdays> getWeekdays() {
        ArrayList<Calendar.Weekdays> out = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            if (_weekday[i]) {
                out.add(Calendar.Weekdays.values()[i]);
            }
        }

        return out;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += _service_id + ",";

        for (int i = 0; i < 7; i++) {
            ret += _weekday[i] ? "1," : "0,";
        }

        ret += _start_date + ",";
        ret += _end_date + "\n";

        return ret;
    }

    @Override
    public String getFilename() {
        return _filename;

    }

    public static enum Weekdays {

        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }
}
