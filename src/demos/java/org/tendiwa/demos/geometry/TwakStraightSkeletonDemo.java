package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.extensions.PointTrail;

import java.util.List;

public class TwakStraightSkeletonDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(TwakStraightSkeletonDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
		TestCanvas.canvas = canvas;
		List<Point2D> outline = new PointTrail(200, 200)
			.moveBy(50, 0)
			.moveBy(0, -50)
			.moveBy(50, 0)
			.moveBy(0, -100)
			.moveBy(-150, 0)
			.moveBy(0, 80)
			.moveBy(50, 0)
			.points();
//		canvas.drawAll(
//			TwakStraightSkeleton.create(outline).cap(3),
//			DrawingGraph.basis(Color.gray, Color.red, Color .black)
//		);
	}
}
