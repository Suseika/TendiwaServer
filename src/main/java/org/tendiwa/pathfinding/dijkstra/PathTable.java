package org.tendiwa.pathfinding.dijkstra;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.geometry.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static org.tendiwa.geometry.GeometryPrimitives.*;

public class PathTable implements BoundedCellSet {

	static final int NOT_COMPUTED_CELL = -1;
	private final int startX;
	private final int startY;
	final CellSet availableCells;
	private final int maxDepth;
	private final int width;
	int[][] pathTable;
	ArrayList<Cell> newFront;
	int step;
	private final Rectangle bounds;

	public PathTable(Cell start, CellSet availableCells, int maxDepth) {
		this.startX = start.x();
		this.startY = start.y();
		this.availableCells = availableCells;
		this.maxDepth = maxDepth;
		this.width = maxDepth * 2 + 1;
		//noinspection SuspiciousNameCombination
		this.bounds = rectangle(startX - maxDepth, startY - maxDepth, width, width);
		step = 0;

		this.pathTable = new int[maxDepth * 2 + 1][maxDepth * 2 + 1];


		for (int i = 0; i < width; i++) {
			Arrays.fill(pathTable[i], NOT_COMPUTED_CELL);
		}
		// Zero-wave consists of a single cell, which is path table's start
		pathTable[maxDepth][maxDepth] = 0;
		newFront = getInitialNewFront();
	}

	/**
	 * Finds whether the starting cell is walkable or not.
	 *
	 * @return A List containing only a starting cell if the starting cell is walkable, or a List containing no cell if
	 * it is not.
	 */
	private ArrayList<Cell> getInitialNewFront() {
		ArrayList<Cell> answer = new ArrayList<>();
		Cell firstCell = new BasicCell(startX, startY);
		computeCell(firstCell.x(), firstCell.y(), maxDepth, maxDepth);
		if (contains(firstCell.x(), firstCell.y())) {
			answer.add(firstCell);
		}
		return answer;
	}


	/**
	 * Returns a new Cell {{@link #startX}:{@link #startY}};
	 *
	 * @return A new Cell.
	 */
	public final BasicCell getStart() {
		return new BasicCell(startX, startY);
	}

	/**
	 * Returns a rectangle in which all cells of this PathTable reside. Note that this rectangle is defined by #startX,
	 * #startY and #maxDepth, and not by actually computed cells. More formally, returns a rectangle
	 * <pre>
	 * {@code
	 * new Rectangle(startX - maxDepth, startY - maxDepth, width, width);
	 * }
	 * </pre>
	 *
	 * @return A bounding rectangle for this PathTable defined by its #startX, #startY and #maxDepth.
	 */
	@Override
	public final Rectangle getBounds() {
		return bounds;
	}

	/**
	 * A getter f {@link #maxDepth}. This method is called radius because in Chebyshev metric {@link #maxDepth} is
	 * radius of a circle that appears to be square in Euclidean metric.
	 *
	 * @return #maxDepth
	 */
	@SuppressWarnings("unused")
	public final int radius() {
		return maxDepth;
	}

	public final PathTable computeFull() {
		boolean computed;
		do {
			computed = nextWave();
		} while (computed);
		return this;
	}

	private boolean nextWave() {
//        if (step == maxDepth) {
//            return false;
//        }
		ArrayList<Cell> oldFront = newFront;
		newFront = new ArrayList<>();
		for (Cell anOldFront : oldFront) {
			int x = anOldFront.x();
			int y = anOldFront.y();
			int[] adjacentX = new int[]{x + 1, x, x, x - 1, x + 1, x + 1, x - 1, x - 1};
			int[] adjacentY = new int[]{y, y - 1, y + 1, y, y + 1, y - 1, y + 1, y - 1};
			for (int j = 0; j < 8; j++) {
				int thisNumX = adjacentX[j];
				int thisNumY = adjacentY[j];
				int tableX = thisNumX - startX + maxDepth;
				int tableY = thisNumY - startY + maxDepth;
				computeCell(thisNumX, thisNumY, tableX, tableY);
			}
		}
		if (newFront.isEmpty()) {
			return false;
		}
		step++;
		return true;
	}

