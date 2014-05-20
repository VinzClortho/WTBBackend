package com.jasonlafrance.wtbbackend.wtb_util;

import com.jasonlafrance.wtbbackend.gtfs.Vertex;

/**
 *
 * @author Jason LaFrance
 */
public class VertexUtil {

    public static Vertex getLineSegmentIntersect(Vertex a, Vertex b, Vertex c) {

        final double xD = b.get_shape_pt_lat() - a.get_shape_pt_lat();
        final double yD = b.get_shape_pt_lon() - a.get_shape_pt_lon();
        final double u = ((c.get_shape_pt_lat() - a.get_shape_pt_lat()) * xD + (c.get_shape_pt_lon() - a.get_shape_pt_lon()) * yD) / (xD * xD + yD * yD);
        final Vertex intersect;

        if (u < 0) {
            intersect = a;
        } else if (u > 1) {
            intersect = b;
        } else {
            intersect = new Vertex(a.get_shape_pt_lat() + u * xD, a.get_shape_pt_lon() + u * yD);
        }

        return intersect;
    }
    /*
     public void removeRedundantVertices(LinkedList<LinkedList<Trip>> paths) {
     if (mVertices.isEmpty()) {
     return;
     }

     LinkedList<Vertex> newVertices = new LinkedList<>();
     ListIterator li = mVertices.listIterator();
     Vertex last = (Vertex) li.next();
     newVertices.add(last);
     Vertex current;
     while (li.hasNext()) {
     current = (Vertex) li.next();
     if (last.get_shape_pt_lat() != current.get_shape_pt_lat() && last.get_shape_pt_lon() != current.get_shape_pt_lon()) {
     newVertices.add(current);
     }
     last = current;
     }
     mVertices = newVertices;
     }
     */
}
