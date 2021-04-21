package ch.zhaw.threatmodeling.utils.intersection;

import ch.zhaw.threatmodeling.skin.utils.intersection.LinearAlgebraUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class LinearAlgebraUtilsTest extends ApplicationTest {
    private static final double EPS = 1e-4;

    @Test
    public void testCosAngle() {
        Vector2D a, b, c;
        a = new Vector2D(0.5, -3);
        b = new Vector2D(0, 7);
        c = new Vector2D(-0.5, -3);
        final double result = LinearAlgebraUtils.cosAngle(a, b, c);
        final double expected = -0.99501246882793;
        assertTrue((expected - EPS < result) && (result < expected + EPS));
    }

   @Test
   public void testOfTheWay() {
       Vector2D a, b;
       a = new Vector2D(1, 2);
       b = new Vector2D(-1, 0);
       double t = 0.5;

       Vector2D expected = new Vector2D(0, 1);
       assertEquals(expected, LinearAlgebraUtils.ofTheWay(a, b, t));
   }

}
