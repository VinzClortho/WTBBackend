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
public final class Transfer extends GTFSParser {

    private static final String _filename = "//transfers.txt";

    private String _from_stop_id = null, _to_stop_id = null;
    private int _transfer_type = -1;
    private double _min_transfer_time = 0.0;

    public Transfer() {;
    }

    public Transfer(
            String inFrom,
            String inTo,
            int inType,
            double minTrans
    ) {
        _from_stop_id = inFrom;
        _to_stop_id = inTo;
        _transfer_type = inType;
        _min_transfer_time = minTrans;
    }

    public Transfer(String inLine) {
    }
    
    public Transfer(String inLine, int id) {
        ArrayList<String> h = _headers.get(id).get(_filename);
        String[] f = CSVParser.parseLine(inLine);

        if (f.length != h.size()) {
            return;
        }

        for (int i = 0; i < f.length; i++) {
            switch (h.get(i)) {
                case "from_stop_id":
                    set_from_stop_id(f[i]);
                    break;
                case "min_transfer_time":
                    set_min_transfer_time(f[i]);
                    break;
                case "to_stop_id":
                    set_to_stop_id(f[i]);
                    break;
                case "transfer_type":
                    set_transfer_type(f[i]);
                    break;
                default:
                    break;
            }
        }
    }

    public void set_from_stop_id(String in) {
        _from_stop_id = in;
    }

    public void set_to_stop_id(String in) {
        _to_stop_id = in;
    }

    public void set_transfer_type(int in) {
        _transfer_type = in;
    }

    public void set_transfer_type(String in) {
        try {
            _transfer_type = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_min_transfer_time(double in) {
        _min_transfer_time = in;
    }

    public void set_min_transfer_time(String in) {
        try {
            _min_transfer_time = Double.parseDouble(in);
        } catch (NumberFormatException e) {;
        }
    }

    @Override
    public int getID() {
        return _from_stop_id.hashCode();
    }

    public String get_from_stop_id() {
        return _from_stop_id;
    }

    public String get_to_stop_id() {
        return _to_stop_id;
    }

    public int get_transfer_type() {
        return _transfer_type;
    }

    public double get_min_transfer_time() {
        return _min_transfer_time;
    }

    @Override
    public String toString() {
        return _from_stop_id + ","
                + _to_stop_id + ","
                + _transfer_type + ","
                + _min_transfer_time + "\n";
    }

    @Override
    public String getFilename() {
        return _filename;
    }
}
