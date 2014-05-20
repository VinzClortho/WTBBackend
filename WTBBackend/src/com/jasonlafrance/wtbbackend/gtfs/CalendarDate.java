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
public final class CalendarDate extends GTFSParser {

    private static final String _filename = "//calendar_dates.txt";

    private String _service_id = null;
    private String _date = null;
    private int _exception_type = -1;

    public CalendarDate() {;
    }

    public CalendarDate(
            String inID,
            String inDate,
            int inType
    ) {
        _service_id = inID;
        _date = inDate;
        _exception_type = inType;
    }

    public CalendarDate(String inLine) {
    }
    
    public CalendarDate(String inLine, int id) {
        ArrayList<String> h = _headers.get(id).get(_filename);
        String[] f = CSVParser.parseLine(inLine);

        if (f.length != h.size()) {
            return;
        }

        for (int i = 0; i < f.length; i++) {
            switch (h.get(i)) {
                case "date":
                    set_date(f[i]);
                    break;
                case "exception_type":
                    set_exception_type(f[i]);
                    break;
                case "service_id":
                    set_service_id(f[i]);
                    break;
                default:
                    break;
            }
        }
    }

    public void set_service_id(String in) {
            _service_id = in;
    }

    public void set_date(String in) {
        _date = in;
    }

    public void set_exception_type(int in) {
        _exception_type = in;
    }

    public void set_exception_type(String in) {
        try {
            _exception_type = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public String get_service_id() {
        return _service_id;
    }

    @Override
    public int getID() {
        return _service_id.hashCode();
    }

    public String get_date() {
        return _date;
    }

    public int get_exception_type() {
        return _exception_type;
    }

    @Override
    public String toString() {
        return _service_id + ","
                + _date + ","
                + _exception_type + "\n";
    }

    @Override
    public String getFilename() {
        return _filename;
    }

}
