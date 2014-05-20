package com.jasonlafrance.wtbbackend.wtb_util;

import java.util.HashSet;

/**
 *
 * @author Jason LaFrance
 */
public class Graph {

    public static class Node {

        private final double nLat, nLon;
        private final HashSet<Node> nLinks;
        private final int nHashcode;

        public Node(double inLat, double inLon) {
            nLat = inLat;
            nLon = inLon;
            nLinks = new HashSet<>();
            nHashcode = (int) (Double.doubleToRawLongBits(nLat) * 31 + Double.doubleToRawLongBits(nLon));
        }

        public double getLat() {
            return nLat;
        }

        public double getLon() {
            return nLon;
        }

        public void addLink(Node in) {
            nLinks.add(in);
        }

        public Node[] getLinks() {
            return nLinks.toArray(new Node[nLinks.size()]);
        }

        @Override
        public int hashCode() {
            return nHashcode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Node other = (Node) obj;
            if (this.nHashcode != other.nHashcode) {
                return false;
            }
            return true;
        }
    }

    public static class Edge {

        private final Node A, B;
        private final int eHashcode;
        private String eColor;

        public Edge(Node a, Node b) {
            if (a.hashCode() < b.hashCode()) {
                A = a;
                B = b;
            } else {
                A = b;
                B = a;
            }
            eHashcode = A.hashCode() * 3131 + B.hashCode();
        }

        public Node getA() {
            return A;
        }

        public Node getB() {
            return B;
        }

        public void setColor(String in) {
            eColor = in;
        }

        public String getColor() {
            return eColor;
        }

        @Override
        public int hashCode() {
            return eHashcode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Edge other = (Edge) obj;
            if (this.eHashcode != other.eHashcode) {
                return false;
            }
            return true;
        }

    }
}
