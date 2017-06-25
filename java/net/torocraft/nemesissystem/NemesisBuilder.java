package net.torocraft.nemesissystem;

public class NemesisBuilder {

	public static Nemesis build(String playerName, String mob, int level, int x, int z) {
		Nemesis nemesis = new Nemesis();

		//TODO random name
		nemesis.setName("BadDude " + System.currentTimeMillis());

		nemesis.setLevel(level);
		nemesis.setMob(mob);
		nemesis.setNemesisOf(playerName);
		nemesis.setX(x);
		nemesis.setZ(z);

		//TODO random stats

		//TODO random name

		//TODO random armor/weapons

		return nemesis;
	}



}
