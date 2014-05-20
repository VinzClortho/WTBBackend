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
public final class FareAttribute extends GTFSParser {

    private static final String _filename = "//fare_attributes.txt";

    private String _fare_id = null;
    private double _price = 0.0;
    private String _currency_type = null;
    private int _payment_method = -1;
    private int _transfers = -1;
    private double _transfer_duration = 0.0;

    public FareAttribute() {;
    }

    public FareAttribute(
            String inID,
            double inPrice,
            String inType,
            int inMethod,
            int inTransfers,
            double inTransDur
    ) {
        _fare_id = inID;
        _price = inPrice;
        _currency_type = inType;
        _payment_method = inMethod;
        _transfers = inTransfers;
        _transfer_duration = inTransDur;
    }

    public FareAttribute(String inLine) {
    }

    public FareAttribute(String inLine, int id) {
        ArrayList<String> h = _headers.get(id).get(_filename);
        String[] f = CSVParser.parseLine(inLine);

        if (f.length != h.size()) {
            return;
        }

        for (int i = 0; i < f.length; i++) {
            switch (h.get(i)) {
                case "currency_type":
                    set_currency_type(f[i]);
                    break;
                case "fare_id":
                    set_fare_id(f[i]);
                    break;
                case "payment_method":
                    set_payment_method(f[i]);
                    break;
                case "price":
                    set_price(f[i]);
                    break;
                case "transfer_duration":
                    set_transfer_duration(f[i]);
                    break;
                case "transfers":
                    set_transfers(f[i]);
                    break;
                default:
                    break;
            }
        }
    }

    public void set_fare_id(String in) {
        _fare_id = in;
    }

    public void set_price(double in) {
        _price = in;
    }

    public void set_price(String in) {
        try {
            _price = Double.parseDouble(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_currency_type(String in) {
        _currency_type = in;
    }

    public void set_payment_method(int in) {
        _payment_method = in;
    }

    public void set_payment_method(String in) {
        try {
            _payment_method = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_transfers(int in) {
        _transfers = in;
    }

    public void set_transfers(String in) {
        try {
            _transfers = Integer.parseInt(in);
        } catch (NumberFormatException e) {;
        }
    }

    public void set_transfer_duration(double in) {
        _transfer_duration = in;
    }

    public void set_transfer_duration(String in) {
        try {
            _transfer_duration = Double.parseDouble(in);
        } catch (NumberFormatException e) {;
        }
    }

    @Override
    public int getID() {
        return _fare_id.hashCode();
    }

    public String get_fare_id() {
        return _fare_id;
    }

    public double get_price() {
        return _price;
    }

    public String get_currency_type() {
        return _currency_type;
    }

    public int get_payment_method() {
        return _payment_method;
    }

    public int get_transfers() {
        return _transfers;
    }

    public double get_transfer_duration() {
        return _transfer_duration;
    }

    @Override
    public String toString() {
        return _fare_id + ","
                + _price + ","
                + _currency_type + ","
                + _payment_method + ","
                + _transfers + ","
                + _transfer_duration + "\n";
    }

    @Override
    public String getFilename() {
        return _filename;
    }
}
