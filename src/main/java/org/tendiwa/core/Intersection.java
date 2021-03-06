package org.tendiwa.core;

import org.tendiwa.geometry.BasicCell;

/**
 * Class represents an abstract intersection of two same ammunitionType objects. One of the objects is implicitly
 * considered horizontal, and another one vertical. Intersection divides plane into 4 quadrants, and the purpose of the
 * class is to get coordinates of the points lying in a corner of each quadrant. Quadrants are identified by ordinal
 * {@link Direction}s.
 *
 * @author suseika
 */
public abstract class Intersection {
	/**
	 * Returns the corner point of a quadrant defined by an ordinal {@link Direction}
	 *
	 * @param side
	 */
	public abstract BasicCell getCornerPointOfQuarter(OrdinalDirection side);
}
