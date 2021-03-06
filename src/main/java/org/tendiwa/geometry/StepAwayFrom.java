package org.tendiwa.geometry;

import org.tendiwa.core.CardinalDirection;

public final class StepAwayFrom {
	private final RectanglePointer pointer;

	StepAwayFrom(RectanglePointer pointer) {
		this.pointer = pointer;
	}

	public StepAwayFromFromSide fromSide(CardinalDirection side) {
		return new StepAwayFromFromSide(pointer, side);
	}
}
