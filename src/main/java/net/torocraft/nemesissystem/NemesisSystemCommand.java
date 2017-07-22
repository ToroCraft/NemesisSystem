package net.torocraft.nemesissystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.handlers.Spawn;
import net.torocraft.nemesissystem.network.MessageOpenNemesisDetailsGui;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGui;
import net.torocraft.nemesissystem.registry.INemesisRegistry;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.nemesissystem.traits.Trait;
import net.torocraft.nemesissystem.traits.Type;
import net.torocraft.nemesissystem.util.EntityDecorator;
import net.torocraft.nemesissystem.util.NemesisActions;
import net.torocraft.nemesissystem.util.NemesisBuilder;
import net.torocraft.nemesissystem.util.NemesisUtil;
import net.torocraft.nemesissystem.util.SpawnUtil;

public class NemesisSystemCommand extends CommandBase {

	private static final UUID TEST_ID = UUID.fromString("2027e16a-6edd-11e7-907b-a6006ad3dba0");

	@Override
	public String getName() {
		return "nemesis_system";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.nemesis_system.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		if (args.length < 1) {
			throw new WrongUsageException("commands.nemesis_system.usage");
		}

		String command = args[0];

		switch (command) {
		case "create":
			create(server, sender, args);
			return;
		case "spawn":
			spawn(server, sender, args);
			return;
		case "create_test":
			createTest(server, sender, args);
			return;
		case "spawn_test":
			spawnTest(server, sender, args);
			return;
		case "list":
			list(server, sender, args);
			return;
		case "clear":
			clear(server, sender, args);
			return;
		case "duelIfCrowded":
			duel(server, sender, args);
			return;
		case "gui":
			gui(server, sender, args);
			return;
		case "enchant":
			enchant(server, sender, args);
			return;
		case "promote":
			promote(server, sender, args);
			return;
		default:
			throw new WrongUsageException("commands.nemesis_system.usage");
		}
	}

