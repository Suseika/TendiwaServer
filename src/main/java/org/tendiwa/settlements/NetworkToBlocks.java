package org.tendiwa.settlements;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.GraphExplorer;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.awt.Color;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Divides space inside a network into enclosed blocks.
 */
public class NetworkToBlocks {
	public static TestCanvas canvas;
	private final Set<SecondaryRoadNetworkBlock> enclosedBlocks;

	NetworkToBlocks(
		UndirectedGraph<Point2D, Segment2D> relevantNetwork,
		Set<DirectionFromPoint> filamentEnds,
		double snapSize
	) {
		if (!filamentEnds.isEmpty()) {
			relevantNetwork = copyRelevantNetwork(relevantNetwork);
			GraphLooseEndsCloser
				.withSnapSize(snapSize)
				.withFilamentEnds(filamentEnds)
				.mutateGraph(relevantNetwork);
		}
		canvas.draw(relevantNetwork, DrawingGraph.basis(Color.black, Color.black, Color.black));
//		new GraphExplorer(relevantNetwork);
		enclosedBlocks = new MinimumCycleBasis<>(relevantNetwork, Point2DVertexPositionAdapter.get())
			.minimalCyclesSet()
			.stream()
			.map(cycle -> new SecondaryRoadNetworkBlock(cycle.vertexList()))
			.collect(toSet());
	}

	public Set<SecondaryRoadNetworkBlock> getEnclosedBlocks() {
		return enclosedBlocks;
	}

	private UndirectedGraph<Point2D, Segment2D> copyRelevantNetwork(UndirectedGraph<Point2D, Segment2D> relevantNetwork) {
		UndirectedGraph<Point2D, Segment2D> blockBoundsNetwork = new SimpleGraph<>(relevantNetwork.getEdgeFactory());
		for (Point2D vertex : relevantNetwork.vertexSet()) {
			blockBoundsNetwork.addVertex(vertex);
		}
		for (Segment2D edge : relevantNetwork.edgeSet()) {
			blockBoundsNetwork.addEdge(edge.start, edge.end, edge);
		}
		return blockBoundsNetwork;
	}
}
