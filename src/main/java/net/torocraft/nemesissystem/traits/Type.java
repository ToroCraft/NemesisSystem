package net.torocraft.nemesissystem.traits;

import java.util.Arrays;

public enum Type {

	/*
	 * attacks
	 */
	DOUBLE_MELEE(Affect.STRENGTH),
	ARROW(Affect.STRENGTH),
	SUMMON(Affect.STRENGTH),
	REFLECT(Affect.STRENGTH),
	//HEAT(Affect.STRENGTH),
	POTION(Affect.STRENGTH),
	TELEPORT(Affect.STRENGTH),
	//FIREBALL(Affect.STRENGTH),
	HEAL(Affect.STRENGTH),
	FIRE(Affect.STRENGTH),

	/*
	 * weaknesses
	 */
	HYDROPHOBIA(Affect.WEAKNESS),
	PYROPHOBIA(Affect.WEAKNESS),
	WOOD_ALLERGY(Affect.WEAKNESS),
	GOLD_ALLERGY(Affect.WEAKNESS),
	STONE_ALLERGY(Affect.WEAKNESS),
	GREEDY(Affect.WEAKNESS),
	GLUTTONY(Affect.WEAKNESS),
	CHICKEN(Affect.WEAKNESS);

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

	Type(Affect affect) {
		this.affect = affect;
	}

	private final Affect affect;

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
