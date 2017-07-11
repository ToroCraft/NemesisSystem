package net.torocraft.nemesissystem.util;

import java.util.Random;

public class NameBuilder {

	private static final Random rand = new Random();

	public static String build() {
		return random(rand);
	}

	public static final String[] TITLES = {
			"Cookie Hoarder",
			"Block Breaker",
			"Sheep Shearer",
			"Bed Breaker",
			"Sleepy",
			"Square",
			"Late",
			"Slow",
			"Laggy",
			"Happy",
			"Grain Thief",
			"Tree Puncher",
			"Cobble-Head",
			"Slime-Encrusted",
			"Inflammable",
			"Door Buster",
			"Chicken Kicker",
			"Archer",
			"Ash-Skin",
			"Bag-Head",
			"Barrel-Scraper",
			"Beast Slayer",
			"Black-Blade",
			"Black-Heart",
			"Blade Master",
			"Blade Sharpener",
			"Blood-Lover",
			"Blood-Storm",
			"Bone-Licker",
			"Bone-Ripper",
			"Brawl-Master",
			"Brawler",
			"Cannibal",
			"Cave Rat",
			"Corpse-Eater",
			"Death-Blade",
			"Deathbringer",
			"Drooler",
			"Eagle Eye",
			"Evil Eye",
			"Fat Head",
			"Fire-Brander",
			"Flesh-Render",
			"Foul-Spawn",
			"Frog-Blood",
			"Giggles",
			"Head-Chopper",
			"Head-Hunter",
			"Heart-Eater",
			"Horn Blower",
			"Hot Tongs",
			"Life-Drinker",
			"Limp-Leg",
			"Literate One",
			"Long-Tooth",
			"Lucky Shot",
			"Mad-Eye",
			"Maggot-Nest",
			"Man-Hunter",
			"Man-Stalker",
			"Meat Hooks",
			"Metal-Beard",
			"Night-Bringer",
			"One-Eye",
			"Pig Fighter",
			"Plague-Bringer",
			"Pot-Licker",
			"Quick-Blades",
			"Rabble Rouser",
			"Raid Leader",
			"Raw-Head",
			"Runny-Bowls",
			"Sawbones",
			"Scar-Artist",
			"Shaman",
			"Shield Master",
			"Skull Bow",
			"Skull-Cracker",
			"Slashface",
			"Storm-Bringer",
			"Sword Master",
			"Advisor",
			"Amputator",
			"Assassin",
			"Beheader",
			"Biter",
			"Black",
			"Bleeder",
			"Bloated",
			"Bone Collector",
			"Bowmaster",
			"Brander",
			"Brave",
			"Breaker",
			"Brewer",
			"Brother",
			"Brown",
			"Catcher",
			"Choker",
			"Chunky",
			"Claw",
			"Clever",
			"Cook",
			"Corruptor",
			"Coward",
			"Crazy",
			"Cruel",
			"Dark",
			"Defender",
			"Defiler",
			"Destroyer",
			"Devourer",
			"Diseased",
			"Disgusting",
			"Drunk",
			"Dung Collector",
			"Elder",
			"Endless",
			"Executioner",
			"Fanatical",
			"Flesh Glutton",
			"Fool",
			"Foul",
			"Friendly",
			"Funny One",
			"Gentle",
			"Gorger",
			"Grinder",
			"Hacker",
			"Handsome",
			"Hell-Hawk",
			"Humiliator",
			"Hungry",
			"Immovable",
			"Infernal",
			"Judge",
			"Killer",
			"Kin-Slayer",
			"Knife",
			"Legend",
			"Loaded",
			"Lookout",
			"Mad",
			"Man-Eater",
			"Meat Hoarder",
			"Merciful",
			"Messenger",
			"Mindless",
			"Mountain",
			"Painted",
			"Proud",
			"Puny",
			"Rash",
			"Rat",
			"Raven",
			"Red",
			"Relentless",
			"Ruinous",
			"Runner",
			"Runt",
			"Savage",
			"Scholar",
			"Screamer",
			"Serpent",
			"Shadow",
			"Shield",
			"Skinless",
			"Slasher",
			"Slaughterer",
			"Small",
			"Smasher",
			"Spike",
			"Stinger",
			"Stout",
			"Surgeon",
			"Swift",
			"Tongue",
			"Trainer",
			"Unkillable",
			"Vile",
			"Wanderer",
			"Watcher",
			"Weak",
			"Whiner",
			"Wise",
			"Wrestler",
			"Thunderhead",
			"Tree-Killer",
			"Ugly Face",
			"Lice-Head"
	};

