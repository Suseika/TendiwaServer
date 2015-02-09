package org.tendiwa.demos.settlements;

import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.settlements.networks.SegmentNetwork;
import org.tendiwa.settlements.networks.algorithms.SegmentNetworkAlgorithms;

import java.awt.Color;

public class CityDrawer implements DrawingAlgorithm<SegmentNetwork> {

	@Override
	public void draw(SegmentNetwork segmentNetwork, DrawableInto canvas) {
//		Iterator<Color> colors = Iterators.cycle(Color.red, Color.blue, Color.green, Color.orange, Color.cyan, Color.black);
		segmentNetwork.getNetworks().stream()
			.forEach(c -> c.network().edgeSet().stream()
					.forEach(line -> {
//							canvas.drawRasterLine(line.start.toCell(), line.end.toCell(), colors.next());
					})
			);
		SegmentNetworkAlgorithms.createFullGraph(segmentNetwork)
			.edgeSet()
			.forEach(e -> canvas.drawRasterLine(e, Color.red));
//		for (Point2D vertex : city.getOriginalGraph().vertexSet()) {
//			canvas.draw(vertex, DrawingPoint2D.withColorAndSize(Color.orange, 8));
//		}
	}
}
