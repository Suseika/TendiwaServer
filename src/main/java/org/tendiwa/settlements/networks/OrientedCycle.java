package org.tendiwa.settlements.networks;

import org.jgrapht.Graph;
import org.tendiwa.collections.SuccessiveTuples;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.straightSkeleton.Bisector;
import org.tendiwa.graphs.GraphCycleTraversal;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.graphs2d.Graph2D;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Holds a graph of a cycle within which {@link org.tendiwa.settlements.networks.SecondaryRoadNetwork} is constructed,
 * and for each edge remembers whether that edge goes clockwise or counter-clockwise. That effectively means that
 * OrientedCycle can tell if its innards are to the right or to the left from its certain edge.
 */
final class OrientedCycle implements NetworkPart {
	private final boolean isCycleClockwise;
	private final Graph2D splitOriginalGraph;
	private final Graph2D cycleGraph;
	private final Set<Segment2D> reverseEdges = new HashSet<>();

	OrientedCycle(
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle,
		Graph2D splitOriginalGraph
	) {
		this.splitOriginalGraph = splitOriginalGraph;
		this.cycleGraph = createCycleGraph(originalMinimalCycle);
		this.isCycleClockwise = JTSUtils.isYDownCCW(originalMinimalCycle.vertexList());
	}

	@Override
	public Graph2D graph() {
		return cycleGraph;
	}

	private Graph2D createCycleGraph(
		MinimalCycle<Point2D, Segment2D> originalMinimalCycle
	) {
		Graph2D cycleGraph = new Graph2D();
		SuccessiveTuples.forEach(
			originalMinimalCycle.asVertices(),
			(previous, current, next) -> {
				if (splitOriginalGraph.containsEdge(current, next)) {
					addAutoDirectedEdge(cycleGraph, current, next);
				} else {
					GraphCycleTraversal
						.traverse(splitOriginalGraph)
						.startingWith(current)
						.awayFrom(previous)
						.until(triplet -> triplet.next() == next)
						.stream()
						.forEach(
							triplet -> addAutoDirectedEdge(
								cycleGraph,
								triplet.current(),
								triplet.next()
							)
						);
				}
			}
		);
		return cycleGraph;
	}

	private void addAutoDirectedEdge(Graph<Point2D, Segment2D> cycleGraph, Point2D current, Point2D next) {
		assert splitOriginalGraph.containsEdge(current, next);
		cycleGraph.addVertex(current);
		cycleGraph.addVertex(next);
		Segment2D edge = splitOriginalGraph.getEdge(current, next);
		if (splitOriginalGraph.getEdgeSource(edge) != current) {
			assert splitOriginalGraph.getEdgeSource(edge) == next
				&& splitOriginalGraph.getEdgeTarget(edge) == current;
			reverseEdges.add(edge);
		} else {
			assert splitOriginalGraph.getEdgeSource(edge) == current
				&& splitOriginalGraph.getEdgeTarget(edge) == next;
		}
		cycleGraph.addEdge(current, next, edge);
	}

	boolean isAgainstCycleDirection(Segment2D edge) {
		return reverseEdges.contains(edge);
	}

	/**
	 * Multiplier that describes which way the ring goes along one of its edges.
	 *
	 * @param edge
	 * 	An edge with its endpoints being two consecutive nodes of this
	 * 	{@link OrientedCycle}.
	 * @return -1 or 1
	 */
	double getDirection(Segment2D edge) {
		// TODO: Is this cycle always counter-clockwise because isCycleClockwise is always false?
		return (isCycleClockwise ? -1 : 1) * (reverseEdges.contains(edge) ? 1 : -1);
	}

	/**
	 * For each of two new parts of a spilt edge, calculates if that part goes clockwise or counter-clockwise and
	 * remembers that information.
	 * <p>
	 * This method should be called each time an edge of this OrientedCycle is split with
	 */
	@Override
	public void notify(CutSegment2D cutSegment) {
		Segment2D originalSegment = cutSegment.originalSegment();
		Vector2D originalVector = originalSegment.asVector();
		boolean isSplitEdgeAgainst = isAgainstCycleDirection(originalSegment);
		cutSegment.stream()
			.filter(segment -> isSplitEdgeAgainst ^ originalVector.dotProduct(segment.asVector()) < 0)
			.forEach(this::setReverse);
		reverseEdges.remove(originalSegment);
		cycleGraph.removeEdge(cutSegment.originalSegment());
		cycleGraph.integrateCutSegment(cutSegment);
	}

	private void setReverse(Segment2D edge) {
		assert !reverseEdges.contains(edge);
		reverseEdges.add(edge);
	}

	/**
	 * [Kelly figure 42]
	 *
	 * @return An angle in radians.
	 */
	public double deviatedAngleBisector(Point2D bisectorStart, boolean inward) {
		Set<Segment2D> adjacentEdges = cycleGraph.edgesOf(bisectorStart);
		assert adjacentEdges.size() == 2;
		Iterator<Segment2D> iterator = adjacentEdges.iterator();

		Segment2D previous = iterator.next();
		if (!isClockwise(previous)) {
			previous = previous.reverse();
		}
		Segment2D next = iterator.next();
		if (!isClockwise(next)) {
			next = next.reverse();
		}

		Segment2D bisectorSegment = new Bisector(
			previous,
			next,
			bisectorStart,
			inward
		).asSegment(Bisector.DEFAULT_SEGMENT_LENGTH);
		return bisectorSegment.start.angleTo(bisectorSegment.end);
	}

	/**
	 * Checks if going from {@link org.tendiwa.geometry.Segment2D#start} to {@link org.tendiwa.geometry
	 * .Segment2D#end} would be going clockwise in the ring.
	 *
	 * @param edge
	 * 	A segment of ring.
	 * @return true if it is clockwise, false if it is counter-clockwise.
	 */
	private boolean isClockwise(Segment2D edge) {
		return isCycleClockwise ^ isAgainstCycleDirection(edge);
	}
}
