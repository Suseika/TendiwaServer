package org.tendiwa.settlements;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.core.Direction;
import org.tendiwa.core.Directions;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.CachedCellSet;
import org.tendiwa.geometry.extensions.ChebyshevDistanceBufferBorder;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.MinimumCycleBasis;
import org.tendiwa.pathfinding.dijkstra.PathTable;

import java.util.HashSet;
import java.util.Set;

/**
 * From {@link CellSet}, creates a graph used as a base for a {@link RoadsPlanarGraphModel}.
 */
public class CityBoundsFactory {
	private final CellSet water;

	public CityBoundsFactory(CellSet water) {
		this.water = water;
	}

	/**
	 * Creates a new graph that can be used as a base for {@link RoadsPlanarGraphModel}.
	 *
	 * @param startCell
	 * 	A cell from which a City originates. Roughly denotes its final position.
	 * @param maxCityRadius
	 * 	A maximum radius of a Rectangle containing resulting City.
	 * @return A new graph that can be used as a base for {@link RoadsPlanarGraphModel}.
	 * @see CityGeometryBuilder
	 */
	public UndirectedGraph<Point2D, Segment2D> create(
		BoundedCellSet cityShape,
		Cell startCell,
		int maxCityRadius
	) {
		if (water.contains(startCell.x, startCell.y)) {
			throw new IllegalArgumentException(
				"Start cell " + startCell + " must be a ground cell, not water cell"
			);
		}
		UndirectedGraph<Point2D, Segment2D> answer = computeCityBoundingRoads(cityShape, startCell, maxCityRadius);
		assert !minimalCyclesOfGraphHaveCommonVertices(answer);
		return answer;
	}

	/**
	 * Checks if there in any vertex in graph that is present in more than one of graph's minimal cycles.
	 *
	 * @param graph
	 * 	A graph.
	 * @return true if there is such vertex, false otherwise.
	 */
	private boolean minimalCyclesOfGraphHaveCommonVertices(UndirectedGraph<Point2D, Segment2D> graph) {
		Set<MinimalCycle<Point2D, Segment2D>> minimalCycles = new MinimumCycleBasis<>(graph,
			Point2DVertexPositionAdapter.get())
			.minimalCyclesSet();
		Set<Point2D> usedVertices = new HashSet<>();
		for (MinimalCycle<Point2D, Segment2D> cycle : minimalCycles) {
			for (Point2D vertex : cycle.vertexList()) {
				boolean added = usedVertices.add(vertex);
				if (!added) {
					System.out.println(vertex);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Culls the cells that will produce intersecting bounding roads.
	 *
	 * @param bufferBorder
	 * 	Coastal road's cells.
	 * @param start
	 * 	The point from which the City originated.
	 * @return A path table whose bounds end on bufferBorder's cells, but not past them.
	 */
	private PathTable cullIntersectingBoundingRoadsCells(
		CellSet bufferBorder,
		Cell start,
		Rectangle boundingRec,
		int radius
	) {
		return new PathTable(
			start.x,
			start.y,
			(x, y) -> boundingRec.contains(x, y) && !bufferBorder.contains(x, y),
			radius
		).computeFull();
	}

	private UndirectedGraph<Point2D, Segment2D> computeCityBoundingRoads(
		BoundedCellSet cityShape,
		Cell startCell,
		int radius
	) {
		CachedCellSet bufferBorder = new CachedCellSet(
			new ChebyshevDistanceBufferBorder(
				1,
				(x, y) -> !cityShape.getBounds().contains(x, y) || !cityShape.contains(x, y)
			),
			cityShape.getBounds()
		).computeAll();
		PathTable culledTable = cullIntersectingBoundingRoadsCells(
			bufferBorder,
			startCell,
			cityShape.getBounds(),
			radius + 1
		);
		CachedCellSet culledBufferBorder = new CachedCellSet(
			new ChebyshevDistanceBufferBorder(
				1,
				culledTable::isCellComputed
			),
			culledTable.getBounds()
		).computeAll();
		return bufferBorderToGraph(culledBufferBorder);
	}

	/*
	 * Transforms a 1 cell wide border to a graph.
	 *
	 * @param bufferBorder
	 * 	One cell wide border, with cells beigh neighbors with each other from cardinal sides.
	 * @return A graph where vertices are all the cells of the one cell wide border,
	 * and edges are two cells being near each other from cardinal sides.
	 */
	private UndirectedGraph<Point2D, Segment2D> bufferBorderToGraph(CachedCellSet bufferBorder) {
		UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());

		ImmutableSet<Cell> borderCells = bufferBorder.toSet();
		BiMap<Cell, Point2D> cell2PointMap = HashBiMap.create();
		for (Cell cell : borderCells) {
			cell2PointMap.put(cell, new Point2D(cell.x, cell.y));
		}
		for (Cell cell : borderCells) {
			graph.addVertex(cell2PointMap.get(cell));
		}
		for (Cell cell : borderCells) {
			for (Direction dir : Directions.CARDINAL_DIRECTIONS) {
				Point2D neighbour = cell2PointMap.get(cell.moveToSide(dir));
				if (graph.containsVertex(neighbour)) {
					graph.addEdge(cell2PointMap.get(cell), neighbour);
				}
			}
		}
		new EdgeReducer(graph, cell2PointMap).reduceEdges();
		SameLineGraphEdgesPerturbations.perturbIfHasSameLineEdges(graph, 1e-4);
		return graph;
	}

}
