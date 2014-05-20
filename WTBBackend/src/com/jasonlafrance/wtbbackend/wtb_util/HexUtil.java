package com.jasonlafrance.wtbbackend.wtb_util;

/**
 *
 * @author Jason LaFrance
 */
public class HexUtil {

    static final String HEXES = "0123456789ABCDEF";

    public static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }

        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }

        return hex.toString();
    }

    public static byte[] getBytes(String hex) {
        if (hex == null) {
            return null;
        }
        byte[] bytes = new byte[(hex.length() / 2)];
        byte value = 0;
        int index = 0;
        int nybble = 0;
        for (final char b : hex.toCharArray()) {
            value += HEXES.indexOf(b);
            if (nybble == 0) {
                value <<= 4;
                nybble = 1;
            } else {
                bytes[index] = value;
                value = 0;
                nybble = 0;
                index++;
            }
        }

        return bytes;
    }
}
