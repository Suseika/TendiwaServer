package org.tendiwa.settlements;

import java.util.Set;

public class InnerEnclosedBlocksIndex {
	private final Set<EnclosedBlock> blocks;

	public InnerEnclosedBlocksIndex(Set<EnclosedBlock> blocks) {
		this.blocks = blocks;
	}

	public boolean contains(EnclosedBlock block) {
//		Set<EnclosedBlock> set = new HashSet(blocks);
		return true;
	}
}