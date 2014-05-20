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

package com.jasonlafrance.wtbbackend.gps_portal;

import java.nio.ByteBuffer;

/**
 *
 * @author Jason LaFrance
 */
public class GPSPacket {

    private short id;
    private float lat, lon;

    /* GPSPacket is 12 bytes raw:
     * header - 2 bytes (B@)
     * id - 2 bytes
     * lat - 4 bytes
     * lon - 4 bytes
     */

    public GPSPacket(short inID, float inLat, float inLon) {
        id = inID;
        lat = inLat;
        lon = inLon;
    }

    public GPSPacket(byte[] in) {
        if (in.length == 12 && in[0] == 'B' && in[1] == '@') {
            ByteBuffer bytes = ByteBuffer.wrap(in);
            // clear the header sig from the buffer
            bytes.get();
            bytes.get();
            id = bytes.getShort();
            lat = bytes.getFloat();
            lon = bytes.getFloat();
        } else {
            id = -1;
            lat = 0.0f;
            lon = 0.0f;
        }
    }
    
    // pack up the values in the byte array
    public byte[] getBytes() {
        ByteBuffer bytes = ByteBuffer.allocate(12);
        bytes.put((byte) 66);
        bytes.put((byte) 64);
        bytes.putShort(id);
        bytes.putFloat(lat);
        bytes.putFloat(lon);
        return bytes.array();
    }

    public short getID() {
        return id;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return id + " -> " + lat + ", " + lon;

    }

}
