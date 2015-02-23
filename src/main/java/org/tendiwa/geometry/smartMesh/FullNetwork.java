package org.tendiwa.geometry.smartMesh;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.collections.Consumers;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.graphs2d.Graph2D;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This is a Mediator that notifies other {@link NetworkPart}s of splits in the full
 * graph if that NetworkPart shares an edge that is being split.
 */
final class FullNetwork implements NetworkPart {

	private final Map<Segment2D, Collection<NetworkPart>> edgesToNetworkParts = new LinkedHashMap<>();
	private final Graph2D graph;

	FullNetwork(UndirectedGraph<Point2D, Segment2D> originalGraph) {
		this.graph = new Graph2D();
		Graphs.addGraph(graph, originalGraph);
		addNetworkPart(this);
	}

	void addNetworkPart(NetworkPart networkPart) {
		Graph2D graph = networkPart.graph();
		for (Segment2D edge : graph.edgeSet()) {
			addNetworkPart(edge, networkPart);
		}
	}

	@Override
	public Graph2D graph() {
		return graph;
	}

	void addNetworkPart(Segment2D edge, NetworkPart networkPart) {
		assert networkPart.graph().hasOnlyEdge(edge)
			|| anotherEdgeOfGraphIsInFullGraph(networkPart.graph(), edge);
		if (!networkPart.graph().containsEdge(edge)) {
			throw new IllegalArgumentException("Added edge is not contained in graph");
		}
		edgesToNetworkParts
			.computeIfAbsent(edge, (e) -> new ArrayList<>())
			.add(networkPart);
	}

	private boolean anotherEdgeOfGraphIsInFullGraph(Graph<Point2D, Segment2D> graph, Segment2D edge) {
		return graph.edgeSet().stream()
			.filter(e -> !e.equals(edge))
			.findAny()
			.isPresent();
	}

	void splitEdge(CutSegment2D cutSegment) {
		if (!cutSegment.hasBeenCut()) {
			return;
		}
		Segment2D originalSegment = cutSegment.originalSegment();
		Collection<NetworkPart> partsOwningEdge = edgesToNetworkParts.get(originalSegment);
		partsOwningEdge.forEach(part -> part.integrate(cutSegment));
		cutSegment.stream()
			.map(this::obtainCollectionFor)
			.forEach(gh -> gh.addAll(partsOwningEdge));
	}

	private Collection<NetworkPart> obtainCollectionFor(Segment2D segment) {
		assert !edgesToNetworkParts.containsKey(segment);
		Collection<NetworkPart> collection = new ArrayList<>();
		edgesToNetworkParts.put(segment, collection);
		return collection;
	}
}