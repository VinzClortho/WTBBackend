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
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Jason LaFrance
 */
public class GPSPortal extends Thread {

    private final int mPort;
    private final String mPassword;
    private Cipher mCipher;

    public GPSPortal(int inPort, String inPassword) {
        mPort = inPort;
        mPassword = inPassword;

        // thread it!
        this.start();
    }

    @Override
    public void run() {
        ServerSocket serversocket;

        try {
            System.out.println("Trying to bind to localhost on port " + mPort + "...");
            serversocket = new ServerSocket(mPort);
        } catch (IOException e) { //catch any errors and print errors to gui
            System.out.println("\nError:" + e.getMessage());
            return;
        }
        System.out.println("OK!");

        // generate the key from the password
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA");
            digest.update(mPassword.getBytes());
            SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
            mCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            mCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            System.out.println("GPSPortal: Can't initialize cipher key for some reason!");
            return;
        }

        while (true) {
            try {
                // wait for a connection
                Socket connectionsocket = serversocket.accept();
                // read http request into buffer
                BufferedReader input
                        = new BufferedReader(new InputStreamReader(connectionsocket.
                                        getInputStream()));
                // set up output stream
                DataOutputStream output
                        = new DataOutputStream(connectionsocket.getOutputStream());

                // spawn new listener thread
                HTTPInput portListener = HTTPInput.getInstance(input, output, mCipher);
                Thread t = new Thread(portListener);
                t.setPriority(Thread.MIN_PRIORITY);
                t.start();

            } catch (IOException e) {
                System.out.println("Error:" + e.getMessage());
            }
        }
    }
}
