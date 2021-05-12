package ch.zhaw.threatmodeling.skin.utils.intersection;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.*;

import java.util.Arrays;
import java.util.List;

/**
 * The spline intersection finding code (QuadraticSplineUtils) and its helper functions (LinearAlgebra- , GeometryUtils)
 * are based on a python implementation given to us by Dr. Stephan Neuhaus.
 * Date: 2021
 */
public class GeometryUtils {

    /**
     *  Return True if and only of the triangle A1, B1, C1 intersects
     *  the triangle A2, B2, C2.  NOTE: This may wrongly return False
     *  if one triangle is completely contained in the other, i.e.,
     *  without intersection.
     *
     * @param p1
     * @param p2
     * @param q1
     * @param q2
     * @return
     */
    public static boolean sameSide(double[] p1, double[] p2, double[] q1, double[] q2) {
        // Will probably not be used.
        Vector2D p = new Vector2D(p2[0] - p1[0], p2[1] - p1[1]);
        Vector2D q1s = new Vector2D(q1[0] - p1[0], q1[1] - p1[1]);
        Vector2D q2s = new Vector2D(q2[0] - p1[0], q2[1] - p1[1]);

        return Math.signum(Vector2D.ZERO.crossProduct(p, q1s)) == Math.signum(Vector2D.ZERO.crossProduct(p, q2s));
    }

    /**
     * Return the intersection point and the intersection times between
     * the line segments defined by P1, P2 and Q1 and Q2 if such an
     * intersection exists, None otherwise.
     *
     * @param p1 Point p1
     * @param p2 Point p2
     * @param q1 Point q1
     * @param q2 Point q1
     */
    public static RealMatrix intersectLineSegment(Vector2D p1, Vector2D p2, Vector2D q1, Vector2D q2) {
        // We solve 2x2 matrix Ax=b where A = 2x2 matrix "m" and b = 2x1 Vector "v"
        Vector2D row1 = new Vector2D(p2.getX() - p1.getX(), q1.getX() - q2.getX());
        Vector2D row2 = new Vector2D(p2.getY() - p1.getY(), q1.getY() - q2.getY());
        double[][] tempMatrix = {row1.toArray(), row2.toArray()};
        RealMatrix m = new Array2DRowRealMatrix(tempMatrix);

        Vector2D column = new Vector2D(q1.getX() - p1.getX(), q1.getY() - p1.getY());
        RealMatrix v = new Array2DRowRealMatrix(new double[][]{{column.getX()},{column.getY()}});

        DecompositionSolver solver = new QRDecomposition(m).getSolver();
        // Save the found intersection point if the linear system of equations can be solved, otherwise return null
        RealMatrix intersectionMatrix = null;
        if (solver.isNonSingular()) {
            // Solve m*x=v
            RealMatrix x = solver.solve(v);
            double v0 = x.getEntry(0, 0);
            double v1 = x.getEntry(1, 0);
            if (0 <= v0 && v0 <= 1 && 0 <= v1 && v1 <= 1) {
                double[][] result = new double[][]{{p1.getX() + v0 * (p2.getX() - p1.getX()),
                        p1.getY() + v0 * (p2.getY() - p1.getY())},
                        {v0, v1}};
                intersectionMatrix = new Array2DRowRealMatrix(result);

            }
        }
        return intersectionMatrix;
    }

    /**
     * Return the bounding box of an array of points. The bounding box is
     * defined by its lower left and upper right points. The lower
     * left point has as its coordinates the minimum coordinates of
     * the points in the array, and the upper right point has as its
     * coordinates the maximum coordinates of the points in the
     * array. The argument must not be the empty list or None.
     *
     * @param points The points of curve of which we want to know the bounding box.
     * @return its upper left and upper right bounding box coordinates.
     */
    public static RealMatrix boundingBox(List<Vector2D> points) {
       double lowerLeftX = points.get(0).getX();
       double lowerLeftY = points.get(0).getY();
       double upperRightX = points.get(0).getX();
       double upperRightY = points.get(0).getY();
       for (Vector2D point : points.subList(1,points.size())) {
           if (point.getX() < lowerLeftX) {
               lowerLeftX = point.getX();
           }
           if (point.getY() < lowerLeftY) {
               lowerLeftY = point.getY();
           }
           if (point.getX() > upperRightX) {
               upperRightX = point.getX();
           }
           if (point.getY() > upperRightY) {
               upperRightY = point.getY();
           }
       }
       return new Array2DRowRealMatrix(new double[][]{{lowerLeftX, lowerLeftY},{upperRightX, upperRightY}});
    }

    /**
     * Return True if and only if the two bounding boxes BB1 and BB2
     * do not intersect.
     *
     * @param boundingBox1
     * @param boundingBox2
     * @return
     */
    public static boolean separateBoundingBoxes(RealMatrix boundingBox1, RealMatrix boundingBox2) {
        return boundingBox1.getEntry(1,0) < boundingBox2.getEntry(0,0) ||
                boundingBox2.getEntry(1,0) < boundingBox1.getEntry(0,0) ||
                boundingBox1.getEntry(1,1) < boundingBox2.getEntry(0,1) ||
                boundingBox2.getEntry(1,1) < boundingBox1.getEntry(0,1);
    }

    /**
     * Return True if and only of the triangle A1, B1, C1 intersects
     * the triangle A2, B2, C2.  NOTE: This may wrongly return False
     * if one triangle is completely contained in the other, i.e.,
     * without intersection.
     *
     * @param a1 Point a1
     * @param b1 Point b1
     * @param c1 Point c1
     * @param a2 Point a2
     * @param b2 Point b2
     * @param c2 Point c2
     * @return
     */
    public static boolean trianglesIntersect(Vector2D a1, Vector2D b1, Vector2D c1, Vector2D a2, Vector2D b2, Vector2D c2) {
        // Will probably not be used.
        RealMatrix boundingBox1 = boundingBox(Arrays.asList(new Vector2D[]{a1, b1, c1}));
        RealMatrix boundingBox2 = boundingBox(Arrays.asList(new Vector2D[]{a2, b2, c2}));

        return separateBoundingBoxes(boundingBox1, boundingBox2) &&
                ((intersectLineSegment(a1, b1, a2, b2) != null) ||
                (intersectLineSegment(a1, b1, b2, c2) != null) ||
                (intersectLineSegment(a1, b1, c2, a2) != null) ||
                (intersectLineSegment(b1, c1, a2, b2) != null) ||
                (intersectLineSegment(b1, c1, b2, c2) != null) ||
                (intersectLineSegment(b1, c1, c2, a2) != null) ||
                (intersectLineSegment(c1, a1, a2, b2) != null) ||
                (intersectLineSegment(c1, a1, b2, c2) != null) ||
                (intersectLineSegment(c1, a1, c2, a2) != null));

    }
}
