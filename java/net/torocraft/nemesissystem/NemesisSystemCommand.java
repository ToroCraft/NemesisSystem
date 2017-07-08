package net.torocraft.nemesissystem;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class NemesisSystemCommand extends CommandBase {

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

		/*
		 * nemesis_system create frodare zombie 3
		 * nemesis_system list
		 * nemesis_system spawn name
		 */

		String command = args[0];

		switch (command) {
		case "create":
			create(server, sender, args);
			return;
		case "list":
			list(server, sender, args);
			return;
		case "clear":
			clear(server, sender, args);
			return;
		default:
			throw new WrongUsageException("commands.nemesis_system.usage");
		}
	}

	private void clear(MinecraftServer server, ICommandSender sender, String[] args) {
		NemesisRegistryProvider.get(server.getWorld(0)).clear();
	}

	private void list(MinecraftServer server, ICommandSender sender, String[] args) {
		// TODO dimID support
		List<Nemesis> l = NemesisRegistryProvider.get(server.getWorld(0)).list();
		StringBuilder s = new StringBuilder();
		for (Nemesis nemesis : l) {
			s.append(" * ").append(nemesis).append("\n");
		}
		notifyCommandListener(sender, this, "commands.nemesis_system.success.list", s.toString());
	}

	private void create(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length != 3) {
			throw new WrongUsageException("commands.nemesis_system.usage");
		}

		int x, z;

		if (sender instanceof EntityPlayer) {
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			x = player.getPosition().getX();
			z = player.getPosition().getZ();
		} else {
			x = 0;
			z = 0;
		}

		Nemesis nemesis = NemesisBuilder.build(args[1], i(args[2]), x, z);
		nemesis.register(server.getWorld(senderDimId(sender)));
		notifyCommandListener(sender, this, "commands.nemesis_system.success.create", nemesis.toString());
	}

	private int senderDimId(ICommandSender sender) {
		try{
			return getCommandSenderAsPlayer(sender).dimension;
		}catch(Exception e){
			return 0;
		}
	}

	private BlockPos senderPos(ICommandSender sender) {
		try{
			return getCommandSenderAsPlayer(sender).getPosition();
		}catch(Exception e){
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
			return getListOfStringsMatchingLastWord(args, "create", "list", "clear");
		}
		String command = args[0];
		switch (command) {
		case "create":
			return tabCompletionsForCreate(server, args);
		default:
			return Collections.emptyList();
		}
	}

	private List<String> tabCompletionsForSpawn(MinecraftServer server, ICommandSender sender, String[] args) {
		if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, NemesisRegistryProvider.get(server.getWorld(senderDimId(sender))).list());
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

}
