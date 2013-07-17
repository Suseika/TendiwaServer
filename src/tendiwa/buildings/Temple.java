package tendiwa.buildings;

import java.awt.Rectangle;

import tendiwa.core.Building;
import tendiwa.core.StaticData;
import tendiwa.core.TerrainBasics;
import tendiwa.core.meta.Coordinate;
import tendiwa.core.terrain.settlements.BuildingPlace;
import tendiwa.geometry.CardinalDirection;
import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.RectangleArea;
import tendiwa.geometry.RectangleSystem;

public class Temple extends Building {
	protected Temple(BuildingPlace bp, CardinalDirection side) {
		super(bp, side);
	}

	public static final long serialVersionUID = 801812251L;

	public void draw() {
		int wallGreyStone = StaticData.getObjectType("wall_gray_stone").getId();
		int objHumanAltar = StaticData.getObjectType("human_altar").getId();
		int objHumanTribune = StaticData.getObjectType("human_tribune").getId();
		int objStatueDefender1 = StaticData.getObjectType("statue_defender_1").getId();
		int objStatueFemaleElf3 = StaticData.getObjectType("statue_female_elf_3").getId();
		int objBench = StaticData.getObjectType("bench").getId();
		int objTree1 = StaticData.getFloorType("tree1").getId();
		int objChest1 = StaticData.getFloorType("chest1").getId();
		int objDoorBlue = StaticData.getObjectType("door_blue").getId();

		// DirectionToBERemoved dir;
		CardinalDirection side = CardinalDirection.N;
		// int lobbyWidth = 6;
		// if (side == SideTest.N || side == SideTest.S) {
		// dir = DirectionToBERemoved.H;
		// } else {
		// dir = DirectionToBERemoved.V;
		// }

		// For two of four sides we should revert width of cut rectangle
		RectangleSystem crs = new RectangleSystem(1);

		RectangleArea initial = crs.addRectangleArea(x, y, width, height);
		crs.cutRectangleFromSide(initial, side.opposite(), 4);

		// 0 - area behind altar, 1 - main area
		RectangleArea trees = crs.cutRectangleFromSide(initial, side.counterClockwiseQuarter(), 4);
		crs.excludeRectangle(trees);

		terrainModifier = settlement.getTerrainModifier(crs);
		buildBasis(wallGreyStone);
		setLobby(trees);

		/* CONTENT */
		// Althar and cathedra
		EnhancedRectangle mainRec = trees;
		EnhancedRectangle backRec = initial;
		Coordinate c = mainRec.getMiddleOfSide(side.opposite());
		Coordinate c2 = new Coordinate(c);
		c2.moveToSide(side, 1);
		settlement.setObject(c.x, c.y, objHumanAltar);
		settlement.setObject(c2.x, c2.y, objHumanTribune);

		// Benches
		c = mainRec.getCellFromSide(side, side.clockwiseQuarter(), 0);
		c2 = mainRec.getCellFromSide(side, side.counterClockwiseQuarter(), 0);
		int limit = (side.getOrientation().isVertical() ? mainRec.height : mainRec.width) - 5;
		for (int i = 0; i < limit; i++) {
			settlement.line(c.x, c.y, c2.x, c2.y, TerrainBasics.ELEMENT_OBJECT, objBench);
			c.moveToSide(side.opposite(), 2);
			c2.moveToSide(side.opposite(), 2);
		}

		// Passage from door to tribune
		c2 = mainRec.getMiddleOfSide(side.opposite());
		c2.moveToSide(side, 2);
		c = mainRec.getMiddleOfSide(side);
		settlement.line(c.x, c.y, c2.x, c2.y, TerrainBasics.ELEMENT_OBJECT, StaticData.VOID);

		// Door to back room
		c = mainRec.getCellFromSide(side.opposite(), side.counterClockwiseQuarter(), 1);
		c.moveToSide(side.opposite(), 1);
		settlement.setObject(c.x, c.y, objDoorBlue);

		// Front door
		c = mainRec.getMiddleOfSide(side);
		c.moveToSide(side, 1);
		settlement.setObject(c.x, c.y, objDoorBlue);

		// Statues
		c.moveToSide(side, 1);
		c.moveToSide(side.clockwise(), 1);
		settlement.setObject(c.x, c.y, objStatueDefender1);
		c.moveToSide(side.counterClockwise(), 2);
		settlement.setObject(c.x, c.y, objStatueFemaleElf3);

		// Trees behind temple
		Rectangle r = trees;
		for (int rx = r.x; rx < r.x + r.width; rx += 2) {
			for (int ry = r.y; ry < r.y + r.height; ry += 2) {
				settlement.setObject(rx, ry, objTree1);
			}
		}

		// Back door
		c = backRec.getCellFromSide(side.clockwiseQuarter(), side.opposite(), 1);
		c.moveToSide(side.clockwise(), 1);
		settlement.setObject(c.x, c.y, objDoorBlue);

		// Chests
		c = backRec.getCellFromSide(side.counterClockwiseQuarter(), side.opposite(), 0);
		while (backRec.contains(c)) {
			settlement.setObject(c.x, c.y, objChest1);
			c.moveToSide(side, 1);
		}

		// Ceilings
		for (EnhancedRectangle ceiling : terrainModifier.getRectangleSystem().rectangleSet()) {
			settlement.createCeiling(ceiling, 1);
		}
	}
	@Override
	public boolean fitsToPlace(BuildingPlace place) {
		return place.width > 14 || place.height > 14;
	}
}
