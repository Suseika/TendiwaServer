package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.Lists;
import org.tendiwa.core.meta.Utils;
import org.tendiwa.geometry.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public final class CycleExtraVerticesRemover {
	/**
	 * For a list of vertices, returns a new list of the same vertices but without those that lie on the same line
	 * with their neighbors.
	 * <p>
	 * In a list of only two or less vertices none of them are considered lying on the same line with neighbors,
	 * though technically they are.
	 *
	 * @param vertices
	 * 	A list of vertices.
	 * @return A new list without extra vertices.
	 */
	public static List<Point2D> removeVerticesOnLineBetweenNeighbors(List<Point2D> vertices) {
		int l = vertices.size();
		if (l < 3) {
			return vertices;
		}
		Map<Integer, Point2D> nonRemovedVertices = new LinkedHashMap<>();
		for (int i = 0; i < l; i++) {
			nonRemovedVertices.put(i, vertices.get(i));
		}
		for (int i = 0; i < l; i++) {
			if (
				isOnLineBetweenPreviousAndNextNodes(
					vertices.get(Utils.previousIndex(i, l)),
					vertices.get(i),
					vertices.get(Utils.nextIndex(i, l))
				)
				) {
				nonRemovedVertices.remove(i);
			}
		}
		assert nonRemovedVertices.size() > 0;
		vertices = Lists.newArrayList(nonRemovedVertices.values());
		return vertices;
	}

	private static boolean isOnLineBetweenPreviousAndNextNodes(Point2D previous, Point2D current, Point2D next) {
		return previous != null
			&& current.distanceToLine(segment2D(previous, next)) < Vectors2D.EPSILON
			&& !isMiddlePointPointy(previous, current, next);
	}

	private static boolean isMiddlePointPointy(Point2D start, Point2D middle, Point2D end) {
		return Vector2D.fromStartToEnd(start, middle).dotProduct(
			Vector2D.fromStartToEnd(middle, end)
		) < 0;
	}
}