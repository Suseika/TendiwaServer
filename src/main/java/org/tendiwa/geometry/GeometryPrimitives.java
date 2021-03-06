package org.tendiwa.geometry;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.BasicRange;
import org.tendiwa.core.meta.Cell;
import org.tendiwa.core.meta.Range;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;

import java.util.List;

public final class GeometryPrimitives {
	public static Point2D point2D(double x, double y) {
		return new BasicPoint2D(x, y);
	}

	public static Segment2D segment2D(double x1, double y1, double x2, double y2) {
		return new BasicSegment2D(
			new BasicPoint2D(x1, y1),
			new BasicPoint2D(x2, y2)
		);
	}

	public static Segment2D segment2D(Point2D start, Point2D end) {
		return new BasicSegment2D(start, end);
	}

	public static Rectangle2D rectangle2D(double x, double y, double width, double height) {
		return new BasicRectangle2D(x, y, width, height);
	}

	public static Rectangle2D rectangle2D(double width, double height) {
		return new BasicRectangle2D(0, 0, width, height);
	}

	public static Rectangle rectangle(int x, int y, int width, int height) {
		return new BasicRectangle(x, y, width, height);
	}

	public static Rectangle rectangle(int width, int height) {
		return new BasicRectangle(0, 0, width, height);
	}

	public static Range range(int min, int max) {
		return new BasicRange(min, max);
	}

	public static OrthoCellSegment orthoCellSegment(int x, int y, int length, Orientation orientation) {
		return new BasicOrthoCellSegment(x, y, length, orientation);
	}

	public static GraphConstructor<Point2D, Segment2D> graphConstructor() {
		return new GraphConstructor<>(BasicSegment2D::new);
	}

	public static Polygon polygon(List<Point2D> points) {
		return new BasicPolygon(points);
	}

	public static Polygon polygon(Point2D first, Point2D second, Point2D third, Point2D... rest) {
		return new BasicPolygon(first, second, third, rest);
	}

	public static Cell cell(int x, int y) {
		return new BasicCell(x, y);
	}

	public static Vector2D vector(double x, double y) {
		return new BasicPoint2D(x, y);
	}

	public static Rectangle2D rectangle2D(Rectangle rectangle) {
		return new BasicRectangle2D(
			rectangle.x(),
			rectangle.y(),
			rectangle.width(),
			rectangle.height()
		);
	}

	public static Line2D line2D(double ax, double ay, double bx, double by) {
		return new BasicLine2D(ax, ay, bx, by);
	}

	public static Graph2D graph2D(UndirectedGraph<Point2D, Segment2D> graph) {
		return new BasicMutableGraph2D(graph);
	}

	public static PointTrail pointTrail(double startX, double startY) {
		return new PointTrail(startX, startY);
	}
}
