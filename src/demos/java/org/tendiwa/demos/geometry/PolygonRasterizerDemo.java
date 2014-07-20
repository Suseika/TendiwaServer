package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.tendiwa.demos.Demos;
import org.tendiwa.demos.geometry.polygons.ConvexAndReflexAmoeba;
import org.tendiwa.demos.geometry.polygons.CutUpRing;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingCellSet;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.geometry.extensions.PolygonRasterizer;

import java.awt.Color;
import java.util.List;

public class PolygonRasterizerDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(PolygonRasterizerDemo.class, new DrawingModule());
	}


	@Override
	public void run() {
		List<Point2D> polygon = new ConvexAndReflexAmoeba();
		BoundedCellSet rasterizedPolygon = PolygonRasterizer.rasterize(polygon).toCellSet();
		canvas.draw(rasterizedPolygon, DrawingCellSet.withColor(Color.red));

	}
}