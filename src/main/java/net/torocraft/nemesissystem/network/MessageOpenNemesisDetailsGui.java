package net.torocraft.nemesissystem.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.discovery.NemesisKnowledge;
import net.torocraft.nemesissystem.gui.NemesisSystemGuiHandler;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class MessageOpenNemesisDetailsGui implements IMessage {

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

		private static void work(MessageOpenNemesisDetailsGui message) {
			NemesisSystem.NEMESIS = message.nemesis;
			NemesisSystem.KNOWLEDGE = message.knowledge;
			NemesisSystem.PROXY.openGui(NemesisSystemGuiHandler.NEMESIS_DETAILS_GUI);
		}
	}

}
