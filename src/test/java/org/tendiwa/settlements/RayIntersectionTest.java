package org.tendiwa.settlements;

import org.junit.Test;
import org.tendiwa.geometry.ParallelSegment;
import org.tendiwa.geometry.RayIntersection;
import org.tendiwa.geometry.Segment2D;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public class RayIntersectionTest {
	@Test
	public void segmentsDontIntersectWithEndPoints() {
		Segment2D a = segment2D(0, 0, 4, 0);
		Segment2D b = segment2D(4, 0, 8, 0);
		assertFalse(new RayIntersection(a, b).segmentsIntersect());
	}

	@Test
	public void segmentsDontIntersect() {
		Segment2D a = segment2D(0, 0, 4, 0);
		Segment2D b = segment2D(2, -4, 2, -2);
		assertFalse(new RayIntersection(a, b).segmentsIntersect());
	}

	@Test
	public void segmentsIntersect() {
		Segment2D a = segment2D(0, 0, 4, 0);
		Segment2D b = segment2D(2, -2, 2, 2);
		assertTrue(new RayIntersection(a, b).segmentsIntersect());
	}

	@Test
	public void linesIntersectWhenSegmentsDoNot() {
		Segment2D a = segment2D(0, 0, 4, 1);
		Segment2D b = segment2D(3, 5, 5, 2);
		RayIntersection intersection = new RayIntersection(a, b);
		assertTrue(intersection.intersects);
		assertTrue(!intersection.segmentsIntersect());
	}

	@Test
	public void parallelDontIntersect() {
		Segment2D a = segment2D(0, 0, 4, 0);
		Segment2D b = new ParallelSegment(
			a,
			1,
			true
		);
		RayIntersection intersection = new RayIntersection(a, b);
		assertFalse(intersection.intersects);
	}
}
