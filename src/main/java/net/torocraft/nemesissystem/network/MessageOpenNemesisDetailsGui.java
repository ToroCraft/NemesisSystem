package net.torocraft.nemesissystem.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.discovery.NemesisDiscovery;
import net.torocraft.nemesissystem.discovery.NemesisKnowledge;
import net.torocraft.nemesissystem.gui.NemesisSystemGuiHandler;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.util.nbt.NbtData;
import net.torocraft.nemesissystem.util.nbt.NbtSerializer;

public class MessageOpenNemesisDetailsGui implements IMessage {
	@SideOnly(Side.CLIENT)
	public static NemesisEntry NEMESIS;

	@SideOnly(Side.CLIENT)
	public static NemesisKnowledge KNOWLEDGE;

	private NemesisEntry nemesis;
	private NemesisKnowledge knowledge;

	public static void init(int packetId) {
		NemesisSystem.NETWORK.registerMessage(MessageOpenNemesisDetailsGui.Handler.class, MessageOpenNemesisDetailsGui.class, packetId, Side.CLIENT);
	}

	public MessageOpenNemesisDetailsGui() {

	}

	public MessageOpenNemesisDetailsGui(NemesisEntry nemesis, NemesisKnowledge knowledge) {
		this.nemesis = nemesis;
		this.knowledge = knowledge;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nemesis = NemesisEntry.load(ByteBufUtils.readTag(buf));
		knowledge = NemesisKnowledge.load(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, NemesisEntry.save(nemesis));
		ByteBufUtils.writeTag(buf, NemesisKnowledge.save(knowledge));
	}

	public static class Handler implements IMessageHandler<MessageOpenNemesisDetailsGui, IMessage> {
		@Override
		public IMessage onMessage(final MessageOpenNemesisDetailsGui message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> work(message));
			return null;
		}
	}

	private static void work(MessageOpenNemesisDetailsGui message) {
		NEMESIS = message.nemesis;
		KNOWLEDGE = message.knowledge;
		EntityPlayer player = Minecraft.getMinecraft().player;
		player.openGui(NemesisSystem.INSTANCE, NemesisSystemGuiHandler.NEMESIS_DETAILS_GUI, player.world, (int) player.posX, (int) player.posY,
				(int) player.posZ);
	}


}
