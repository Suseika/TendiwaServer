package org.tendiwa.geometry;

import org.tendiwa.core.OrdinalDirection;

import java.util.Objects;

public interface Rectangle2D extends RectangularHull {
	double x();

	double y();

	double width();

	double height();

	default double getMaxX() {
		return x() + width();
	}

	default double getMaxY() {
		return y() + height();
	}

	/**
	 * Checks if a rectangle intersects segment.
	 *
	 * @param segment
	 * 	A segment.
	 * @return true if some part of {@code segment} lies inside {@code rectangle}, false otherwise.
	 * @see <a href="http://stackoverflow.com/a/293052/1542343">How to test if a line segment intersects an
	 * axis-aligned rectange in 2D</a>
	 */
	default boolean intersectsSegment(Segment2D segment) {
		double pointPosition = pointRelativeToLine(x(), y(), segment);
		do {
			if (Math.abs(pointPosition) < Vectors2D.EPSILON) {
				break;
			}
			double newPointPosition = pointRelativeToLine(getMaxX(), y(), segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			newPointPosition = pointRelativeToLine(x(), getMaxY(), segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			newPointPosition = pointRelativeToLine(getMaxX(), getMaxY(), segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			return false;
		} while (false);
		double segmentBoundsMin;
		double segmentBoundsMax;
		if (segment.start().x() < segment.end().x()) {
			segmentBoundsMin = segment.start().x();
			segmentBoundsMax = segment.end().x();
		} else {
			segmentBoundsMin = segment.end().x();
			segmentBoundsMax = segment.start().x();
		}
		if (segmentBoundsMax < x() || segmentBoundsMin > getMaxX()) {
			return false;
		}
		if (segment.start().y() < segment.end().y()) {
			segmentBoundsMin = segment.start().y();
			segmentBoundsMax = segment.end().y();
		} else {
			segmentBoundsMin = segment.end().y();
			segmentBoundsMax = segment.start().y();
		}
		if (segmentBoundsMax < y() || segmentBoundsMin > getMaxY()) {
			return false;
		}
		return true;
	}

	/**
	 * @param x
	 * 	X-coordinate of a point.
	 * @param y
	 * 	X-coordinat of a point.
	 * @param segment
	 * 	A segment.
	 * @return > 0 if point is below line, < 0 if point is above line, 0 if point is on line.
	 */
	default double pointRelativeToLine(double x, double y, Segment2D segment) {
		return (segment.end().y() - segment.start().y()) * x
			+ (segment.start().x() - segment.end().x()) * y
			+ (segment.end().x() * segment.start().y() - segment.start().x() * segment.end().y());
	}

	default boolean contains(Point2D point) {
		return point.x() >= x() && point.x() <= getMaxX()
			&& point.y() >= y() && point.y() <= getMaxY();
	}

	default boolean strictlyContains(Point2D point) {
		return point.x() > x() && point.x() < getMaxX()
			&& point.y() > y() && point.y() < getMaxY();
	}

	@Override
	default double minX() {
		return x();
	}

	@Override
	default double maxX() {
		return x() + width();
	}

	@Override
	default double minY() {
		return y();
	}

	@Override
	default double maxY() {
		return y() + height();
	}

	default Rectangle2D stretch(double amount) {
		return new BasicRectangle2D(
			x() - amount,
			y() - amount,
			width() + amount * 2,
			height() + amount * 2
		);
	}

	default Point2D corner(OrdinalDirection direction) {
		Objects.requireNonNull(direction);
		switch (direction) {
			case NW:
				return new BasicPoint2D(x(), y());
			case NE:
				return new BasicPoint2D(x() + width(), y());
			case SE:
				return new BasicPoint2D(x() + width(), y() + height());
			case SW:
			default:
				return new BasicPoint2D(x(), y() + height());
		}
	}
}
