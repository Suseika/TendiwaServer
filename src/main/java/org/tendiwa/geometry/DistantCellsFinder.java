package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Out of collection of cells, greedily finds a set of cells where there is at least certain distance between each
 * found cell.
 * <p>
 * Example:
 * <pre>{@code
 *     DistantCellsFinder distantCells = new DistantCellsFinder(
 *          collectionOfCells,
 *          20
 *     );
 *     for (Cell cell : distantCells) {
 *         canvas.drawCell(cell, RED);
 *     }
 * }</pre>
 */
public class DistantCellsFinder implements Iterable<Cell> {
    private final Collection<Cell> cells;
    private final int minDistance;

    public DistantCellsFinder(
            BoundedCellSet cells,
            int minDistance
    )

    {
        this.cells = ImmutableList.copyOf(cells.toList());
        this.minDistance = minDistance;
    }

    @Override
    public Iterator<Cell> iterator() {
        return new Iterator<Cell>() {
            private Collection<Rectangle> occupiedPlaces = new LinkedList<>();
            private Cell next = findNext();

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Cell next() {
                Cell current = next;
                next = findNext();
                return current;

            }

            private Cell findNext() {
                for (Cell nextCell : cells) {
                    if (!occupiedPlaces.stream().anyMatch(r -> r.contains(nextCell))) {
                        occupyAreaAroundCell(nextCell);
                        return nextCell;
                    }
                }
                return null;
            }

            private void occupyAreaAroundCell(Cell nextCell) {
                occupiedPlaces.add(Recs.rectangleByCenterPoint(
                        nextCell,
                        minDistance * 2 + 1,
                        minDistance * 2 + 1
                ));
            }
        };
    }

}