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
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ListIterator;
import java.awt.BorderLayout;
import java.util.HashSet;

import com.jasonlafrance.wtbbackend.gtfs.RoutePath;
import com.jasonlafrance.wtbbackend.gtfs.Trip;
import com.jasonlafrance.wtbbackend.gtfs.Vertex;
import com.jasonlafrance.wtbbackend.wtb_util.Graph.Edge;
import com.jasonlafrance.wtbbackend.wtb_util.Graph.Node;

/**
 * 
 * @author Jason LaFrance
 */
public class GraphFrame extends javax.swing.JFrame implements ComponentListener {

	private final HashSet<Edge> mEdges = new HashSet<>();

	/**
	 * Creates new form GraphFrame
	 */
	private double mScale;
	private double mXMin;
	private double mXMax;
	private double mYMin;
	private double mYMax;
	private int maxWidth;
	private int maxHeight;
	private double mMoveIncrement;

	private int width, height;

	private ArrayList<RoutePath> mPaths;
	private BufferedImage mPathImage;

	private final GraphPanel gPanel;

	private static boolean mStopInfoSelected = false;

	public static boolean getStopInfoVisible() {
		return mStopInfoSelected;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel MovePanel;

	private javax.swing.JPanel ViewControlPanel;

	private javax.swing.JPanel ZoomPanel;

	private javax.swing.JButton mMoveEast;

	private javax.swing.JButton mMoveNorth;

	private javax.swing.JButton mMoveSouth;

	private javax.swing.JButton mMoveWest;

	private javax.swing.JCheckBox mShowStopInfo;

	private javax.swing.JButton mZoomIn;

	private javax.swing.JButton mZoomOut;

	public GraphFrame() {
		initComponents();

		// mNavPanel.setSize(100, this.height);
		mXMin = Double.NaN;
		mXMax = Double.NaN;
		mYMin = Double.NaN;
		mYMax = Double.NaN;

		mPaths = new ArrayList<>();

		this.setSize(1000, 600);
		gPanel = new GraphPanel(this);
		this.add(gPanel, BorderLayout.CENTER);
		this.addComponentListener(this);
	}

	public synchronized void addPaths(ArrayList<RoutePath> inPaths) {
		System.out.println("GraphFrame: adding paths...");
		if (mPaths.addAll(inPaths)) {

			buildEdges();
			updatePathImage();
		}
	}

	private void adjustMovement() {
		maxWidth = (int) (Math.abs(mXMax - mXMin) * mScale);
		maxHeight = (int) (Math.abs(mYMax - mYMin) * mScale);
		mMoveIncrement = 10 / mScale;
		// gPanel.setXOffset(Math.abs(width - maxWidth));
		// gPanel.setYOffset(Math.abs(height - maxHeight));
		updatePathImage();

	}

	private void adjustViewport() {
		width = gPanel.getWidth();
		height = gPanel.getHeight();
		mScale = Math.min(width / Math.abs(mXMax - mXMin),
				height / Math.abs(mYMax - mYMin));

		maxWidth = (int) (Math.abs(mXMax - mXMin) * mScale);
		maxHeight = (int) (Math.abs(mYMax - mYMin) * mScale);

		// gPanel.setXOffset(Math.abs(width - maxWidth));
		// gPanel.setYOffset(Math.abs(height - maxHeight));
	}

	public void adjustViewport(double xMin, double yMin, double xMax,
			double yMax) {

		if (Double.isNaN(mXMin) || mXMin > xMin) {
			mXMin = xMin;
		}
		if (Double.isNaN(mXMax) || mXMax < xMax) {
			mXMax = xMax;
		}
		if (Double.isNaN(mYMin) || mYMin > yMin) {
			mYMin = yMin;
		}
		if (Double.isNaN(mYMax) || mYMax < yMax) {
			mYMax = yMax;
		}
		adjustViewport();
	}

	private void buildEdges() {
		mEdges.clear();

		System.out.println("BuildEdges...");

		if (mPaths == null) {
			return;
		}

		ListIterator li = mPaths.listIterator();

		while (li.hasNext()) {
			RoutePath rp = (RoutePath) li.next();

			String color = rp.getColor();

			ListIterator ti = rp.getPath().listIterator();
			Node last = null;

			while (ti.hasNext()) {
				Trip t = (Trip) ti.next();
				ListIterator vi = t.getVertices().listIterator();
				while (vi.hasNext()) {
					Vertex v = (Vertex) vi.next();
					Node cur = new Node(v.get_shape_pt_lat(),
							v.get_shape_pt_lon());
					if (last != null) {
						// add edge
						Edge e = new Edge(last, cur);
						e.setColor(color);
						mEdges.add(e);
					}
					last = cur;
				}
			}
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	// End of variables declaration//GEN-END:variables
	@Override
	public void componentResized(ComponentEvent e) {
		// gPanel.setSize(this.getSize());
		adjustViewport();
		updatePathImage();
	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	private void getStopInfoSelected() {
		mStopInfoSelected = mShowStopInfo.isSelected();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		ViewControlPanel = new javax.swing.JPanel();
		ZoomPanel = new javax.swing.JPanel();
		mZoomIn = new javax.swing.JButton();
		mZoomOut = new javax.swing.JButton();
		MovePanel = new javax.swing.JPanel();
		mMoveWest = new javax.swing.JButton();
		mMoveEast = new javax.swing.JButton();
		mMoveNorth = new javax.swing.JButton();
		mMoveSouth = new javax.swing.JButton();
		mShowStopInfo = new javax.swing.JCheckBox();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("WTB Backend Debug");

		ViewControlPanel
				.setMaximumSize(new java.awt.Dimension(200, 2147483647));
		ViewControlPanel.setLayout(new java.awt.BorderLayout());

		mZoomIn.setText("Zoom In");
		mZoomIn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mZoomInActionPerformed(evt);
			}
		});
		ZoomPanel.add(mZoomIn);

		mZoomOut.setText("Zoom Out");
		mZoomOut.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mZoomOutActionPerformed(evt);
			}
		});
		ZoomPanel.add(mZoomOut);

		ViewControlPanel.add(ZoomPanel, java.awt.BorderLayout.WEST);

		mMoveWest.setText("West");
		mMoveWest.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mMoveWestActionPerformed(evt);
			}
		});
		MovePanel.add(mMoveWest);

		mMoveEast.setText("East");
		mMoveEast.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mMoveEastActionPerformed(evt);
			}
		});
		MovePanel.add(mMoveEast);

		mMoveNorth.setText("North");
		mMoveNorth.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mMoveNorthActionPerformed(evt);
			}
		});
		MovePanel.add(mMoveNorth);

		mMoveSouth.setText("South");
		mMoveSouth.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mMoveSouthActionPerformed(evt);
			}
		});
		MovePanel.add(mMoveSouth);

		ViewControlPanel.add(MovePanel, java.awt.BorderLayout.EAST);

		mShowStopInfo.setText("Show Stop Info");
		mShowStopInfo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mShowStopInfoActionPerformed(evt);
			}
		});
		ViewControlPanel.add(mShowStopInfo, java.awt.BorderLayout.CENTER);

		getContentPane()
				.add(ViewControlPanel, java.awt.BorderLayout.PAGE_START);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	public int mapXToView(double inX) {
		return (int) ((inX - mXMin) * mScale);
	}

	public int mapYToView(double inY) {
		return gPanel.getHeight() - (int) ((inY - mYMin) * mScale);
	}

	private void mMoveEastActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mMoveEastActionPerformed
		mXMin += mMoveIncrement;
		mXMax += mMoveIncrement;
		adjustMovement();
	}// GEN-LAST:event_mMoveEastActionPerformed

	private void mMoveNorthActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mMoveNorthActionPerformed
		mYMin += mMoveIncrement;
		mYMax += mMoveIncrement;
		adjustMovement();
	}// GEN-LAST:event_mMoveNorthActionPerformed

	private void mMoveSouthActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mMoveSouthActionPerformed
		mYMin -= mMoveIncrement;
		mYMax -= mMoveIncrement;
		adjustMovement();
	}// GEN-LAST:event_mMoveSouthActionPerformed

	private void mMoveWestActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mMoveWestActionPerformed
		mXMin -= mMoveIncrement;
		mXMax -= mMoveIncrement;
		adjustMovement();
	}// GEN-LAST:event_mMoveWestActionPerformed

	private void mShowStopInfoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mShowStopInfoActionPerformed
		getStopInfoSelected();
	}// GEN-LAST:event_mShowStopInfoActionPerformed

	private void mZoomInActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mZoomInActionPerformed
		mScale *= 1.1;
		adjustMovement();

	}// GEN-LAST:event_mZoomInActionPerformed

	private void mZoomOutActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mZoomOutActionPerformed
		mScale *= 0.9;
		adjustMovement();
	}// GEN-LAST:event_mZoomOutActionPerformed

	public synchronized void setPaths(ArrayList<RoutePath> inPaths) {
		mPaths = inPaths;
		buildEdges();
		updatePathImage();
	}

	private synchronized void updatePathImage_Old() {
		if (gPanel.getWidth() == 0 || gPanel.getHeight() == 0) {
			return;
		}
		mPathImage = new BufferedImage(gPanel.getWidth(), gPanel.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = mPathImage.createGraphics();

		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, gPanel.getWidth(), gPanel.getHeight());

		if (mPaths == null) {
			return;
		}

		ListIterator li = mPaths.listIterator();

		while (li.hasNext()) {
			RoutePath rp = (RoutePath) li.next();

			g2d.setColor(Color.decode("#" + rp.getColor()));
			// g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
			// 0.5f));

			ListIterator ti = rp.getPath().listIterator();
			int lastX = -1;
			int lastY = -1;
			while (ti.hasNext()) {
				Trip t = (Trip) ti.next();
				ListIterator vi = t.getVertices().listIterator();
				while (vi.hasNext()) {
					Vertex v = (Vertex) vi.next();
					System.out.println("GraphFrame: " + v);
					int x = mapXToView(v.get_shape_pt_lon());
					int y = mapYToView(v.get_shape_pt_lat());
					if (lastX != -1) {
						g2d.drawLine(lastX, lastY, x, y);
					}
					lastX = x;
					lastY = y;
				}
			}
		}
		gPanel.setPathImage(mPathImage);

		this.repaint();
	}

	private synchronized void updatePathImage() {
		if (gPanel.getWidth() == 0 || gPanel.getHeight() == 0) {
			return;
		}
		mPathImage = new BufferedImage(gPanel.getWidth(), gPanel.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = mPathImage.createGraphics();

		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, gPanel.getWidth(), gPanel.getHeight());

		if (mPaths == null) {
			return;
		}

		for (Edge e : mEdges.toArray(new Edge[mEdges.size()])) {
			g2d.setColor(Color.decode("#" + e.getColor()));

			int aX = mapXToView(e.getA().getLon());
			int aY = mapYToView(e.getA().getLat());
			int bX = mapXToView(e.getB().getLon());
			int bY = mapYToView(e.getB().getLat());
			g2d.drawLine(aX, aY, bX, bY);

		}

		gPanel.setPathImage(mPathImage);

		this.repaint();
	}
}