	private static final String[] PARTS1 = { "a", "e", "i", "o", "u"};

	private static final String[] PARTS2 = { "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "y", "z", "br", "cr", "dr", "fr", "gr", "kr", "pr", "qr", "sr", "tr", "vr", "wr", "yr", "zr", "str",
			"bl", "cl", "fl", "gl", "kl", "pl", "sl", "vl", "yl", "zl", "ch", "kh", "ph", "sh", "yh", "zh" };

	private static final String[] PARTS3 = { "a", "e", "i", "o", "u", "ae", "ai", "ao", "au", "aa", "ee", "ea", "ei", "eo", "eu", "ia", "ie", "io", "iu", "oa", "oe", "oi", "oo", "ou", "ua", "ue", "ui", "uo", "uu", "a", "e", "i", "o", "u",
			"a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u", "a", "e", "i", "o", "u" };

	private static final String[] PARTS4 = { "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "y", "z", "br", "cr", "dr", "fr", "gr", "kr", "pr", "tr", "vr", "wr", "zr", "st", "bl", "cl", "fl",
			"gl", "kl", "pl", "sl", "vl", "zl", "ch", "kh", "ph", "sh", "zh" };

	private static final String[] PARTS5 = { "c", "d", "f", "h", "k", "l", "m", "n", "p", "r", "s", "t", "x", "y"};

	private static final String[] PARTS6 = { "aco", "ada", "adena", "ago", "agos", "aka", "ale", "alo", "am", "anbu", "ance", "and", "ando", "ane", "ans", "anta", "arc", "ard", "ares", "ario", "ark", "aso", "athe", "eah", "edo", "ego",
			"eigh", "eim", "eka", "eles", "eley", "ence", "ens", "ento", "erton", "ery", "esa", "ester", "ey", "ia", "ico", "ido", "ila", "ille", "in", "inas", "ine", "ing", "irie", "ison", "ita", "ock", "odon", "oit", "ok", "olis", "olk",
			"oln", "ona", "oni", "onio", "ont", "ora", "ord", "ore", "oria", "ork", "osa", "ose", "ouis", "ouver", "ul", "urg" };

	private static final String[] PARTS7 = { "bert", "bus", "by", "dence", "diff", "ding", "don", "fast", "ford", "gan", "gas", "gate", "gend",
			"ginia", "gow", "hull", "las", "ledo", "lens", "ling", "mont", "more", "mouth", "nard", "phia", "phis", "polis",   "pus",  "rith", "ron", "rora", "ross", "rough", "sa",
			"sall", "sas", "sea", "set", "sey",  "son", "stin", "ta", "tin", "tol", "ton", "vale", "ver",  "vine", "ving", "well" };

	public static String random(Random rand) {
		StringBuilder buf = new StringBuilder();
		switch(rand.nextInt(4)) {
			case 0:
				buf.append(choose(rand, PARTS4));
				buf.append(choose(rand, PARTS6));
				break;
			case 1:
				buf.append(choose(rand, PARTS1));
				buf.append(choose(rand, PARTS2));
				buf.append(choose(rand, PARTS1));
				buf.append(choose(rand, PARTS7));
				break;
			case 2:
				buf.append(choose(rand, PARTS1));
				buf.append(choose(rand, PARTS2));
				buf.append(choose(rand, PARTS7));
				break;
			case 3:
				buf.append(choose(rand, PARTS5));
				buf.append(choose(rand, PARTS1));
				buf.append(choose(rand, PARTS7));
				break;
		}
		String name = buf.toString();
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	private static String choose(Random rand, String[] parts) {
		return parts[rand.nextInt(parts.length)];
	}
}
