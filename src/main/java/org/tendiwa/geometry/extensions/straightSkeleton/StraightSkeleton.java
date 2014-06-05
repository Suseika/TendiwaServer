package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.GeometryException;
import org.tendiwa.geometry.JTSUtils;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.settlements.LineIntersection;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import static org.tendiwa.drawing.extensions.DrawingSegment2D.withColor;
import static org.tendiwa.graphs.MinimumCycleBasis.perpDotProduct;

public class StraightSkeleton {

	private final ListOfActiveVertices lav;
	private final List<Segment2D> edges;
	TestCanvas canvas = new TestCanvas(1, 200, 200);
	private final PriorityQueue<IntersectionPoint> queue;
	private final Multimap<Point2D, Point2D> arcs = HashMultimap.create();

	public StraightSkeleton(MinimalCycle<Point2D, Segment2D> cycle) {
		// Transform clockwise list to a counter-clockwise list.
		this(Lists.reverse(cycle.vertexList()), true);

	}

	public StraightSkeleton(List<Point2D> vertices) {
		this(vertices, false);
	}

	private StraightSkeleton(List<Point2D> vertices, boolean trustCounterClockwise) {
		vertices = Lists.reverse(vertices);
		if (!trustCounterClockwise && !JTSUtils.isYDownCCW(vertices)) {
			vertices = Lists.reverse(vertices);
		}
		int l = vertices.size();
		edges = new ArrayList<>(l);
		for (int i = 0; i < l; i++) {
			edges.add(
				new Segment2D(
					vertices.get(i),
					vertices.get(i + 1 < l ? i + 1 : 0)
				)
			);
			canvas.draw(edges.get(i), withColor(Color.RED));
		}


		this.lav = new ListOfActiveVertices(vertices, edges);
		this.queue = new PriorityQueue<>(l);
		// [Obdrzalek 1998, paragraph 2.2, algorithm step 1c]
		int i = 0;
		for (
			Node node = lav.nodes.getFirst();
			i < l;
			i++, node = node.next
			) {
//			canvas.draw(node.bisector.segment, DrawingSegment2D.withColor(Color.green));
			IntersectionPoint e = computeNearerBisectorsIntersection(node);
			if (e != null) {
				queue.add(e);
			}
		}
		while (!queue.isEmpty()) {
			// Convex 2a
			IntersectionPoint point = queue.poll();
			if (point.event == EventType.EDGE) {
				// Convex 2b
				if (point.va.isProcessed || point.vb.isProcessed) {
					continue;
				}
				// Convex 2c
				if (point.va.previous.previous.vertex.equals(point.vb.vertex)) {
					outputArc(point.va.vertex, point);
					point.va.isProcessed = true;
					point.vb.isProcessed = true;
					point.va.previous.isProcessed = true;
					outputArc(point.vb.vertex, point);
					assert point.va.previous == point.vb.next;
					outputArc(point.va.previous.vertex, point);
					point.va.isProcessed = true;
					point.vb.isProcessed = true;
					point.va.previous.isProcessed = true;
					continue;
				}
				// Convex 2d
				outputArc(point.va.vertex, point);
				outputArc(point.vb.vertex, point);

				// Convex 2e
				point.va.isProcessed = true;
				point.vb.isProcessed = true;
				Node node = new Node(
					point.va.previous.currentEdge,
					point.vb.currentEdge,
					point
				);
				node.connectWithPrevious(point.va.previous);
				point.vb.next.connectWithPrevious(node);
				node.computeReflexAndBisector();

				// Convex 2f
				IntersectionPoint e = computeNearerBisectorsIntersection(node);
				if (e != null) {
					queue.add(e);
				}
			} else {
				System.out.println("SPLIT");
				assert point.event == EventType.SPLIT;
				canvas.draw(point, DrawingPoint2D.withColorAndSize(Color.YELLOW, 5));
				if (point.va.isProcessed) {
					continue;
				}
				// Non-convex 2c
				if (point.va.previous.previous.previous == point.va) {
					outputArc(point.va.vertex, point);
					point.va.isProcessed = true;
					point.vb.isProcessed = true;
					point.va.previous.isProcessed = true;
					outputArc(point.vb.vertex, point);
					assert point.va.previous == point.vb.next;
					outputArc(point.va.previous.vertex, point);
					point.va.isProcessed = true;
					point.vb.isProcessed = true;
					point.va.previous.isProcessed = true;
					continue;
				}
				// Non-convex 2D
				outputArc(point.va.vertex, point);
				// Non-convex 2e
				point.va.isProcessed = true;
				Point2D oppositeEdgeSplitPoint = new LineIntersection(
					point.va.bisector.segment,
					point.oppositeEdgeStart.currentEdge
				).getIntersectionPoint();
				Node node1 = new Node(
					point.va.previous.currentEdge,
					point.oppositeEdgeStart.currentEdge,
					point
				);
				node1.connectWithPrevious(point.va.previous);
				point.oppositeEdgeStart.next.connectWithPrevious(node1);
				node1.computeReflexAndBisector();

				Node node2 = new Node(
					point.oppositeEdgeStart.currentEdge,
					point.va.currentEdge,
					point
				);
				node2.connectWithPrevious(point.oppositeEdgeStart);
				point.va.next.connectWithPrevious(node2);
				node2.computeReflexAndBisector();


//				canvas.draw(node1.bisector.segment, DrawingSegment2D.withColor(Color.green));
//				canvas.draw(node2.bisector.segment, DrawingSegment2D.withColor(Color.blue));
//				canvas.draw(new Segment2D(point, point.oppositeEdgeStart.vertex), DrawingSegment2D.withColorDirected(Color
//					.black));
//				canvas.draw(new Segment2D(point.va.next.vertex, point), DrawingSegment2D.withColorDirected(Color
//					.yellow));
				// Non-convex 2f
				IntersectionPoint e1 = computeNearerBisectorsIntersection(node1);
				if (e1 != null) {
					queue.add(e1);
				}
				IntersectionPoint e2 = computeNearerBisectorsIntersection(node2);
				if (e2 != null) {
					queue.add(e2);
				}
			}
		}
	}