	/**
	 * Checks if a cell should be stepped on and adds it into newFront if it should. This code is extracted into a
	 * method only to be overridden by {@link PostConditionPathTable}.
	 *
	 * @param thisNumX
	 * 	X coordinate of a cell in world coordinates.
	 * @param thisNumY
	 * 	Y coordinate of a cell in world coordinates.
	 * @param tableX
	 * 	X coordinate of a cell in table coordinates.
	 * @param tableY
	 * 	Y coordinate of a cell in table coordinates.
	 */
	protected void computeCell(int thisNumX, int thisNumY, int tableX, int tableY) {
		if (bounds.contains(thisNumX, thisNumY) && pathTable[tableX][tableY] == NOT_COMPUTED_CELL && availableCells.contains(thisNumX, thisNumY)) {
			// Step to cell if character can see it and it is free
			// or character cannot se it and it is not PASSABILITY_NO
			pathTable[tableX][tableY] = step + 1;
			newFront.add(new BasicCell(thisNumX, thisNumY));
		}
	}

	/**
	 * Returns steps of path to a destination cell computed on this path table.
	 *
	 * @param target
	 * 	Target coordinates.
	 * 	Destination y coordinate.
	 * @return null if path can't be found.
	 */
	public final LinkedList<Cell> getPath(Cell target) {
		if (Math.abs(target.x() - startX) > maxDepth || Math.abs(target.y() - startY) > maxDepth) {
			throw new IllegalArgumentException("Trying to get path to " + target.x() + ":" + target.y() + ". That point is too far from start point " + startX + ":" + startY + ", maxDepth is " + maxDepth);
		}
		while (pathTable[maxDepth + target.x() - startX][maxDepth + target.y() - startY] == NOT_COMPUTED_CELL) {
			// There will be 0 iterations if that cell is already computed
			boolean waveAddedNewCells = nextWave();
			if (!waveAddedNewCells) {
				return null;
			}
		}
		if (target.x() == startX && target.y() == startY) {
			throw new RuntimeException("Getting path to itself");
		}
		LinkedList<Cell> path = new LinkedList<>();
		if (Cells.isNear(startX, startY, target.x(), target.y())) {
			path.add(new BasicCell(target.x(), target.y()));
			return path;
		}
		int currentNumX = target.x();
		int currentNumY = target.y();
		int cX = currentNumX;
		int cY = currentNumY;
		for (
			int j = pathTable[currentNumX - startX + maxDepth][currentNumY - startY + maxDepth];
			j > 0;
			j = pathTable[currentNumX - startX + maxDepth][currentNumY - startY + maxDepth]
			) {
			path.addFirst(new BasicCell(currentNumX, currentNumY));
			int[] adjacentX = {cX, cX + 1, cX, cX - 1, cX + 1, cX + 1, cX - 1, cX - 1};
			int[] adjacentY = {cY - 1, cY, cY + 1, cY, cY + 1, cY - 1, cY + 1, cY - 1};
			for (int i = 0; i < 8; i++) {
				int thisNumX = adjacentX[i];
				int thisNumY = adjacentY[i];
				int tableX = thisNumX - startX + maxDepth;
				int tableY = thisNumY - startY + maxDepth;
				if (tableX < 0 || tableX >= width) {
					continue;
				}
				if (tableY < 0 || tableY >= width) {
					continue;
				}
				if (pathTable[tableX][tableY] == j - 1) {
					currentNumX = adjacentX[i];
					currentNumY = adjacentY[i];
					cX = currentNumX;
					cY = currentNumY;
					break;
				}
			}
		}
		return path;
	}

	public final boolean isCellComputed(int x, int y) {
		return bounds.contains(x, y) && pathTable[maxDepth + x - startX][maxDepth + y - startY] != NOT_COMPUTED_CELL;
	}

	@Override
	public boolean contains(int x, int y) {
		try {
			return bounds.contains(x, y) && pathTable[maxDepth + x - startX][maxDepth + y - startY] != PathTable
				.NOT_COMPUTED_CELL;
		} catch (Exception e) {
			System.out.println(bounds);
			throw new RuntimeException(x + " " + y);
		}
	}
}
