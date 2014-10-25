package tests.geometry;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.tendiwa.core.Orientation;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Segment;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SegmentTest {

	/**
	 * Test iterating over a {@link Segment} as a collection of points: points
	 * should go as axis grows, and each point of a Segment should be iterated
	 * over.
	 */
	@Test
	public void testIterableVertical() {
		Segment segment = new Segment(4, 7, 3, Orientation.VERTICAL);
		List<Cell> cellsOfSegment = Lists.newArrayList(segment);
		List<Cell> equalCells = Arrays.asList(new Cell[]{
			new Cell(4, 7),
			new Cell(4, 8),
			new Cell(4, 9)
		});
		assertEquals(cellsOfSegment, equalCells);
	}

	/**
	 * @see SegmentTest#testIterableVertical()
	 */
	@Test
	public void testIterableHorizontal() {
		Segment segment = new Segment(8, 19, 5, Orientation.HORIZONTAL);
		List<Cell> cellsOfSegment = Lists.newArrayList(segment);
		List<Cell> equalCells = Arrays.asList(new Cell[]{
			new Cell(8, 19),
			new Cell(9, 19),
			new Cell(10, 19),
			new Cell(11, 19),
			new Cell(12, 19)
		});
		assertEquals(cellsOfSegment, equalCells);
	}
}
