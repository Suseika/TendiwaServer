package org.tendiwa.settlements;

import com.aliasi.util.CompactHashSet;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Direction;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.terrain.KnownWorldGenerationException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

//TODO: This class mutates constructor argument. Check that out.

class EdgeReducer {

	private final CardinalDirection[] growingDirs = {CardinalDirection.N, CardinalDirection.E};
	private UndirectedGraph<Point2D, Segment2D> graph;
	private BiMap<Cell, Point2D> map;

	/**
	 * @param graphToMutate
	 * 	A graph to be mutated by the algorithm.
	 * @param map
	 * 	A mapping to vertices of {@code graphToMutate} from those vertices transformed to {@link Cell}s.
	 */
	public EdgeReducer(UndirectedGraph<Point2D, Segment2D> graphToMutate, BiMap<Cell, Point2D> map) {
		this.graph = graphToMutate;
		this.map = map;
	}


	/**
	 * Finds cells on border that have more than 2 (that is, 3 or 4) neighbors.
	 *
	 * @return A collection of all cells on border that have 3 or 4 neighbors.
	 */
	private Collection<Cell> findIntersectionCells() {
		Collection<Cell> answer = new HashSet<>();
		for (Cell cell : map.keySet()) {
			int neighbourCells = 0;
			for (Direction dir : CardinalDirection.values()) {
				if (map.containsKey(cell.moveToSide(dir))) {
					neighbourCells++;
				}
			}
			if (neighbourCells > 2) {
				answer.add(cell);
			}
		}
		return answer;
	}

	/**
	 * Joins edges of a planar graph that are consecutive and lie on the same straight horizontal or vertical line.
	 * Graph is mutated as a result.
	 * <p>
	 * This operation mostly removes vertices and adds new edges, but is some rare (but absolutely legitimate) cases it
	 * may also have to add new vertices.
	 */
	public void reduceEdges() {
		boolean changesMade;
		Collection<Point2D> finalVertices = new CompactHashSet<>(map.size() / 4);
		Set<Point2D> vertices = ImmutableSet.copyOf(graph.vertexSet());
		do {
			changesMade = false;
			Collection<Cell> intersectionCells = findIntersectionCells();
			for (Point2D point : vertices) {
				if (!graph.containsVertex(point) || finalVertices.contains(point)) {
					continue;
				}
				Cell graphCell = map.inverse().get(point);
				for (CardinalDirection dir : growingDirs) {
					int combinedEdgeLength = 1;
					Cell movedCell = graphCell;
					while (true) {
						// Find a cell on one side of a straight segment
						movedCell = movedCell.moveToSide(dir);
						if (map.containsKey(movedCell) && graph.containsVertex(map.get(movedCell))) {
							combinedEdgeLength++;
							if (intersectionCells.contains(movedCell)) {
								break;
							}
						} else {
							movedCell = movedCell.moveToSide(dir.opposite());
							break;
						}
					}
					Cell oppositeMovedCell = graphCell;
					while (true) {
						// Find a cell on the other side of a straight segment
						oppositeMovedCell = oppositeMovedCell.moveToSide(dir.opposite());
						if (map.containsKey(oppositeMovedCell) && graph.containsVertex(map.get(oppositeMovedCell))) {
							combinedEdgeLength++;
							if (intersectionCells.contains(oppositeMovedCell)) {
								break;
							}
						} else {
							oppositeMovedCell = oppositeMovedCell.moveToSide(dir);
							break;
						}
					}
					if (combinedEdgeLength > 2) {
						for (
							Cell cell = movedCell.moveToSide(dir.opposite());
							!cell.equals(oppositeMovedCell) && !finalVertices.contains(map.get(cell));
							cell = cell.moveToSide(dir.opposite())
							) {
							boolean removed = graph.removeVertex(map.get(cell));
							assert removed;
						}
						changesMade = true;
//                            canvas.drawCell(oppositeMovedCell, YELLOW);
//                            canvas.drawCell(movedCell, RED);
						if (!graph.containsVertex(map.get(oppositeMovedCell))) {
							indicateThatBorderGoesAlongItselfProblem();
						}
						graph.addEdge(map.get(movedCell), map.get(oppositeMovedCell));
						finalVertices.add(map.get(movedCell));
						finalVertices.add(map.get(oppositeMovedCell));
					} else if (combinedEdgeLength == 2) {
//                            if (!graph.containsEdge(map.get(movedCell), map.get(oppositeMovedCell))) {
//                                canvas.draw(movedCell, DrawingCell.withColorAndSize(Color.BLACK, 1));
//                                canvas.draw(oppositeMovedCell, DrawingCell.withColorAndSize(Color.BLACK, 1));
//                                throw new AssertionError(movedCell + " " + oppositeMovedCell);
//                            }
					}
				}
			}
		} while (changesMade);
	}

	/**
	 * Notice how the pink border goes along itself where inner space encounters outer space.
	 * <p>
	 * <img src="http://tendiwa.org/doc-illustrations/edge-reducer-with-border-going-along-itself.png" />
	 */
	private void indicateThatBorderGoesAlongItselfProblem() throws KnownWorldGenerationException {
		throw new KnownWorldGenerationException("Border given to EdgeReducer goes along itself");
	}
}