	private void outputArc(Point2D start, Point2D end) {
		arcs.put(start, end);
		canvas.draw(new Segment2D(start, end), withColor(Color.CYAN));
	}

	private IntersectionPoint computeNearerBisectorsIntersection(Node node) {
		// Non-convex 1c
		LineIntersection next = node.bisector.intersectionWith(node.next.bisector);
		LineIntersection previous = null;
		try {
			previous = node.bisector.intersectionWith(node.previous.bisector);
		} catch (GeometryException e) {
			canvas.draw(node.vertex, DrawingPoint2D.withColorAndSize(Color.red, 4));
			canvas.draw(node.previous.vertex, DrawingPoint2D.withColorAndSize(Color.black, 4));
			canvas.draw(node.bisector.segment, DrawingSegment2D.withColorDirected(Color.blue));
			canvas.draw(node.previous.bisector.segment, DrawingSegment2D.withColorDirected(Color.green));
			throw new RuntimeException(e);
		}
		Point2D nearer = null;
		Node originalEdgeStart = null;
		Node va = null;
		Node vb = null;
		if (next.r > 0 && previous.r > 0) {
			if (next.r < previous.r) {
				nearer = next.getIntersectionPoint();
				originalEdgeStart = node;
				va = node;
				vb = node.next;
			} else {
				nearer = previous.getIntersectionPoint();
				originalEdgeStart = node.previous;
				va = node.previous;
				vb = node;
			}
		} else if (next.r > 0) {
			nearer = next.getIntersectionPoint();
			originalEdgeStart = node;
			va = node;
			vb = node.next;
		} else if (previous.r > 0) {
			nearer = previous.getIntersectionPoint();
			originalEdgeStart = node.previous;
			va = node.previous;
			vb = node;
		}
		if (node.isReflex) {
			IntersectionPoint splitPoint = findSplitEvent(node);
			if (
				nearer == null
					|| splitPoint != null
					&& node.vertex.distanceTo(splitPoint) < node.vertex.distanceTo(nearer)
				) {
				return splitPoint;
			}
		}
		assert nearer == null || va != null && vb != null;
		return nearer == null ? null : new IntersectionPoint(nearer.x, nearer.y, originalEdgeStart, va, vb,
			EventType.EDGE);
	}

