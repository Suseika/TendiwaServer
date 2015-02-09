package org.tendiwa.settlements.networks;

final class NetworkGenerationParameters {
	final int roadsFromPoint;
	final double connectivity;
	final double roadSegmentLength;
	final double snapSize;
	final int maxStartPointsPerCell;
	final double secondaryRoadNetworkDeviationAngle;
	final double secondaryRoadNetworkRoadLengthDeviation;
	final boolean favourAxisAlignedSegments;

	/**
	 * @param roadsFromPoint
	 * 	[Kelly figure 42, variable ParamDegree]
	 * 	<p>
	 * 	How many lines would normally go from one point of secondary road network.
	 * @param connectivity
	 * 	[Kelly figure 42, variable ParamConnectivity]
	 * 	<p>
	 * 	How likely it is to snap to node or road when possible. When connectivity == 1.0, algorithm will always
	 * 	snap when possible. When connectivity == 0.0, algorithm will never snap.
	 * @param roadSegmentLength
	 * 	[Kelly figure 42, variable ParamSegmentLength]
	 * 	<p>
	 * 	Mean length of secondary network roads.
	 * @param snapSize
	 * 	[Kelly figure 42, variable ParamSnapSize]
	 * 	<p>
	 * 	A radius around secondary roads' end points inside which new end points would snap to existing ones.
	 * @param maxStartPointsPerCell
	 * 	Number of starting points for road generation in each {@link org.tendiwa.settlements.networks.NetworkWithinCycle}.
	 * 	Must be 0 or greater.
	 * 	<p>
	 * 	In [Kelly figure 43] there are 2 starting points.
	 * 	<p>
	 * 	A NetworkWithinCycle is not guaranteed to have exactly {@code maxRoadsFromPoint} starting roads, because
	 * 	such amount might not fit into a cell.
	 * 	<p>
	 * @param secondaryRoadNetworkDeviationAngle
	 * 	An angle in radians. How much should the secondary network roads should be deviated from the "ideal" net
	 * 	("ideal" is when this parameter is 0.0).
	 * 	<p>
	 * 	Kelly doesn't have this as a parameter, it is implied in [Kelly figure 42] under "deviate newDirection"
	 * 	and "calculate deviated boundaryRoad perpendicular".
	 */
	NetworkGenerationParameters(

		int roadsFromPoint,
		double connectivity,
		double roadSegmentLength,
		double snapSize,
		int maxStartPointsPerCell,
		double secondaryRoadNetworkDeviationAngle,
		double secondaryRoadNetworkRoadLengthDeviation,
		boolean favourAxisAlignedSegments
	) {
		if (Math.abs(secondaryRoadNetworkDeviationAngle) >= Math.PI * 2) {
			throw new IllegalArgumentException("secondaryRoadNetworkDeviationAngle must be in [0; Math.PI*2)");
		}
		if (Math.abs(secondaryRoadNetworkRoadLengthDeviation) >= roadSegmentLength) {
			throw new IllegalArgumentException("secondaryRoadNetworkRoadLengthDeviation can't be greater than " +
				"roadSegmentLength (the former is " + secondaryRoadNetworkDeviationAngle + ", " +
				"the latter is " + roadSegmentLength + ")");
		}
		if (connectivity < 0 || connectivity > 1) {
			throw new IllegalArgumentException("Connectivity must be in range [0.0; 1.0]");
		}
		if (maxStartPointsPerCell < 0) {
			throw new IllegalArgumentException("NumOfStartPoints must be at least 0");
		}
		this.roadsFromPoint = roadsFromPoint;
		this.connectivity = connectivity;
		this.roadSegmentLength = roadSegmentLength;
		this.snapSize = snapSize;
		this.maxStartPointsPerCell = maxStartPointsPerCell;
		this.secondaryRoadNetworkDeviationAngle = secondaryRoadNetworkDeviationAngle;
		this.secondaryRoadNetworkRoadLengthDeviation = secondaryRoadNetworkRoadLengthDeviation;
		this.favourAxisAlignedSegments = favourAxisAlignedSegments;
	}
}
