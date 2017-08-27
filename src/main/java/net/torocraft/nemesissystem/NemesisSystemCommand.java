package net.torocraft.nemesissystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.nemesissystem.discovery.NemesisKnowledge;
import net.torocraft.nemesissystem.handlers.SpawnHandler;
import net.torocraft.nemesissystem.network.MessageOpenNemesisDetailsGui;
import net.torocraft.nemesissystem.network.MessageOpenNemesisGui;
import net.torocraft.nemesissystem.registry.INemesisRegistry;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;
import net.torocraft.nemesissystem.traits.Trait;
import net.torocraft.nemesissystem.traits.Type;
import net.torocraft.nemesissystem.util.DiscoveryUtil;
import net.torocraft.nemesissystem.util.EntityDecorator;
import net.torocraft.nemesissystem.util.NemesisActions;
import net.torocraft.nemesissystem.util.NemesisBuilder;
import net.torocraft.nemesissystem.util.NemesisUtil;
import net.torocraft.nemesissystem.util.SpawnUtil;

public class NemesisSystemCommand extends CommandBase {

	private static final UUID TEST_ID = UUID.fromString("2027e16a-6edd-11e7-907b-a6006ad3dba0");

	@Override
	@Nonnull
	public String getName() {
		return "nemesis_system";
	}

	@Override
	@Nonnull
	public String getUsage(@Nullable ICommandSender sender) {
		return "commands.nemesis_system.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {

		if (args.length < 1) {
			throw new WrongUsageException("commands.nemesis_system.usage");
		}

		String command = args[0];

		switch (command) {
		case "create":
			create(server, sender, args);
			return;
		case "spawn":
			spawn(sender, args);
			return;
		case "create_test":
			createTest(sender);
			return;
		case "spawn_test":
			spawnTest(sender);
			return;
		case "list":
			list(server, sender);
			return;
		case "clear":
			clear(server);
			return;
		case "duelIfCrowded":
			duel(sender);
			return;
		case "gui":
			gui(sender, args);
			return;
		case "enchant":
			enchant(sender);
			return;
		case "promote":
			promote(server, sender, args);
			return;
		case "demote":
			demote(server, sender, args);
			return;
		case "give_book":
			giveBook(sender);
			return;
		default:
			throw new WrongUsageException("commands.nemesis_system.usage");
		}
	}

	private void giveBook(ICommandSender sender) throws CommandException {
		if (!(sender instanceof EntityPlayer)) {
			return;
		}

		EntityPlayer player = getCommandSenderAsPlayer(sender);
		World world = player.world;

		ItemStack book = DiscoveryUtil.createUnreadBook();
		EntityItem bookEntity = new EntityItem(world, player.posX, player.posY, player.posZ, book);
		world.spawnEntity(bookEntity);
	}

