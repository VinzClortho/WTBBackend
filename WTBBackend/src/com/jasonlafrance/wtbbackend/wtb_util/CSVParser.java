package com.jasonlafrance.wtbbackend.wtb_util;

import java.util.ArrayList;

/**
 *
 * @author Jason LaFrance
 */
public class CSVParser {

    public static String[] parseLine(String in) {
        ArrayList<String> list = new ArrayList<>();

        boolean notInsideComma = true;
        int start = 0, end = 0;
        for (int i = 0; i < in.length(); i++) {
            if (in.charAt(i) == ',' && notInsideComma) {
                list.add(in.substring(start, i));
                start = i + 1;
            } else if (in.charAt(i) == '"') {
                notInsideComma = !notInsideComma;
            }
        }
        list.add(in.substring(start));
        return list.toArray(new String[list.size()]);
    }
}
