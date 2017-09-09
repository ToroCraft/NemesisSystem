package net.torocraft.nemesissystem.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.nemesissystem.NemesisSystem;

public class MessageOpenNemesisGuiRequest implements IMessage {

	public static void init(int packetId) {
		NemesisSystem.NETWORK.registerMessage(MessageOpenNemesisGuiRequest.Handler.class, MessageOpenNemesisGuiRequest.class, packetId, Side.SERVER);
	}

	public MessageOpenNemesisGuiRequest() {

	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class Handler implements IMessageHandler<MessageOpenNemesisGuiRequest, IMessage> {
		@Override
		public IMessage onMessage(final MessageOpenNemesisGuiRequest message, MessageContext ctx) {
			final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			player.getServerWorld().addScheduledTask(() -> sendResponse(player));
			return null;
		}

		private static void sendResponse(EntityPlayerMP player) {
			NemesisSystem.NETWORK.sendTo(new MessageOpenNemesisGui(player), player);
		}
	}

}
