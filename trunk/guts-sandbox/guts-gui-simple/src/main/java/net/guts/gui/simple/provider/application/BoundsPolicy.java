package net.guts.gui.simple.provider.application;

public enum BoundsPolicy {

	PACK_AND_CENTER, //
	PACK_ONLY, //
	CENTER_ONLY, //
	KEEP_ORIGINAL_BOUNDS;//

	public boolean mustPack() {
		return this == PACK_AND_CENTER || this == PACK_ONLY;
	}

	public boolean mustCenter() {
		return this == PACK_AND_CENTER || this == CENTER_ONLY;
	}

}
