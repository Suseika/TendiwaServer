package org.tendiwa.drawing.extensions;

import org.tendiwa.core.Orientation;
import org.tendiwa.drawing.DrawingAlgorithm;

import java.awt.*;

@SuppressWarnings("unused")
public class DrawingInteger {
	/**
	 * Draws a vertical or horizontal line over the whole TestCanvas with a given static coordinate.
	 *
	 * @param orientation
	 * 	Orientation of a line drawn.
	 * @param color
	 * 	Color of line
	 * @return DrawingAlgorithm that draws that line when given an Integer to drawWorld.
	 */
	public static DrawingAlgorithm<Integer> byOrientation(final Orientation orientation, final Color color) {
		return (value, canvas) -> {
			int startX = orientation.isHorizontal() ? 0 : value;
			int startY = orientation.isVertical() ? 0 : value;
			int width = orientation.isHorizontal() ? canvas.getWidth() : 1;
			int height = orientation.isVertical() ? canvas.getHeight() : 1;
			canvas.drawRectangle(
				new org.tendiwa.geometry.Rectangle(startX, startY, width, height),
				color
			);
		};
	}

	public static DrawingAlgorithm<Integer> byYAxis() {
		return byOrientation(Orientation.HORIZONTAL, Color.RED);
	}

	public static DrawingAlgorithm<Integer> byXAxis() {
		return byOrientation(Orientation.VERTICAL, Color.RED);
	}
}
