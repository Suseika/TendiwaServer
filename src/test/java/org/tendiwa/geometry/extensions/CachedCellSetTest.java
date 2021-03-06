package org.tendiwa.geometry.extensions;

import org.junit.Assert;
import org.junit.Test;
import org.tendiwa.geometry.Rectangle;

import static org.tendiwa.geometry.GeometryPrimitives.rectangle;


public class CachedCellSetTest {
	@Test
	public void iterations() {
		Rectangle shape = rectangle(5, 5, 5, 5);
		CachedCellSet cells = new CachedCellSet(
			new ChebyshovDistanceBuffer(
				3,
				shape::contains
			),
			rectangle(15, 15)
		);
		Assert.assertEquals(96, cells.toSet().size());
	}

	@Test
	public void iterations2() {
		Rectangle shape = rectangle(5, 5, 5, 5);
		CachedCellSet cells = new CachedCellSet(
			new ChebyshovDistanceBuffer(
				5,
				shape::contains
			),
			rectangle(15, 15)
		);
		Assert.assertEquals(200, cells.toSet().size());
	}
}
