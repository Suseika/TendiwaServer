package tests;

import static org.hamcrest.CoreMatchers.*;

import java.awt.Point;

import org.junit.Assert;
import org.junit.Test;

import org.tendiwa.geometry.Cell;
import org.tendiwa.core.Directions;
import org.tendiwa.core.Orientation;

public class EnhancedPointTest extends Assert {

	@Test
	public void testFromStaticAndDynamic() {
		assertEquals(
			Cell.fromStaticAndDynamic(7, 9, Orientation.VERTICAL),
			new Point(7, 9));
		assertEquals(
			Cell.fromStaticAndDynamic(7, 9, Orientation.HORIZONTAL),
			new Point(9, 7));
	}
	@Test
	public void testMove() throws Exception {
		Cell point = new Cell(6, 7)
			.moveToSide(Directions.E)
			.moveToSide(Directions.NW)
			.moveToSide(Directions.SW);
		assertEquals(point, new Point(5, 7));
		assertThat(point, is(not(new Cell(4,7))));
	}

}
