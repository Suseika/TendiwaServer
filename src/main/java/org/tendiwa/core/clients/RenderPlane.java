package org.tendiwa.core.clients;

import com.google.common.collect.*;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.tendiwa.core.*;
import org.tendiwa.core.events.EventFovChange;
import org.tendiwa.core.events.EventInitialTerrain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is supposed to be used by anyone who creates his own Tendiwa client. It uses the philosophical metaphor
 * of
 * a subjective and objective world, where subjective world (as a player character sees it) is represented by an
 * instance of this class, and the objective world (the world as it is if there's no observer) is represented by {@link
 * org.tendiwa.core.World} in backend.
 */
public class RenderPlane {
	private final HorizontalPlane backendPlane;
	private final World world;
	private Map<Integer, RenderCell> cells = new HashMap<>();
	private Multimap<Integer, RememberedItem> unseenItems = HashMultimap.create();
	private Table<Integer, CardinalDirection, RenderBorder> unseenBorderObjects = HashBasedTable.create();
	private Map<Border, RenderBorder> borders = new HashMap<>();

	@Inject
	RenderPlane(
		@Assisted World world,
		@Assisted HorizontalPlane backendPlane
	) {
		this.world = world;
		this.backendPlane = backendPlane;
	}

	/**
	 * Returns an object that represents a subjective view of a particular cell.
	 *
	 * @param x
	 * 	X coordinate of a cell in world coordinates.
	 * @param y
	 * 	Y coordinate of a cell in world coordinates.
	 * @return The cell object, or null if that cell has not yet been seen or unseen.
	 */
	public RenderCell getCell(int x, int y) {
		return cells.get(cellHash(x, y));
	}

	/**
	 * Returns a cell corresponding to a hash. Hash is f(x,y) = x*worldHeight+y. For example, for cell 5:12 in 400x300
	 * world
	 * hash is 5*300+12 = 1512.
	 *
	 * @param hash
	 * 	f(x,y) = x*worldHeight+y
	 * @return The cell object, or null if that cell has not yet been seen or unseen.
	 */
	public RenderCell getCell(int hash) {
		return cells.get(hash);
	}

	/**
	 * Checks if a player sees or has ever seen a cell. That is, if there is an object created to hold a subjective
	 * state of
	 * a cell.
	 *
	 * @param x
	 * 	X coordinate of cell in world coordinates.
	 * @param y
	 * 	Y coordinate of cell in world coordinates.
	 * @return True if there is a cell with given coordinates, false otherwise.
	 */
	public boolean hasCell(int x, int y) {
		if (x >= 0 && x <= world.getWidth() && y >= 0 && y <= world.getHeight()) {
			return cells.containsKey(cellHash(x, y));
		} else {
			return false;
		}
	}

	/**
	 * Checks if player can see a cell. Objective analog is {@link org.tendiwa.core.vision.Seer#isCellVisible(int, int,
	 * Border)}. Note that if this method returns false, then cell can be either unseen or not yet seen.
	 *
	 * @param x
	 * 	X coordinate of cell in world coordinates.
	 * @param y
	 * 	Y coordinate of cell in world coordinates.
	 * @return True if cell is visible, false otherwise.
	 */
	public boolean isCellVisible(int x, int y) {
		return hasCell(x, y) && getCell(x, y).isVisible();
	}

	/**
	 * Checks if player seen the cell earlier. There is no objective analog of this method. Note that f this method
	 * returns
	 * false, then cell can be either seen or not yet seen at all.
	 *
	 * @param x
	 * 	X coordinate of cell in world coordinates.
	 * @param y
	 * 	Y coordinate of cell in world coordinates.
	 * @return True if cell is visible, false otherwise.
	 */
	public boolean isCellUnseen(int x, int y) {
		return hasCell(x, y) && !getCell(x, y).isVisible();
	}

	public void addUnseenItem(int x, int y, Item item) {
		unseenItems.put(cellHash(x, y), new RememberedItem(x, y, item.getType()));
	}

	/**
	 * Returns all items that player has unseen.
	 *
	 * @return All remembered items.
	 */
	public Collection<RememberedItem> getRememberedItems() {
		return Collections.unmodifiableCollection(unseenItems.values());
	}

