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

package com.jasonlafrance.wtbbackend.wtb_util;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Singleton class to manage a debugging text output window Kinda handy because
 * it keeps debugging text out of the console output.
 * 
 * @author Jason LaFrance
 */

public class DebugWindow {

	private static DebugWindow sInstance = null;

	/**
	 * Get or create the DebugWindow instance
	 * 
	 * @return The DebugWindow
	 * 
	 */
	public static DebugWindow getInstance() {
		if (sInstance == null) {
			sInstance = new DebugWindow();
		}
		return sInstance;
	}

	private final JFrame mFrame;
	private final JTextArea mTextArea;
	private final JScrollPane mScrollPane;

	private StringBuilder mSB;

	/**
	 * Create the DebugWindow
	 */
	private DebugWindow() {
		mFrame = new JFrame();
		mFrame.setTitle("Debug Window");
		mFrame.setSize(200, 600);
		mTextArea = new JTextArea();
		mTextArea.setEditable(false);
		mScrollPane = new JScrollPane(mTextArea);
		mFrame.add(mScrollPane);
		mFrame.setVisible(true);
		mSB = new StringBuilder();
	}

	/**
	 * Add text to the window
	 * 
	 * @param in
	 *            The text to add
	 */
	public void addText(String in) {
		mSB.append(in).append("\n");
		mTextArea.setText(mSB.toString());
	}

	/**
	 * Clear all text from the window
	 */
	public void clearText() {
		mSB = new StringBuilder();
		mTextArea.setText(mSB.toString());
	}

	/**
	 * Close the window
	 */
	public void closeWindow() {
		mFrame.setVisible(false);
	}
}