	private void spawn(ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayer)) {
			return;
		}

		if (args.length != 2) {
			throw new WrongUsageException("commands.nemesis_system.usage");
		}

		EntityPlayer player = getCommandSenderAsPlayer(sender);
		World world = player.world;

		INemesisRegistry registry = NemesisRegistryProvider.get(world);
		NemesisEntry nemesis = registry.getByName(args[1]);

		SpawnHandler.spawnNemesis(world, player.getPosition(), nemesis);
	}

	private void spawnTest(ICommandSender sender) throws CommandException {
		if (!(sender instanceof EntityPlayer)) {
			return;
		}

		EntityPlayer player = getCommandSenderAsPlayer(sender);
		World world = player.world;
		NemesisEntry nemesis = NemesisRegistryProvider.get(world).getById(TEST_ID);

		if (nemesis == null) {
			System.out.println("NemesisEntry is null, run /nemesis_system create_test");
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

	private void createTest(ICommandSender sender) throws CommandException {
		if (!(sender instanceof EntityPlayer)) {
			return;
		}

		EntityPlayer player = getCommandSenderAsPlayer(sender);
		World world = player.world;
		int x = player.getPosition().getX();
		int z = player.getPosition().getZ();

		NemesisEntry nemesis = new NemesisEntry();

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
		//nemesis.getTraits().add(new Trait(Type.ARCHER, 4));
		//nemesis.getTraits().add(new Trait(Type.FIREBALL, 4));
		nemesis.getTraits().add(new Trait(Type.GLUTTONY, 1));
		nemesis.getTraits().add(new Trait(Type.GREEDY, 1));
		nemesis.getTraits().add(new Trait(Type.CHICKEN, 1));

		INemesisRegistry registry = NemesisRegistryProvider.get(world);
		if (registry.getById(TEST_ID) == null) {
			registry.register(nemesis);
			System.out.println("created test nemesis: " + nemesis);
		} else {
			registry.update(nemesis);
			System.out.println("updated test nemesis: " + nemesis);
		}
	}

	private void demote(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length != 2) {
			throw new WrongUsageException("commands.nemesis_system.usage");
		}
		World world = server.getWorld(senderDimId(sender));
		INemesisRegistry registry = NemesisRegistryProvider.get(world);
		NemesisEntry nemesis = registry.getByName(args[1]);
		if (nemesis == null) {
			return;
		}
		NemesisActions.demote(world, nemesis, "Server Command");
		registry.markDirty();
	}

	private void promote(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length != 2) {
			throw new WrongUsageException("commands.nemesis_system.usage");
		}
		World world = server.getWorld(senderDimId(sender));
		INemesisRegistry registry = NemesisRegistryProvider.get(world);
		NemesisEntry nemesis = registry.getByName(args[1]);
		if (nemesis == null) {
			return;
		}
		NemesisActions.promote(world, nemesis);
		registry.markDirty();
	}

	private void enchant(ICommandSender sender) throws CommandException {
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

	private void duel(ICommandSender sender) {
		if (sender instanceof EntityPlayer) {
			NemesisActions.duelIfCrowded(sender.getEntityWorld(), null, false);
		}
	}

	private void gui(ICommandSender sender, String[] args) throws CommandException {

		if (!(sender instanceof EntityPlayer)) {
			return;
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		if (args.length == 1) {
			NemesisSystem.NETWORK.sendTo(new MessageOpenNemesisGui(player), player);
			return;
		}

		if (args.length == 2) {
			NemesisEntry nemesis = NemesisRegistryProvider.get(player.world).getByName(args[1]);
			NemesisKnowledge knowledge = DiscoveryUtil.getGetPlayerKnowledgeOfNemesis(player, nemesis.getId());
			if (knowledge == null) {
				knowledge = new NemesisKnowledge();
			}
			NemesisSystem.NETWORK.sendTo(new MessageOpenNemesisDetailsGui(nemesis, knowledge), player);
			return;
		}

		throw new WrongUsageException("commands.nemesis_system.usage");
	}

	private void clear(MinecraftServer server) {
		NemesisRegistryProvider.get(server.getWorld(0)).clear();
	}

	private void list(MinecraftServer server, ICommandSender sender) {
		List<NemesisEntry> l = NemesisRegistryProvider.get(server.getWorld(0)).list();
		StringBuilder s = new StringBuilder();
		for (NemesisEntry nemesis : l) {
			s.append(" * ");
			if (nemesis.isDead()) {
				s.append(" DEAD ");
			}
			if (nemesis.isSpawned()) {
				s.append(" SPAWNED ");
			}
			s.append(nemesis);
			s.append(" ").append(nemesis.getX()).append(",").append(nemesis.getZ());

			long now = server.getWorld(0).getTotalWorldTime();
			long lastSpawned = nemesis.getLastSpawned() == null ? 0 : nemesis.getLastSpawned();
			long spawnDelay = (lastSpawned + SpawnHandler.SPAWN_COOLDOWN_PERIOD) - now;

			if (spawnDelay > 0) {
				s.append(" spawnDelay[").append(spawnDelay).append("] ");
			}

			s.append(" level[").append(nemesis.getLevel()).append("] ");

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

		NemesisEntry nemesis = NemesisBuilder
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

	private int i(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return 1;
		}
	}

	@Override
	@Nonnull
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "create", "list", "clear", "gui", "duelIfCrowded", "promote", "spawn", "demote",
					"give_book");
		}
		String command = args[0];
		switch (command) {
		case "create":
			return tabCompletionsForCreate(args);
		case "promote":
		case "demote":
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

	private List<String> tabCompletionsForCreate(String[] args) {

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
		List<NemesisEntry> nemeses = NemesisRegistryProvider.get(server.getWorld(senderDimId(sender))).list();
		return nemeses.stream().map(NemesisEntry::getName).collect(Collectors.toList());
	}

}
