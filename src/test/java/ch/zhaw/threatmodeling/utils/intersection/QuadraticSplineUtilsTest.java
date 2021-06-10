package ch.zhaw.threatmodeling.utils.intersection;

import ch.zhaw.threatmodeling.skin.utils.intersection.QuadraticSplineUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class QuadraticSplineUtilsTest extends ApplicationTest {

    @Test
    public void testIntersectSplines() {
        Vector2D a1, b1, c1;
        a1 = new Vector2D(1,2);
        b1 = new Vector2D(-1,0);
        c1 = new Vector2D(1,-2);

        Vector2D a2, b2, c2;
        a2 = new Vector2D(-1,1);
        b2 = new Vector2D(0,-1);
        c2 = new Vector2D(1,1);

        // Single intersection
        List<Vector2D> intersections = QuadraticSplineUtils.intersectSplines(a1, b1, c1, a2, b2, c2);
        List<Vector2D> expectedIntersections = Arrays.asList(new Vector2D[]{new Vector2D(0, 0)});
        assertEquals(expectedIntersections, intersections);

        a1 = new Vector2D(1,2);
        b1 = new Vector2D(-1,0);
        c1 = new Vector2D(1,-2);

        a2 = new Vector2D(1,1.5);
        b2 = new Vector2D(-2,0);
        c2 = new Vector2D(1,-1.5);

        // Two intersections
        // (0.3,-1.09583)
        // (0.3,1.09583)
        intersections = QuadraticSplineUtils.intersectSplines(a1, b1, c1, a2, b2, c2);
        assertEquals(2, intersections.size());

        a1 = new Vector2D(1,2);
        b1 = new Vector2D(-4,0);
        c1 = new Vector2D(1,-2);

        a2 = new Vector2D(0.5,-3);
        b2 = new Vector2D(0,7);
        c2 = new Vector2D(-0.5,-3);

        // Four intersections
        // (-0.41594,-1.3188)
        // (-0.16552,1.46172)
        // (0.137801,1.62349)
        // (0.438048,-1.76096)
        intersections = QuadraticSplineUtils.intersectSplines(a1, b1, c1, a2, b2, c2);
        assertEquals(4, intersections.size());


        a1 = new Vector2D(0.5,-3);
        b1 = new Vector2D(0,7);
        c1 = new Vector2D(-0.5,-3);

        a2 = new Vector2D(0.6,0);
        b2 = new Vector2D(12,-2);
        c2 = new Vector2D(-1.5,1.25);

        // Two intersections at an acute angle
        // (-0.230561,0.944394)
        // (0.241935,0.830645)
        intersections = QuadraticSplineUtils.intersectSplines(a1, b1, c1, a2, b2, c2);
        assertEquals(2, intersections.size());
    }

    @Test
    public void testVector2DComparator() {
        Vector2D a1, b1, c1;
        a1 = new Vector2D(0,0);
        b1 = new Vector2D(1,1);
        c1 = new Vector2D(2,2);
        List<Vector2D> points = new ArrayList<>(Arrays.asList(new Vector2D[]{a1, b1, c1}));
        Collections.sort(points, Comparator.comparing(Vector2D::getX).thenComparing(Vector2D::getY));
        List<Vector2D> expected = new ArrayList<>(Arrays.asList(new Vector2D[]{a1, b1, c1}));
        assertEquals(expected, points);

        a1 = new Vector2D(0,0);
        b1 = new Vector2D(-1,2);
        c1 = new Vector2D(0,-3);
        points = new ArrayList<>(Arrays.asList(new Vector2D[]{a1, b1, c1}));
        Collections.sort(points, Comparator.comparing(Vector2D::getX).thenComparing(Vector2D::getY));
        expected = new ArrayList<>(Arrays.asList(new Vector2D[]{b1, c1, a1}));
        assertEquals(expected, points);

        a1 = new Vector2D(1,2);
        b1 = new Vector2D(1,2);
        c1 = new Vector2D(0,0);
        points = new ArrayList<>(Arrays.asList(new Vector2D[]{a1, b1, c1}));
        Collections.sort(points, Comparator.comparing(Vector2D::getX).thenComparing(Vector2D::getY));
        expected = new ArrayList<>(Arrays.asList(new Vector2D[]{c1, a1, b1}));
        assertEquals(expected, points);

    }
}
