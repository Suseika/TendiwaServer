package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vector2D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.tendiwa.geometry.Vector2D.fromStartToEnd;

/**
 * Tracks which edges are split by which split events. Keeps all split events on an edge sorted by length of projection
 * of event-edge.start on edge.
 */
class RegistryOfSplitEventsOnEdges {
	private final Map<Node, TreeSet<SplitEventOnEdge>> edgesToSplitNodes = new HashMap<>();

	RegistryOfSplitEventsOnEdges(List<? extends Node> nodes) {
		nodes.forEach(this::initOriginalEdge);
	}

	void addSplitNode(InitialNode oppositeEdgeStart, SplitNode node) {
		assert edgesToSplitNodes.containsKey(oppositeEdgeStart);
		edgesToSplitNodes.get(oppositeEdgeStart).add(
			new SplitEventOnEdge(
				node,
				projectionOnEdge(node.vertex, oppositeEdgeStart.currentEdge)
			)
		);
	}

	Node getNodeFromRight(InitialNode oppositeEdgeStart, SplitNode node) {
		Node node1 = null;
		SplitEventOnEdge lower = null;
		try {
			lower = edgesToSplitNodes.get(oppositeEdgeStart).lower(
				new SplitEventOnEdge(
					node,
					projectionOnEdge(node.vertex, oppositeEdgeStart.currentEdge)
				)
			);
		} catch (NullPointerException e) {
			assert false;
		}
		if (lower != null) {
			node1 = lower.node;
		}
		if (node1 == null) {
			return oppositeEdgeStart.currentEdgeStart.face().whereEndMoved();
		} else {
			return node1;
		}
	}

	Node getNodeFromLeft(InitialNode oppositeEdgeStart, LeftSplitNode node) {
		Node node1 = null;
		SplitEventOnEdge higher = edgesToSplitNodes.get(oppositeEdgeStart).higher(
			new SplitEventOnEdge(
				node,
				projectionOnEdge(node.vertex, oppositeEdgeStart.currentEdge)
			)
		);
		if (higher != null) {
			node1 = higher.node;
		}
		if (node1 == null) {
			return oppositeEdgeStart.currentEdgeStart.face().whereStartMoved();
		} else {
			return node1;
		}
	}

	private void initOriginalEdge(Node node) {
		assert node.next() != null;
		edgesToSplitNodes.put(node, new TreeSet<>(
			(o1, o2) -> {
				if (o1 == o2) {
					return 0;
				}
				assert o1.node != o2.node;
				if (o1.node.isPair(o2.node)) {
					assert o1.node.isLeft() != o2.node.isLeft();
					return o1.node.isLeft() ? 1 : -1;
				} else {
					assert o1.projectionLength != o2.projectionLength;
					return (int) Math.signum(o1.projectionLength - o2.projectionLength);
				}
			}
		));
	}

	private static double projectionOnEdge(Point2D vertex, Segment2D edge) {
		Vector2D edgeVector = edge.asVector();
		return fromStartToEnd(edge.start, vertex).dotProduct(edgeVector) / edgeVector.magnitude() / edgeVector.magnitude();
	}

	enum Orientation {
		LEFT, RIGHT
	}

	private class SplitEventOnEdge {
		private final SplitNode node;
		private final double projectionLength;

		private SplitEventOnEdge(SplitNode node,  double projectionLength) {
			assert node != null;
			this.projectionLength = projectionLength;
			this.node = node;
		}
	}

	public static void main(String[] args) {
		Segment2D edge = Segment2D.create(4, 7, 12, 43);
		System.out.println(projectionOnEdge(edge.end, edge));
		System.out.println(projectionOnEdge(edge.start, edge));
	}
}