	private void spawn(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayer)) {
			return;
		}

		if (args.length != 2) {
			throw new WrongUsageException("commands.nemesis_system.usage");
		}

		EntityPlayer player = getCommandSenderAsPlayer(sender);
		World world = player.world;

		INemesisRegistry registry = NemesisRegistryProvider.get(world);
		Nemesis nemesis = registry.getByName(args[1]);

		Spawn.spawnNemesis(world, player.getPosition(), nemesis);
	}

	private void spawnTest(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayer)) {
			return;
		}

		EntityPlayer player = getCommandSenderAsPlayer(sender);
		World world = player.world;
		Nemesis nemesis = NemesisRegistryProvider.get(world).getById(TEST_ID);

		if (nemesis == null) {
			System.out.println("Nemesis is null, run /nemesis_system create_test");
			return;
		}

		Entity entity = SpawnUtil.getEntityFromString(world, nemesis.getMob());

		if (entity == null) {
			return;
		}

		EntityDecorator.decorate((EntityCreature) entity, nemesis);
		entity.addTag(NemesisSystem.TAG_SPAWNING);
		SpawnUtil.spawnEntityLiving(world, (EntityCreature) entity, player.getPosition(), 0);
	}


	private void createTest(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayer)) {
			return;
		}

		EntityPlayer player = getCommandSenderAsPlayer(sender);
		World world = player.world;
		int x = player.getPosition().getX();
		int z = player.getPosition().getZ();

		Nemesis nemesis = new Nemesis();

		nemesis.setId(TEST_ID);
		nemesis.setName(NemesisBuilder.getUniqueName(world) + " of Test");
		nemesis.setTitle(NemesisBuilder.getUniqueTitle(world));

		nemesis.setLevel(10);
		nemesis.setMob("minecraft:zombie");
		nemesis.setChild(0);
		nemesis.setX(x);
		nemesis.setZ(z);
		nemesis.setDimension(player.dimension);

		nemesis.setTraits(new ArrayList<>());
		//nemesis.getTraits().add(new Trait(Type.WOOD_ALLERGY, 5));
		nemesis.getTraits().add(new Trait(Type.ARROW, 5));

		INemesisRegistry registry = NemesisRegistryProvider.get(world);
		if(registry.getById(TEST_ID) == null){
			registry.register(nemesis);
			System.out.println("created test nemesis: " + nemesis);
		} else {
			registry.update(nemesis);
			System.out.println("updated test nemesis: " + nemesis);
		}
	}

	private void promote(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length != 2) {
			throw new WrongUsageException("commands.nemesis_system.usage");
		}
		World world = server.getWorld(senderDimId(sender));
		INemesisRegistry registry = NemesisRegistryProvider.get(world);
		Nemesis nemesis = registry.getByName(args[1]);
		if (nemesis == null) {
			return;
		}
		NemesisActions.promote(world, nemesis);
		registry.markDirty();
	}

	private void enchant(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (sender instanceof EntityPlayer) {
			NemesisUtil.enchantItems(getHotBarItems(getCommandSenderAsPlayer(sender)));
			logHotBarItems(getCommandSenderAsPlayer(sender));
		}
	}

	private void logHotBarItems(EntityPlayer player) {
		InventoryPlayer inv = player.inventory;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (InventoryPlayer.isHotbar(i)) {
				ItemStack stack = inv.getStackInSlot(i);
				System.out.println(stack.getTagCompound());
			}
		}
	}

	private List<ItemStack> getHotBarItems(EntityPlayer player) {
		InventoryPlayer inv = player.inventory;
		List<ItemStack> items = new ArrayList<>();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (InventoryPlayer.isHotbar(i)) {
				items.add(inv.getStackInSlot(i));
			}
		}
		return items;
	}

	private void duel(MinecraftServer server, ICommandSender sender, String[] args) {
		if (sender instanceof EntityPlayer) {
			NemesisActions.duelIfCrowded(sender.getEntityWorld(), null, false);
		}
	}

	private void gui(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		if (!(sender instanceof EntityPlayer)) {
			return;
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		if (args.length == 1) {
			NemesisSystem.NETWORK.sendTo(new MessageOpenNemesisGui(player), player);
			return;
		}

		if (args.length == 2) {
			Nemesis nemesis = NemesisRegistryProvider.get(player.world).getByName(args[1]);
			NemesisSystem.NETWORK.sendTo(new MessageOpenNemesisDetailsGui(nemesis), player);
			return;
		}

		throw new WrongUsageException("commands.nemesis_system.usage");
	}

	private void clear(MinecraftServer server, ICommandSender sender, String[] args) {
		NemesisRegistryProvider.get(server.getWorld(0)).clear();
	}

	private void list(MinecraftServer server, ICommandSender sender, String[] args) {
		List<Nemesis> l = NemesisRegistryProvider.get(server.getWorld(0)).list();
		StringBuilder s = new StringBuilder();
		for (Nemesis nemesis : l) {
			s.append(" * ");
			if (nemesis.isDead()) {
				s.append(" DEAD ");
			}
			s.append(nemesis);
			s.append(" ").append(nemesis.getX()).append(",").append(nemesis.getZ());
			s.append("\n");
		}
		notifyCommandListener(sender, this, "commands.nemesis_system.success.list", s.toString());
	}

	private void create(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length != 3) {
			throw new WrongUsageException("commands.nemesis_system.usage");
		}

		int x, z, dimension;

		if (sender instanceof EntityPlayer) {
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			x = player.getPosition().getX();
			z = player.getPosition().getZ();
			dimension = player.dimension;
		} else {
			x = 0;
			z = 0;
			dimension = 0;
		}

		Nemesis nemesis = NemesisBuilder
				.build(sender.getEntityWorld(), args[1], sender.getEntityWorld().rand.nextBoolean(), dimension, i(args[2]), x, z);
		nemesis.register(server.getWorld(senderDimId(sender)));
		notifyCommandListener(sender, this, "commands.nemesis_system.success.create", nemesis.toString());
	}

	private int senderDimId(ICommandSender sender) {
		try {
			return getCommandSenderAsPlayer(sender).dimension;
		} catch (Exception e) {
			return 0;
		}
	}

	private BlockPos senderPos(ICommandSender sender) {
		try {
			return getCommandSenderAsPlayer(sender).getPosition();
		} catch (Exception e) {
			return BlockPos.ORIGIN;
		}
	}

	private int i(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return 1;
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "create", "list", "clear", "gui", "duelIfCrowded", "promote", "spawn");
		}
		String command = args[0];
		switch (command) {
		case "create":
			return tabCompletionsForCreate(server, args);
		case "promote":
		case "spawn":
		case "gui":
			return tabCompletionsForName(server, sender, args);
		default:
			return Collections.emptyList();
		}
	}

	private List<String> tabCompletionsForName(MinecraftServer server, ICommandSender sender, String[] args) {
		if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, getNemesisNames(server, sender));
		}
		return Collections.emptyList();
	}

	private List<String> tabCompletionsForSpawn(MinecraftServer server, ICommandSender sender, String[] args) {
		if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, getNemesisNames(server, sender));
		}
		return Collections.emptyList();
	}

	private List<String> tabCompletionsForCreate(MinecraftServer server, String[] args) {

		if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList());
		}

		if (args.length == 3) {
			String[] levels = new String[10];
			for (int i = 0; i < 10; i++) {
				levels[i] = Integer.toString(i, 10);
			}
			return getListOfStringsMatchingLastWord(args, levels);
		}

		return Collections.emptyList();
	}

	private List<String> getNemesisNames(MinecraftServer server, ICommandSender sender) {
		List<Nemesis> nemeses = NemesisRegistryProvider.get(server.getWorld(senderDimId(sender))).list();
		return nemeses.stream().map(Nemesis::getName).collect(Collectors.toList());
	}

}
