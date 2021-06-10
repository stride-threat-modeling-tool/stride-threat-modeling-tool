package ch.zhaw.threatmodeling.skin.utils.intersection;

import ch.zhaw.threatmodeling.skin.connection.DataFlowConnectionSkin;
import ch.zhaw.threatmodeling.skin.connection.TrustBoundaryConnectionSkin;
import javafx.scene.Group;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;
import java.util.logging.Logger;

/**
 * The spline intersection finding code (QuadraticSplineUtils) and its helper functions (LinearAlgebra- , GeometryUtils)
 * are based on a python implementation given to us by Dr. Stephan Neuhaus.
 * Date: 2021
 */
public class QuadraticSplineUtils {

    private QuadraticSplineUtils(){}

    //Maximum angle change between line segments in a spline is at most this # number of degrees.
    private static final double ANGLE_EPS = 5.0;

    private static final Logger LOGGER =  Logger.getLogger("Quadratic Spline Utils");

    /**
     * Subdivide the spline with control points A, B, and C into line
     * segments so that the change in direction between adjacent line
     * segments is less than EPS degrees. Return the list of lne
     * segments.
     * @param a Point a
     * @param b Point b
     * @param c Point c
     * @param eps
     */
    private static List<Vector2D> subdivideSpline(Vector2D a, Vector2D b, Vector2D c, double eps) {
        /* In order for an angle to be less than eps degrees, the cosine of
         * the angle between the vectors has to be close to 0.
         */
        final double cosEps = Math.cos(eps*Math.PI / 180.0);

        List<Vector2D> currentPoints = new ArrayList<>(Arrays.asList(a, b, c));

        boolean allAnglesOk = false;
        while (!allAnglesOk) {
            // Not all angles small enough. Looking for subdivisions.

            List<Vector2D> newPoints = new ArrayList<Vector2D>();
            int i = 0;
            allAnglesOk = true;

            while (i < currentPoints.size() - 2) {
                if (LinearAlgebraUtils.cosAngle(currentPoints.get(i), currentPoints.get(i + 1), currentPoints.get(i + 2)) < cosEps) {
                    allAnglesOk = false;
                    Vector2D a1 = LinearAlgebraUtils.ofTheWay(currentPoints.get(i + 0), currentPoints.get(i + 1), 0.5);
                    Vector2D b1 = LinearAlgebraUtils.ofTheWay(currentPoints.get(i + 1), currentPoints.get(i + 2), 0.5);
                    Vector2D newPoint = LinearAlgebraUtils.ofTheWay(a1, b1, 0.5);

                    if (i == 0) {
                        newPoints.add(currentPoints.get(i + 0));
                    }
                    newPoints.add(a1);
                    newPoints.add(newPoint);
                    newPoints.add(b1);
                    newPoints.add(currentPoints.get(i + 2));
                } else {
                    if (i == 0) {
                        newPoints.add(currentPoints.get(i + 0));
                    }
                    newPoints.add(currentPoints.get(i + 1));
                    newPoints.add(currentPoints.get(i + 2));
                }
                i += 2;
            }
            currentPoints = newPoints;
        }

        return currentPoints;
    }

