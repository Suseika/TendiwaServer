package org.tendiwa.geometry.graphs2d;

import org.jgrapht.EdgeFactory;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polyline;
import org.tendiwa.geometry.Polyline_Wr;
import org.tendiwa.geometry.Segment2D;

import java.util.Collection;
import java.util.Set;

public final class PolylineGraph extends Polyline_Wr implements Graph2D {
	private final Graph2D graph;

	public PolylineGraph(Polyline polyline) {
		super(polyline);
		this.graph = Graph2D.createGraph(polyline, polyline.toSegments());
	}

	@Override
	public int degreeOf(Point2D vertex) {
		return graph.degreeOf(vertex);
	}

	@Override
	public Set<Segment2D> getAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		return graph.getAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.getEdge(sourceVertex, targetVertex);
	}

	@Override
	public EdgeFactory<Point2D, Segment2D> getEdgeFactory() {
		return graph.getEdgeFactory();
	}

	@Override
	public Segment2D addEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.addEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean addEdge(Point2D sourceVertex, Point2D targetVertex, Segment2D segment2D) {
		return graph.addEdge(sourceVertex, targetVertex, segment2D);
	}

	@Override
	public boolean addVertex(Point2D point2D) {
		return graph.addVertex(point2D);
	}

	@Override
	public boolean containsEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.containsEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsEdge(Segment2D segment2D) {
		return graph.containsEdge(segment2D);
	}

	@Override
	public boolean containsVertex(Point2D point2D) {
		return graph.containsVertex(point2D);
	}

	@Override
	public Set<Segment2D> edgeSet() {
		return graph.edgeSet();
	}

	@Override
	public Set<Segment2D> edgesOf(Point2D vertex) {
		return graph.edgesOf(vertex);
	}

	@Override
	public boolean removeAllEdges(Collection<? extends Segment2D> edges) {
		return graph.removeAllEdges(edges);
	}

	@Override
	public Set<Segment2D> removeAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		return graph.removeAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public boolean removeAllVertices(Collection<? extends Point2D> vertices) {
		return graph.removeAllVertices(vertices);
	}

	@Override
	public Segment2D removeEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.removeEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean removeEdge(Segment2D segment2D) {
		return graph.removeEdge(segment2D);
	}

	@Override
	public boolean removeVertex(Point2D point2D) {
		return graph.removeVertex(point2D);
	}

	@Override
	public Set<Point2D> vertexSet() {
		return graph.vertexSet();
	}

	@Override
	public Point2D getEdgeSource(Segment2D segment2D) {
		return graph.getEdgeSource(segment2D);
	}

	@Override
	public Point2D getEdgeTarget(Segment2D segment2D) {
		return graph.getEdgeTarget(segment2D);
	}

	@Override
	public double getEdgeWeight(Segment2D segment2D) {
		return graph.getEdgeWeight(segment2D);
	}
}
