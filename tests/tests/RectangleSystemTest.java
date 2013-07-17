package tests;

import static org.junit.Assert.assertEquals;

import java.awt.Rectangle;

import org.junit.Test;

import tendiwa.geometry.Directions;
import tendiwa.geometry.EnhancedRectangle;

public class RectangleSystemTest {

	
	@Test
	public void testGrowRectangle() {
		assertEquals(
				EnhancedRectangle.growFromPoint(0, 0, Directions.NE, 4, 7),
				new Rectangle(0, -7, 4, 7)
				);
		assertEquals(
				EnhancedRectangle.growFromPoint(0, 0, Directions.NW, 2, 1),
				new Rectangle(-2, -1, 2, 1)
				);
		assertEquals(
				EnhancedRectangle.growFromPoint(0, 0, Directions.SW, 2, 3),
				new Rectangle(-2, 0, 2, 3)
				);
		assertEquals(
				EnhancedRectangle.growFromPoint(0, 0, Directions.SE, 9, 3),
				new Rectangle(0, 0, 9, 3)
				);
	}
}
