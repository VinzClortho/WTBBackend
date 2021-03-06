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

package com.jasonlafrance.wtbbackend.kml_util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.jasonlafrance.wtbbackend.gtfs.Stop;
import com.jasonlafrance.wtbbackend.gtfs.StopAdapter;
import com.jasonlafrance.wtbbackend.gtfs.Trip;
import com.jasonlafrance.wtbbackend.gtfs.Vertex;

/**
 * Some quick and dirty KML export functions.
 * 
 * @author Jason LaFrance
 */
public class KMLExport {

	/**
	 * Generate KML for a given Trip list
	 * 
	 * @param trips
	 *            Trip list to export
	 * @return A String containing the KML data
	 */
	public static String generateTripKML(LinkedList<Trip> trips) {
		String output;

		output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
				+ "  <Document>\n" + "    <name>Paths</name>\n"
				+ "    <Style id=\"testStyle\">\n" + "      <LineStyle>\n"
				+ "        <color>80"
				+ trips.getFirst().getRoute().get_route_color() + "</color>\n"
				+ "        <width>4</width>\n" + "      </LineStyle>\n"
				+ "    </Style>\n";

		int lastStopID = -1;
		ArrayList<Vertex> points = new ArrayList<>();
		for (Trip t : trips) {
			for (StopAdapter s : t.getStops()) {
				if (s.getStop().getID() == lastStopID) {
					continue;
				}
				lastStopID = s.getStop().getID();
				String time = "";
				if (!"".equals(s.getStopTime().get_arrival_time())) {
					time = "(" + s.getStopTime().get_arrival_time() + ")";
				}
				output += "<Placemark>\n" + "    <name>"
						+ s.getStop().get_stop_name().replaceAll("&", "&amp;")
						+ " " + time + "</name>\n" + "    <description>"
						+ s.getStop().get_stop_desc().replaceAll("&", "&amp;")
						+ "</description>\n" + "    <Point>\n"
						+ "      <coordinates>" + s.getStop().get_stop_lon()
						+ "," + s.getStop().get_stop_lat()
						+ ",0</coordinates>\n" + "    </Point>\n"
						+ "  </Placemark>\n";
			}
			points.addAll(t.getVertices());
		}

		output += "    <Placemark>\n"
				+ "      <styleUrl>#testStyle</styleUrl>\n"
				+ "      <name>SoPo Bus Test</name>\n"
				+ "       <LineString id=\"route path\">\n"
				+ "        <extrude>1</extrude>\n" + "        <coordinates>\n";
		Vertex lastPoint = new Vertex();
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i).getDistance(lastPoint) == 0.0) {
				continue;
			}
			lastPoint = points.get(i);

			output += points.get(i).toKMLLine();
		}
		output += " </coordinates>\n" + "      </LineString>\n"
				+ "    </Placemark>\n";
		// footer
		output += "  </Document>\n" + "</kml>";
		return output;
	}

	/**
	 * Write a list of vertices to a file as KML
	 * 
	 * @param inFile
	 *            Name of the KML file to create
	 * @param points
	 *            A list of vertices
	 */
	public static void writeKML(String inFile, ArrayList<Vertex> points) {
		try (PrintWriter printWriter = new PrintWriter(inFile, "UTF-8")) {
			ArrayList<StopAdapter> stops = new ArrayList<>();

			printWriter.printf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
					+ "  <Document>\n" + "    <name>Paths</name>\n"
					+ "    <Style id=\"testStyle\">\n" + "      <LineStyle>\n"
					+ "        <color>7f00ffff</color>\n"
					+ "        <width>4</width>\n" + "      </LineStyle>\n"
					+ "    </Style>\n" + "    <Placemark>\n"
					+ "      <name>SoPo Bus Test</name>\n"
					+ "       <LineString id=\"ID\">\n"
					+ "        <coordinates>\n");
			for (int i = 0; i < points.size(); i++) {
				printWriter.printf(points.get(i).toKMLLine());
				// check if stop and add to stops list
				if (points.get(i).isStop()) {
					stops.add(points.get(i).getStop());
				}
			}
			printWriter.printf(" </coordinates>\n" + "      </LineString>\n"
					+ "    </Placemark>\n");
			// write the stops
			int lastStopID = -1;
			for (int i = 0; i < stops.size(); i++) {
				StopAdapter s = stops.get(i);
				if (s.getStop().getID() == lastStopID) {
					continue;
				}
				lastStopID = s.getStop().getID();
				String time = "";
				if (!"".equals(s.getStopTime().get_arrival_time())) {
					time = "(" + s.getStopTime().get_arrival_time() + ")";
				}
				printWriter.printf("<Placemark>\n" + "    <name>"
						+ s.getStop().get_stop_name().replaceAll("&", "&amp;")
						+ " " + time + "</name>\n" + "    <description>"
						+ s.getStop().get_stop_desc().replaceAll("&", "&amp;")
						+ "</description>\n" + "    <Point>\n"
						+ "      <coordinates>" + s.getStop().get_stop_lon()
						+ "," + s.getStop().get_stop_lat()
						+ ",0</coordinates>\n" + "    </Point>\n"
						+ "  </Placemark>\n");

			}

			printWriter.printf("  </Document>\n" + "</kml>");
		} catch (FileNotFoundException | UnsupportedEncodingException fileNotFoundException) {
		}

	}

	/**
	 * Write a list of Trips to a file as KML
	 * 
	 * @param inFile
	 *            Name of the KML file to create
	 * @param trips
	 *            A list of Trips
	 */
	public static void writeKML(String inFile, LinkedList<Trip> trips) {

		try (PrintWriter printWriter = new PrintWriter(inFile, "UTF-8")) {
			// header
			printWriter.printf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
					+ "  <Document>\n" + "    <name>Paths</name>\n"
					+ "    <Style id=\"testStyle\">\n" + "      <LineStyle>\n"
					+ "        <color>80"
					+ trips.getFirst().getRoute().get_route_color()
					+ "</color>\n" + "        <width>4</width>\n"
					+ "      </LineStyle>\n" + "    </Style>\n");

			int lastStopID = -1;
			ArrayList<Vertex> points = new ArrayList<>();
			for (Trip t : trips) {
				for (StopAdapter s : t.getStops()) {
					if (s.getStop().getID() == lastStopID) {
						continue;
					}
					lastStopID = s.getStop().getID();
					String time = "";
					if (!"".equals(s.getStopTime().get_arrival_time())) {
						time = "(" + s.getStopTime().get_arrival_time() + ")";
					}
					printWriter.printf("<Placemark>\n"
							+ "    <name>"
							+ s.getStop().get_stop_name()
									.replaceAll("&", "&amp;")
							+ " "
							+ time
							+ "</name>\n"
							+ "    <description>"
							+ s.getStop().get_stop_desc()
									.replaceAll("&", "&amp;")
							+ "</description>\n" + "    <Point>\n"
							+ "      <coordinates>"
							+ s.getStop().get_stop_lon() + ","
							+ s.getStop().get_stop_lat() + ",0</coordinates>\n"
							+ "    </Point>\n" + "  </Placemark>\n");
				}
				points.addAll(t.getVertices());
			}

			printWriter.printf("    <Placemark>\n"
					+ "      <styleUrl>#testStyle</styleUrl>\n"
					+ "      <name>Bus Test</name>\n"
					+ "       <LineString id=\"route path\">\n"
					+ "        <extrude>1</extrude>\n"
					+ "        <coordinates>\n");
			Vertex lastPoint = new Vertex();
			for (int i = 0; i < points.size(); i++) {
				if (points.get(i).getDistance(lastPoint) == 0.0) {
					continue;
				}
				lastPoint = points.get(i);

				printWriter.printf(points.get(i).toKMLLine());
			}
			printWriter.printf(" </coordinates>\n" + "      </LineString>\n"
					+ "    </Placemark>\n");
			// footer
			printWriter.printf("  </Document>\n" + "</kml>");
		} catch (FileNotFoundException | UnsupportedEncodingException fileNotFoundException) {
		}

	}
}
