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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import com.jasonlafrance.wtbbackend.config.Config;
import com.jasonlafrance.wtbbackend.vehicle.Vehicle;
import com.jasonlafrance.wtbbackend.wtb_util.HexUtil;

/**
 * HTTP input handler class.
 * 
 * @author Jason LaFrance
 */
public class HTTPInput implements Runnable {

	private static final LinkedList<HTTPInput> sRecycleBin = new LinkedList<>();

	// using an object pool pattern to help avoid excessive object creation
	// finished HTTPInput objects are added to the recycle bin and reclaimed for
	// new instances
	/**
	 * 
	 * @param i
	 *            Input from client.
	 * @param o
	 *            Output back to client.
	 * @param inCipher
	 *            Pre-generated cipher object.
	 * @return Returns an HTTPInput object.
	 */
	public static HTTPInput getInstance(BufferedReader i, DataOutputStream o,
			Cipher inCipher) {
		if (sRecycleBin.isEmpty()) {
			HTTPInput ret = new HTTPInput(i, o, inCipher);
			return ret;
		} else {
			HTTPInput recycle;

			synchronized (sRecycleBin) {
				recycle = sRecycleBin.pop();
			}
			recycle.setmCipher(inCipher);
			recycle.setmInput(i);
			recycle.setmOutput(o);
			return recycle;
		}
	}

	private BufferedReader mInput;
	private DataOutputStream mOutput;
	private Cipher mCipher;

	/**
	 * Create an HTTPInput object from given values.
	 * 
	 * @param i
	 *            Input from client.
	 * @param o
	 *            Output to client.
	 * @param inCipher
	 *            Pre-generated cipher object.
	 */
	private HTTPInput(BufferedReader i, DataOutputStream o, Cipher inCipher) {
		mInput = i;
		mOutput = o;
		mCipher = inCipher;
	}

	/**
	 * Builds an HTTP header.
	 * 
	 * @param code
	 *            The return code to send back.
	 * @param fileType
	 *            File type to report to the client. Note really used much since
	 *            we're mainly just transfering raw binary and text.
	 * @return Returns a completed HTTP header.
	 */
	private String constructHttpHeader(int code, int fileType) {
		String s = "HTTP/1.0 ";

		switch (code) {
		case 200:
			s = s + "200 OK";
			break;
		case 400:
			s = s + "400 Bad Request";
			break;
		case 403:
			s = s + "403 Forbidden";
			break;
		case 404:
			s = s + "404 Not Found";
			break;
		case 500:
			s = s + "500 Internal Server Error";
			break;
		case 501:
			s = s + "501 Not Implemented";
			break;
		}

		s = s + "\r\n";
		s = s + "Connection: close\r\n";
		s = s + "Server: WTBBackend GPS Input\r\n"; // server name

		switch (fileType) {
		case 0:
			break;
		default:
			s = s + "Content-Type: text/html\r\n";
			break;
		}

		s = s + "\r\n";
		return s;
	}

	/**
	 * This is is HTTP input stream handler thread. It does the packet
	 * decrypting, parsing, and vehicle updating.
	 */
	@Override
	public void run() {
		try {
			String temp = mInput.readLine().toUpperCase();
			boolean everythingOK = true;

			if (temp.startsWith("GET")) {
				// isolate mInput field between slash and space
				int start = temp.indexOf('/') + 1;
				int end = temp.indexOf(' ', start);
				String field = temp.substring(start, end);
				GPSPacket packet = null;

				try {
					packet = new GPSPacket(mCipher.doFinal(HexUtil
							.getBytes(field)));

				} catch (IllegalBlockSizeException | BadPaddingException ex) {
					everythingOK = false;
				}
				if (packet != null) {
					// System.out.println("Packet in: " + packet);
					Vehicle.updateVehicle(packet.getID(), packet.getLat(),
							packet.getLon());
				}

			} else {
				everythingOK = false;
			}

			if (everythingOK) // it's good! acknowledge!
			{
				mOutput.writeBytes(constructHttpHeader(200, 5));
				mOutput.writeBytes(Config.getInstance().getOption(
						Config.PACKET_OK));
			} else {
				mOutput.writeBytes(constructHttpHeader(404, 0));
				mOutput.writeBytes(Config.getInstance().getOption(
						Config.PACKET_BAD));
			}
			mOutput.close();
		} catch (IOException ex) {
			// Logger.getLogger(HTTPInput.class.getName()).log(Level.SEVERE,
			// null, ex);
		}

		synchronized (sRecycleBin) {
			sRecycleBin.push(this);
		}
	}

	/**
	 * Set the cipher.
	 * 
	 * @param mCipher
	 *            the mCipher to set
	 */
	public void setmCipher(Cipher mCipher) {
		this.mCipher = mCipher;
	}

	/**
	 * Set the input reader.
	 * 
	 * @param mInput
	 *            the mInput to set
	 */
	public void setmInput(BufferedReader mInput) {
		this.mInput = mInput;
	}

	/**
	 * Set the output stream.
	 * 
	 * @param mOutput
	 *            the mOutput to set
	 */
	public void setmOutput(DataOutputStream mOutput) {
		this.mOutput = mOutput;
	}
}
