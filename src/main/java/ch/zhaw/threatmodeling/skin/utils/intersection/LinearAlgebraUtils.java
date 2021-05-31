package ch.zhaw.threatmodeling.skin.utils.intersection;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * The spline intersection finding code (QuadraticSplineUtils) and its helper functions (LinearAlgebra- , GeometryUtils)
 * are based on a python implementation given to us by Dr. Stephan Neuhaus.
 * Date: 2021
 */
public class LinearAlgebraUtils {
    /**
     * Return the cosine of the angle ABC.
     * @param a Point a
     * @param b Point b
     * @param c Point c
     */
    public static double cosAngle(Vector2D a, Vector2D b, Vector2D c) {
        Vector2D ba = new Vector2D(b.getX() - a.getX(), b.getY() - a.getY());
        Vector2D bc = new Vector2D(c.getX() - b.getX(), c.getY() - b.getY());

        return ba.dotProduct(bc)/(ba.getNorm()*bc.getNorm());
    }

    /**
     * Return the point T of the way between A and B.
     *
     * @param a Point a
     * @param b Point b
     * @param t value in range (0,1)
     * @return
     */
    public static Vector2D ofTheWay(Vector2D a, Vector2D b, double t) {
       return new Vector2D((1-t)*a.getX() + t*b.getX(), (1-t)*a.getY() + t*b.getY());
    }


}
