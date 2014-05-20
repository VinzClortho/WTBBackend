package com.jasonlafrance.wtbbackend.gtfs;

import java.util.ArrayList;

import com.jasonlafrance.wtbbackend.wtb_util.CSVParser;

/**
 *
 * @author Jason LaFrance
 */
public final class Agency extends GTFSParser {

    private static final String _filename = "//agency.txt";

    private String _agency_id = null;
    private String _agency_name = null;
    private String _agency_url = null;
    private String _agency_timezone = null;
    private String _agency_lang = null;
    private String _agency_phone = null;
    private String _agency_fare_url = null;
    private String _bikes_policy_url = null;

    public Agency(
            String inID,
            String inName,
            String inUrl,
            String inTimezone,
            String inLang,
            String inPhone) {
        _agency_id = inID;
        _agency_name = inName;
        _agency_url = inUrl;
        _agency_timezone = inTimezone;
        _agency_lang = inLang;
        _agency_phone = inPhone;
    }

    public Agency(String inLine) {
    }

    // do this without reflection!
    public Agency(String inLine, int id) {
        ArrayList<String> h = _headers.get(id).get(_filename);
        String[] f = CSVParser.parseLine(inLine);

        if (f.length != h.size()) {
            return;
        }

        for (int i = 0; i < f.length; i++) {
            switch (h.get(i)) {
                case "agency_fare_url":
                    set_agency_fare_url(f[i]);
                    break;
                case "agency_id":
                    set_agency_id(f[i]);
                    break;
                case "agency_lang":
                    set_agency_lang(f[i]);
                    break;
                case "agency_name":
                    set_agency_name(f[i]);
                    break;
                case "agency_phone":
                    set_agency_phone(f[i]);
                    break;
                case "agency_timezone":
                    set_agency_timezone(f[i]);
                    break;
                case "agency_url":
                    set_agency_url(f[i]);
                    break;
                 case "bikes_policy_url":
                    set_bikes_policy_url(f[i]);
                    break;
                 default:
                     break;
            }
        }
    }

    public void set_agency_id(String in) {
        _agency_id = in;
    }

    public void set_agency_name(String in) {
        _agency_name = in;
    }

    public void set_agency_url(String in) {
        _agency_url = in;
    }

    public void set_agency_timezone(String in) {
        _agency_timezone = in;
    }

    public void set_agency_lang(String in) {
        _agency_lang = in;
    }

    public void set_agency_phone(String in) {
        _agency_phone = in;
    }

    public void set_agency_fare_url(String in) {
        _agency_fare_url = in;
    }

    public void set_bikes_policy_url(String in) {
        _bikes_policy_url = in;
    }

    @Override
    public int getID() {
        return _agency_name.hashCode();
    }

    public String get_agency_id() {
        return _agency_id;
    }

    public String get_agency_name() {
        return _agency_name;
    }

    public String get_agency_url() {
        return _agency_url;
    }

    public String get_agency_timezone() {
        return _agency_timezone;
    }

    public String get_agency_lang() {
        return _agency_lang;
    }

    public String get_agency_phone() {
        return _agency_phone;
    }

    public String get_agency_fare_url() {
        return _agency_fare_url;
    }

    public String get_bikes_policy_url() {
        return _bikes_policy_url;
    }

    @Override
    public String toString() {
        return _agency_id + ","
                + _agency_name + ","
                + _agency_url + ","
                + _agency_timezone + ","
                + _agency_lang + ","
                + _agency_phone + "\n";
    }

    @Override
    public String getFilename() {
        return _filename;
    }

}