	/**
	 * [Obdrzalek 1998, paragraph 2.2, figure 4]
	 * <p>
	 * Computes the point where a split event occurs.
	 *
	 * @return The point where split event occurs, or null if there is no split event.
	 */
	private IntersectionPoint findSplitEvent(Node reflexNode) {
		assert reflexNode.isReflex;
		Point2D splitPoint = null;
		Node originalEdgeStart = null;
		for (Node node : lav.nodes) {
			if (
				new LineIntersection(
					reflexNode.bisector.segment.start, reflexNode.bisector.segment.end,
					node.currentEdge
				).r <= 0
				) {
				continue;
			}
			if (node == reflexNode || node == reflexNode.previous || node == reflexNode.next) {
				continue;
			}
			Point2D point = computeSplitPoint(reflexNode, node.currentEdge);
			if (isPointInAreaBetweenEdgeAndItsBisectors(point, node)) {
				if (
					splitPoint == null
						|| reflexNode.vertex.distanceTo(splitPoint) > reflexNode.vertex.distanceTo(point)
					) {
					splitPoint = point;
					originalEdgeStart = node;
				}
			}
		}
		if (splitPoint != null) {
			System.out.println("VOILA");
		}
		return splitPoint == null ? null :
			new IntersectionPoint(splitPoint.x, splitPoint.y, originalEdgeStart, reflexNode, null, EventType.SPLIT);
	}

	/**
	 * [Obdrzalek 1998, paragraph 2.2, Figure 4]
	 * <p>
	 * Computes point B_i.
	 *
	 * @param currentNode
	 * 	A reflex node that creates a split event.
	 * @param oppositeEdge
	 * 	The tested line segment.
	 * @return Intersection between the bisector at {@code currentNode} and the axis of the angle between one of the
	 * edges starting at {@code currentNode} and the tested line segment {@code oppositeEdge}.
	 */
	private Point2D computeSplitPoint(Node currentNode, Segment2D oppositeEdge) {
		assert currentNode.isReflex;
//		Point2D bisectorStart = new LineIntersection(
//			currentNode.previousEdge,
//			oppositeEdge
//		).getIntersectionPoint();
//		Point2D oneVector = currentNode.vertex.subtract(bisectorStart).normalize().multiply(40);
//		Point2D oppositeEdgeVector = oppositeEdge.end.subtract(oppositeEdge.start).normalize().multiply(40);
//		Segment2D bisector = new Segment2D(
//			bisectorStart,
//			bisectorStart.add(oneVector).add(oppositeEdgeVector)
//		);
//		Point2D intersectionPoint = new LineIntersection(currentNode.bisector.segment, bisector).getIntersectionPoint();
//		canvas.draw(oppositeEdge, DrawingSegment2D.withColor(Color.black));
//		canvas.draw(bisector, DrawingSegment2D.withColor(Color.green));
//		canvas.draw(currentNode.vertex, DrawingPoint2D.withColorAndSize(Color.green, 6));
//		assert computeAnotherIntersectionPoint(currentNode, oppositeEdge).distanceTo(intersectionPoint) < 0.1
//			Should be the same point
//			: computeAnotherIntersectionPoint(currentNode, oppositeEdge) + " " + intersectionPoint;
//		return intersectionPoint;
		Point2D bisectorStart = new LineIntersection(currentNode.previousEdge, oppositeEdge).getIntersectionPoint();
		Bisector bisector = new Bisector(
			new Segment2D(
				currentNode.vertex,
				bisectorStart
			),
			new Segment2D(
				bisectorStart,
				new LineIntersection(
					currentNode.bisector.segment,
					oppositeEdge
				).getIntersectionPoint()
			),
			bisectorStart,
			false
		);
		Point2D intersectionPoint = bisector.intersectionWith(currentNode.bisector).getIntersectionPoint();
//		Point2D anotherIntersectionPoint = computeAnotherIntersectionPoint(currentNode, oppositeEdge);
//		if (intersectionPoint.distanceTo(anotherIntersectionPoint) > 0.1) {
//			System.out.println(anotherIntersectionPoint + " " + intersectionPoint);
//			canvas.draw(bisector.segment, withColor(Color.black));
//			canvas.draw(currentNode.bisector.segment, withColor(Color.yellow));
//			canvas.draw(anotherIntersectionPoint, DrawingPoint2D.withColorAndSize(Color.blue, 6));
//			canvas.draw(intersectionPoint, DrawingPoint2D.withColorAndSize(Color.green, 4));
//			assert false;
//		}
		return intersectionPoint;
	}