	/**
	 * Checks if there are any unseen items in a cell with a given hash.
	 *
	 * @param hash
	 * 	Hash of cell coordinates f(x,y) = x*worldHeight + y;
	 * @return True if there are any items, false otherwise.
	 */
	public boolean hasAnyUnseenItems(int hash) {
		return unseenItems.containsKey(hash);
	}

	private int cellHash(int x, int y) {
		return x * world.getHeight() + y;
	}

	private int[] cellHashToCoords(int hash) {
		return new int[]{hash / world.getHeight(), hash % world.getHeight()};
	}

	public Collection<RememberedItem> getUnseenItems(int x, int y) {
		return unseenItems.get(cellHash(x, y));
	}

	public void unseeAllCells() {
		for (RenderCell cell : cells.values()) {
			cell.setVisible(false);
		}
	}

	public boolean hasUnseenBorderObject(RenderBorder border) {
		return unseenBorderObjects.contains(
			Chunk.cellHash(border.getX(), border.getY(), world.getHeight()),
			border.getSide()
		);
	}

	public void addUnseenBorder(Border border) {
		unseenBorderObjects.put(
			Chunk.cellHash(border.x, border.y, world.getHeight()),
			border.side,
			borders.get(border)
		);
	}

	public boolean isBorderVisible(Border border) {
		return borders.containsKey(border);
	}

	public void seeBorder(RenderBorder border) {
		assert !borders.containsKey(border);
		borders.put(new Border(border.x, border.y, border.side), border);
	}

	private void unseeBorders(ImmutableList<Border> unseenBorders) {
		for (Border border : unseenBorders) {
			assert borders.containsKey(border) : border;
			borders.get(border).setVisible(false);
			addUnseenBorder(border);
		}

	}

	private void seeBorders(ImmutableList<RenderBorder> seenBorders) {
		for (RenderBorder border : seenBorders) {
			seeBorder(border);
			if (hasUnseenBorderObject(border) && border.getObject() == null) {
				removeUnseenBorder(border);
			}
		}
	}

	private void unseeCells(ImmutableList<Integer> unseenCells) {
		for (int key : unseenCells) {
			RenderCell cell = getCell(key);
			cell.setVisible(false);
			if (backendPlane.hasAnyItems(cell.x, cell.y)) {
				for (Item item : backendPlane.getItems(cell.x, cell.y)) {
					addUnseenItem(cell.x, cell.y, item);
				}
			}
		}
	}

	public void updateFieldOfView(EventFovChange event) {
		unseeCells(event.unseenCells);
		seeCells(event.seenCells);
		unseeBorders(event.unseenBorders);
		seeBorders(event.seenBorders);

	}

	private void seeCells(ImmutableList<RenderCell> seenCells) {
		for (RenderCell cell : seenCells) {
			seeCell(cell);
			if (hasAnyUnseenItems(cell.x, cell.y)) {
				removeUnseenItems(cell.x, cell.y);
			}
		}
	}

	/**
	 * Adds a new cell to subjective world (if a cell with that coordinates has not been seen yet), or sees an unseen
	 * cell.
	 *
	 * @param cell
	 * 	A new cell.
	 */
	public void seeCell(RenderCell cell) {
		assert cell != null;
		int key = cellHash(cell.x, cell.y);
		assert !cells.containsKey(key)
			|| !cells.get(key).isVisible()
			: "Cell " + cell.getX() + ":" + cell.getY() + " is already visible";
		cells.put(key, cell);
	}

	public boolean hasAnyUnseenItems(int x, int y) {
		return unseenItems.containsKey(cellHash(x, y));
	}

	/**
	 * Forgets about unseen items in a particular cell.
	 *
	 * @param x
	 * 	X coordinate of cell in world coordinates.
	 * @param y
	 * 	Y coordinate of cell in world coordinates.
	 */
	public void removeUnseenItems(int x, int y) {
		unseenItems.removeAll(cellHash(x, y));
	}

	/**
	 * Removes BorderObject from RenderBorder when you had seen there was an object, but after awhile saw that border
	 * again
	 * and there wasn't any object any more.
	 *
	 * @param border
	 */
	private void removeUnseenBorder(RenderBorder border) {
		unseenBorderObjects.remove(
			Chunk.cellHash(border.getX(), border.getY(), world.getHeight()),
			border.getSide()
		);
	}

	public void initFieldOfView(EventInitialTerrain e) {
		seeCells(e.seenCells);
		seeBorders(e.seenBorders);
	}
}
