package org.tendiwa.settlements.buildings;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.tendiwa.geometry.Placeable;

import java.util.Collection;
import java.util.LinkedHashSet;

public class ArchitecturePolicyBuilder {
	private int minInstances = 0;
	private int maxInstances = Integer.MAX_VALUE;
	private final TObjectDoubleMap<Architecture> closeEnough = new TObjectDoubleHashMap<>();
	private ArchitecturePolicy.Priority priority = ArchitecturePolicy.Priority.DEFAULT;
	private Placeable allowedArea;
	private Collection<Architecture> presence = new LinkedHashSet<>();
	private Collection<Street> streets = new LinkedHashSet<>();

	public ArchitecturePolicyBuilder withMinInstances(int minInstances) {
		this.minInstances = minInstances;
		return this;
	}

	public ArchitecturePolicyBuilder withMaxInstances(int maxInstances) {
		this.maxInstances = maxInstances;
		return this;
	}

	public ArchitecturePolicyBuilder withCloseEnoughTo(Architecture architecture, int distance) {
		closeEnough.put(architecture, distance);
		return this;
	}

	public ArchitecturePolicyBuilder withPriority(ArchitecturePolicy.Priority priority) {
		this.priority = priority;
		return this;
	}

	public ArchitecturePolicyBuilder withInArea(Placeable area) {
		allowedArea = area;
		return this;
	}

	public ArchitecturePolicyBuilder closeEnoughTo(double distance, Architecture... architectures) {
		for (Architecture architecture : architectures) {
			if (closeEnough.containsKey(architecture)) {
				throw new ArchitectureError("There is already " + architecture.getClass().getName() + " in this policy");
			}
			closeEnough.put(architecture, distance);
		}
		return this;
	}

	/**
	 * AND operation.
	 *
	 * @param architectures
	 * @return
	 */
	public ArchitecturePolicyBuilder demandsPresence(Architecture... architectures) {
		for (Architecture architecture : architectures) {
			presence.add(architecture);
		}
		return this;
	}

	public ArchitecturePolicyBuilder onStreet(Street... streets) {
		for (Street street : streets) {
			this.streets.add(street);
		}
		return this;
	}

	public ArchitecturePolicy build() {
		return new ArchitecturePolicy(
			minInstances,
			maxInstances,
			closeEnough.isEmpty() ? closeEnough : null,
			priority,
			allowedArea,
			presence,
			streets
		);

	}

}