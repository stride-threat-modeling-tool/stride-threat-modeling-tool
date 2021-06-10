package ch.zhaw.threatmodeling.utils.intersection;

import ch.zhaw.threatmodeling.skin.utils.intersection.GeometryUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeometryUtilsTest extends ApplicationTest {

    @Test
    public void testIntersectLinesegment() {
        Vector2D p1 = new Vector2D(1.0,2.0);
        Vector2D p2 = new Vector2D(0.875,1.875);
        Vector2D q1 = new Vector2D(-1.0,1.0);
        Vector2D q2 = new Vector2D(-0.9375,0.875);
        RealMatrix result = GeometryUtils.intersectLineSegment(p1,p2,q1,q2);
        assertNull(result);

        p1 = new Vector2D(0.0,0.0625);
        p2 = new Vector2D(0.0,0.0);
        q1 = new Vector2D(-0.015625,0.0);
        q2 = new Vector2D(0.0,0.0);
        result = GeometryUtils.intersectLineSegment(p1,p2,q1,q2);
        RealMatrix expected = new Array2DRowRealMatrix(new double[][]{{0, 0}, {1, 1}});
        assertEquals(expected, result);
    }

    @Test
    public void testBoundingBox() {
        Vector2D p1, p2, p3;
        p1 = new Vector2D(1,2);
        p2 = new Vector2D(-1,0);
        p3 = new Vector2D(1,-2);
        List<Vector2D> points = new ArrayList<>(Arrays.asList(p1, p2, p3));
        RealMatrix result = GeometryUtils.boundingBox(points);
        RealMatrix expected = new Array2DRowRealMatrix(new double[][]{{-1,-2},{1,2}});
        assertEquals(expected, result);

        p1 = new Vector2D(-1,1);
        p2 = new Vector2D(0,-1);
        p3 = new Vector2D(1,1);
        points = new ArrayList<>(Arrays.asList(p1, p2, p3));
        result = GeometryUtils.boundingBox(points);
        expected = new Array2DRowRealMatrix(new double[][]{{-1,-1},{1,1}});
        assertEquals(expected, result);
    }

    @Test
    public void testSeparateBoundingBoxes() {
        // Two intersecting bounding boxes
        RealMatrix boundingBox1 = new Array2DRowRealMatrix(new double[][]{{-1,-2},{1,2}});
        RealMatrix boundingBox2 = new Array2DRowRealMatrix(new double[][]{{-1,-1},{1,1}});
        assertFalse(GeometryUtils.separateBoundingBoxes(boundingBox1, boundingBox2));
        assertEquals(false, GeometryUtils.separateBoundingBoxes(boundingBox1, boundingBox2));

        // Two separate bounding boxes
        boundingBox1 = new Array2DRowRealMatrix(new double[][]{{-1,-2},{1,2}});
        boundingBox2 = new Array2DRowRealMatrix(new double[][]{{-3,3},{3,6}});
        assertTrue(GeometryUtils.separateBoundingBoxes(boundingBox1, boundingBox2));
    }
}