	private Point2D computeAnotherIntersectionPoint(Node currentNode, Segment2D oppositeEdge) {
//		Point2D anotherBisectorStart = new LineIntersection(
//			currentNode.currentEdge,
//			oppositeEdge
//		).getIntersectionPoint();
//		Point2D anotherOppositeEdgeVector = oppositeEdge.start.subtract(oppositeEdge.end).normalize();
//		Point2D anotherOneVector = currentNode.vertex.subtract(anotherBisectorStart).normalize();
//		Segment2D anotherBisector = new Segment2D(
//			anotherBisectorStart,
//			anotherBisectorStart.add(anotherOneVector).add(anotherOppositeEdgeVector)
//		);
//		return new LineIntersection(
//			currentNode.bisector.segment,
//			anotherBisector
//		).getIntersectionPoint();
		Segment2D previousEdge = new Segment2D(currentNode.next.vertex, currentNode.vertex);
		Point2D bisectorStart = new LineIntersection(previousEdge, oppositeEdge).getIntersectionPoint();
		Bisector bisector = new Bisector(
			new Segment2D(
				previousEdge.end,
				bisectorStart
			),
			new Segment2D(
				bisectorStart,
				new LineIntersection(
					currentNode.bisector.segment,
					oppositeEdge
				).getIntersectionPoint()
			),
			bisectorStart,
			false
		);
		return bisector.intersectionWith(currentNode.bisector).getIntersectionPoint();
	}

	/**
	 * [Obdrzalek 1998, paragraph 2.2, Figure 4]
	 * <p>
	 * Checks if a point (namely point B coming from a reflex vertex) is located in an area bounded by an edge and
	 * bisectors coming from start and end nodes of this edge.
	 *
	 * @param point
	 * 	The point to test.
	 * @param currentNode
	 * 	A node at which starts the area-forming edge.
	 * @return true if the point is located within the area marked by an edge and edge's bisectors, false otherwise.
	 */
	private static boolean isPointInAreaBetweenEdgeAndItsBisectors(Point2D point, Node currentNode) {
		Bisector currentBisector = currentNode.bisector;
		Bisector nextBisector = currentNode.next.bisector;
		Point2D a = currentBisector.segment.end;
		Point2D b = currentNode.vertex;
		Point2D c = currentNode.next.vertex;
		Point2D d = nextBisector.segment.end;
		return isPointReflex(a, point, b) && isPointReflex(b, point, c) && isPointReflex(c, point, d);
	}

	/**
	 * Given 3 counter-clockwise points of a polygon, check if the middle one is convex or reflex.
	 *
	 * @param previous
	 * 	Beginning of vector 1.
	 * @param point
	 * 	End of vector 1 and beginning of vector 2.
	 * @param next
	 * 	End of vector 2.
	 * @return true if {@code point} is reflex, false if it is convex of if all points lie on the same line.
	 */
	private static boolean isPointReflex(Point2D previous, Point2D point, Point2D next) {
		return perpDotProduct(
			new double[]{point.x - previous.x, point.y - previous.y},
			new double[]{next.x - point.x, next.y - point.y}
		) > 0;
	}

	public static void test(Node start) {
		Node node = start;
		int i = 0;
		do {
			System.out.println((i++) + " " + node.vertex);
			node = node.next;
			if (i > 10) {
				break;
			}
		} while (node != start);
	}

}
