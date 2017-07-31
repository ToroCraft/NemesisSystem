package net.torocraft.nemesissystem.discovery;

import java.util.UUID;

/**
 * This class represents a piece of information that has been discovered about an nemesis.
 */
public class NemesisDiscovery {

	public enum Type {NAME, LOCATION, TRAIT}

	public UUID nemesisId;

	public Type type;

	/**
	 * this field is used to hold the index of the discovered trait (type=TRAIT)
	 */
	public int index;

}
