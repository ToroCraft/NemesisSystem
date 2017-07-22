package net.torocraft.nemesissystem.traits;

import java.util.Arrays;

public enum Type {

	/*
	 * attacks
	 */
	DOUBLE_MELEE(Affect.STRENGTH, 1),
	ARROW(Affect.STRENGTH, 1),
	SUMMON(Affect.STRENGTH, 1),
	REFLECT(Affect.STRENGTH, 1),
	POTION(Affect.STRENGTH, 1),
	TELEPORT(Affect.STRENGTH, 1),
	HEAL(Affect.STRENGTH, 1),
	FIRE(Affect.STRENGTH, 1),

	/*
	 * weaknesses
	 */
	HYDROPHOBIA(Affect.WEAKNESS, 1),
	PYROPHOBIA(Affect.WEAKNESS, 1),
	WOOD_ALLERGY(Affect.WEAKNESS, 8),
	GOLD_ALLERGY(Affect.WEAKNESS, 4),
	STONE_ALLERGY(Affect.WEAKNESS, 5),
	GREEDY(Affect.WEAKNESS, 1),
	GLUTTONY(Affect.WEAKNESS, 1),
	CHICKEN(Affect.WEAKNESS, 1);

	//TODO  AMOROUS, DANCE, PLASMOPHOBIA, ICHTHYOPHOBIA, ANIMAL_LOVER

	public final static Type[] STRENGTHS;
	public final static Type[] WEAKNESSES;

	static {
		STRENGTHS = filterByAffect(Affect.STRENGTH);
		WEAKNESSES = filterByAffect(Affect.WEAKNESS);
	}

	private static Type[] filterByAffect(Affect affect) {
		return Arrays.stream(Type.values()).filter((Type t) -> t.getAffect().equals(affect)).toArray(Type[]::new);
	}

	Type(Affect affect, int maxLevel) {
		this.affect = affect;
		this.maxLevel = maxLevel;
	}

	private final Affect affect;
	private final int maxLevel;

	public Affect getAffect() {
		return affect;
	}

	public static boolean isStrength(Type type) {
		return Affect.STRENGTH.equals(type.affect);
	}

	public static boolean isWeakness(Type type) {
		return Affect.WEAKNESS.equals(type.affect);
	}
}
