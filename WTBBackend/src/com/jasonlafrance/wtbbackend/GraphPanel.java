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

package com.jasonlafrance.wtbbackend;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.jasonlafrance.wtbbackend.gtfs.GTFS;
import com.jasonlafrance.wtbbackend.gtfs.StopAdapter;
import com.jasonlafrance.wtbbackend.vehicle.Vehicle;
import com.jasonlafrance.wtbbackend.wtb_util.DebugWindow;
import com.jasonlafrance.wtbbackend.wtb_util.Graph.Node;

/**
 * 
 * @author Jason LaFrance
 */
public class GraphPanel extends JPanel implements ActionListener {

	private BufferedImage pathImage;
	private final GraphFrame mParent;
	private int xOffset = 0, yOffset = 0;

	public GraphPanel(GraphFrame inParent) {
		pathImage = null;
		mParent = inParent;

		Timer t = new Timer(1000, this);
		t.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mParent.repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int x, y;

		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

		if (pathImage != null && pathImage instanceof BufferedImage) {
			g2d.drawImage(pathImage, xOffset, yOffset, this);
		}

		g2d.setColor(Color.white);
		g2d.drawRect(0, 0, this.getWidth(), this.getHeight());

		int yy = 10;
		String dataText = Vehicle.getVehicles().size() + " busses.";
		g2d.drawString(dataText, 10, 10);

		DebugWindow.getInstance().clearText();
		for (Vehicle v : Vehicle.getVehicles()) {
			// yy += 12;
			// dataText = v.toString();
			// g2d.drawString(dataText, 10, yy);
			DebugWindow.getInstance().addText(v.toString());
		}

		// get and draw the stops
		ArrayList<StopAdapter> stops = GTFS.getCurrentStopWindow();

		HashSet<Node> stopSet = new HashSet<>();

		for (StopAdapter s : stops) {
			stopSet.add(new Node(s.getStop().get_stop_lat(), s.getStop()
					.get_stop_lon()));

			x = mParent.mapXToView(s.getStop().get_stop_lon()) + xOffset;
			y = mParent.mapYToView(s.getStop().get_stop_lat()) + yOffset;

			if (GraphFrame.getStopInfoVisible()
					&& (s.getStopTime() != null && (s.getStopTime()
							.get_timepoint() != null
							&& s.getStopTime().get_timepoint().equals("1") || (s
							.getStopTime().get_timepoint() != null
							&& !s.getStopTime().get_timepoint().equals("1") && s
							.getStopTime().getArrivalTimecode() > 0)))) {
				String output = s.getStop().getGTFS_ID() + "-"
						+ s.getRoute().getName();
				output += " " + s.getStop().get_stop_name() + "";
				output += ": " + s.getStopTime().get_arrival_time();
				g2d.drawString(output, x + 2, y - 2);
			}
		}

		// g2d.setFont( new Font("SansSerif", Font.BOLD, 8));
		for (Node s : stopSet.toArray(new Node[stopSet.size()])) {
			x = mParent.mapXToView(s.getLon()) + xOffset;
			y = mParent.mapYToView(s.getLat()) + yOffset;
			g2d.setColor(Color.green);
			g2d.fillOval(x - 2, y - 2, 5, 5);
			g2d.setColor(Color.white);
			g2d.drawOval(x - 2, y - 2, 5, 5);
			g2d.setColor(Color.gray);

		}

		ListIterator li = Vehicle.getVehicles().listIterator();

		g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));

		while (li.hasNext()) {
			Vehicle v = (Vehicle) li.next();
			if (v.isReady()) {
				x = mParent.mapXToView(v.getLon()) + xOffset;
				y = mParent.mapYToView(v.getLat()) + yOffset;
				g2d.setColor(Color.red);
				g2d.fillOval(x - 4, y - 4, 9, 9);
				g2d.setColor(Color.black);
				g2d.drawOval(x - 4, y - 4, 9, 9);
				g2d.setColor(Color.white);
				g2d.drawString(Integer.toString(v.getID()), x + 3, y - 3);
			}
		}
	}

	public void setPathImage(BufferedImage inImage) {
		pathImage = inImage;
	}

	public void setXOffset(int in) {
		xOffset = in;
	}

	public void setYOffset(int in) {
		yOffset = in;
	}

}
