package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.core.meta.Cell;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

@FunctionalInterface
/**
 * A potentially infinite set of cells.
 */
public interface CellSet {

	Function<ScatteredMutableCellSet, FiniteCellSet> scatteredMutableCellSetFiniteCellSetFunction = (a) -> a;

	/**
	 * Checks if a cell is in the set.
	 *
	 * @param x
	 * 	X coordinate of a cell.
	 * @param y
	 * 	Y coordinate of a cell.
	 * @return true if a cell is in the set, false otherwise.
	 */
	boolean contains(int x, int y);

	/**
	 * Checks if a cell is in the set.
	 *
	 * @param cell
	 * 	A cell.
	 * @return true if a cell is in the set, false otherwise.
	 */
	default boolean contains(Cell cell) {
		return contains(cell.x(), cell.y());
	}

	/**
	 * Creates a set that is an intersection of this set and another set.
	 *
	 * @param set
	 * 	Another set.
	 * @return A set that is an intersection of this set and another set.
	 */
	default CellSet and(CellSet set) {
		return (x, y) -> contains(x, y) && set.contains(x, y);
	}

	/**
	 * Creates a set that is a union of this set and another set.
	 *
	 * @param set
	 * 	Another set.
	 * @return A set that is a union of this set and another set.
	 */
	default CellSet or(CellSet set) {
		return (x, y) -> contains(x, y) || set.contains(x, y);
	}

	/**
	 * Creates a set that is a symmetric difference of this set and another set.
	 *
	 * @param set
	 * 	Another set.
	 * @return A set that is a symmetric difference of this set and another set.
	 * @see <a href="http://en.wikipedia.org/wiki/Symmetric_difference">Symmetric difference</a>
	 */
	default CellSet xor(CellSet set) {
		return (x, y) -> contains(x, y) ^ set.contains(x, y);
	}

	default CellSet without(CellSet set) {
		return (x, y) -> contains(x, y) && !set.contains(x, y);
	}

	static Collector<Cell, ?, FiniteCellSet> toCellSet() {
		return new Collector<Cell, ScatteredMutableCellSet, FiniteCellSet>() {
			@Override
			public Supplier<ScatteredMutableCellSet> supplier() {
				return ScatteredMutableCellSet::new;
			}

			@Override
			public BiConsumer<ScatteredMutableCellSet, Cell> accumulator() {
				return ScatteredMutableCellSet::add;
			}

			@Override
			public BinaryOperator<ScatteredMutableCellSet> combiner() {
				return (left, right) -> {
					left.addAll(right);
					return left;
				};
			}

			@Override
			public Function<ScatteredMutableCellSet, FiniteCellSet> finisher() {
				return scatteredMutableCellSetFiniteCellSetFunction;
			}

			@Override
			public Set<Characteristics> characteristics() {
				return ImmutableSet.of(
					Characteristics.IDENTITY_FINISH,
					Characteristics.UNORDERED
				);
			}
		};
	}

	/**
	 * Returns a cell set that doesn't contain any cells.
	 *
	 * @return A cell set that doesn't contain any cells.
	 */
	static CellSet empty() {
		return (x, y) -> false;
	}

	static Collector<Cell, ?, BoundedCellSet> toBoundedCellSet(Rectangle bounds) {

		return new Collector<Cell, MutableBoundedCellSet, BoundedCellSet>() {
			@Override
			public Supplier<MutableBoundedCellSet> supplier() {
				return () -> new Mutable2DCellSet(bounds);
			}

			@Override
			public BiConsumer<MutableBoundedCellSet, Cell> accumulator() {
				return MutableBoundedCellSet::add;
			}

			@Override
			public BinaryOperator<MutableBoundedCellSet> combiner() {
				return (left, right) -> {
					left.addAll(right);
					return left;
				};
			}

			@Override
			public Function<MutableBoundedCellSet, BoundedCellSet> finisher() {
				return (a) -> a;
			}

			@Override
			public Set<Characteristics> characteristics() {
				return ImmutableSet.of(
					Characteristics.IDENTITY_FINISH,
					Characteristics.UNORDERED
				);
			}
		};
	}
}