    /**
     * Intersect the two quadratic splines given by A0, B1, C1, and A2,
     * B1, C2, respectively, and return a list of points with their
     * intersections. There can from zero to four intersections. If
     * the splines do not intersect, return None.
     *
     * @param a1 Point a1
     * @param b1 Point b1
     * @param c1 Point c1
     * @param a2 Point a2
     * @param b2 Point b2
     * @param c2 Point c2
     * @return
     */
    public static List<Vector2D> intersectSplines(Vector2D a1, Vector2D b1, Vector2D c1, Vector2D a2, Vector2D b2, Vector2D c2) {
        RealMatrix boundingBox1 = GeometryUtils.boundingBox(Arrays.asList(a1, b1, c1));
        RealMatrix boundingBox2 = GeometryUtils.boundingBox(Arrays.asList(a2, b2, c2));

        if (GeometryUtils.separateBoundingBoxes(boundingBox1, boundingBox2)) {
            return null;
        }

        List<Vector2D> lineSegments1 = subdivideSpline(a1, b1, c1, ANGLE_EPS);
        List<Vector2D> lineSegments2 = subdivideSpline(a2, b2, c2, ANGLE_EPS);

        List<Vector2D> intersections = new ArrayList<Vector2D>();

        int i = 0;
        while (i < lineSegments1.size() - 1) {

           int j = 0;
           while (j < lineSegments2.size() - 1) {
               RealMatrix point = GeometryUtils.intersectLineSegment(lineSegments1.get(i), lineSegments1.get(i+1),
                                                                       lineSegments2.get(j), lineSegments2.get(j+1));

               if (point != null) {
                   intersections.add(new Vector2D(point.getEntry(0,0), point.getEntry(0,1)));
               }
               j += 1;
           }
           i += 1;
        }

        if (intersections.size() == 0) {
            return null;
        }

        /**
         * Eliminate duplicates. Duplicates can occur if an intersection is
         * exactly at the end of a line segment, which is also the beginning
         * of the next line segment
         */
        Collections.sort(intersections, Comparator.comparing(Vector2D::getX).thenComparing(Vector2D::getY));
        List<Vector2D> intersectionsWithoutDuplicates = new ArrayList<>();

        i = 0;
        while (i < intersections.size()) {
            intersectionsWithoutDuplicates.add(intersections.get(i));

            int j = i + 1;
            while (j < intersections.size() && (intersections.get(i).equals(intersections.get(j)))) {
                j += 1;
            }
            i = j;
        }
        return intersectionsWithoutDuplicates;
    }

    /**
     * Checks if an intersection exists between a dataflow connection and a trust boundary.
     *
     * @param dataflowConnection
     * @param trustBoundaryConnection
     * @return null or the List of intersections
     */
    public static boolean checkIntersection(DataFlowConnectionSkin dataflowConnection, TrustBoundaryConnectionSkin trustBoundaryConnection) {
        // Get the 3 defining points from each connection
        Group root1 = (Group) dataflowConnection.getRoot();
        Path path1 = (Path) root1.getChildren().get(0);
        MoveTo moveTo1 = (MoveTo) path1.getElements().get(0);
        QuadCurveTo quadCurveTo1 = (QuadCurveTo) path1.getElements().get(1);

        Group root2 = (Group) trustBoundaryConnection.getRoot();
        Path path2 = (Path) root2.getChildren().get(0);
        MoveTo moveTo2 = (MoveTo) path2.getElements().get(0);
        QuadCurveTo quadCurveTo2 = (QuadCurveTo) path2.getElements().get(1);

        Vector2D dataFlowStartPoint = new Vector2D(moveTo1.getX(), moveTo1.getY());
        Vector2D dataFlowEndPoint = new Vector2D(quadCurveTo1.getX(), quadCurveTo1.getY());
        Vector2D dataFlowControlPoint = new Vector2D(quadCurveTo1.getControlX(), quadCurveTo1.getControlY());

        Vector2D trustBoundaryStartPoint = new Vector2D(moveTo2.getX(), moveTo2.getY());
        Vector2D trustBoundaryEndPoint = new Vector2D(quadCurveTo2.getX(), quadCurveTo2.getY());
        Vector2D trustBoundaryControlPoint = new Vector2D(quadCurveTo2.getControlX(), quadCurveTo2.getControlY());

        // Find the intersections
        List<Vector2D> intersections = intersectSplines(dataFlowStartPoint,
                dataFlowControlPoint,
                dataFlowEndPoint,
                trustBoundaryStartPoint,
                trustBoundaryControlPoint,
                trustBoundaryEndPoint);

        if (intersections != null) {
            LOGGER.info("Found " + intersections.size() + " intersections.");
            for (Vector2D intersection : intersections) {
                LOGGER.info("Intersection: " + intersection.toString());
            }
        }
        return intersections != null;
    }
}
