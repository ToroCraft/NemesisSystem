package net.torocraft.nemesissystem.network;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class MessageOpenNemesisDetailsGuiRequest implements IMessage {

	private UUID nemesisId;

	public static void init(int packetId) {
		NemesisSystem.NETWORK
				.registerMessage(MessageOpenNemesisDetailsGuiRequest.Handler.class, MessageOpenNemesisDetailsGuiRequest.class, packetId, Side.SERVER);
	}

	public MessageOpenNemesisDetailsGuiRequest() {

	}

	public MessageOpenNemesisDetailsGuiRequest(UUID nemesisId) {
		this.nemesisId = nemesisId;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nemesisId = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, nemesisId.toString());
	}

	public static class Handler implements IMessageHandler<MessageOpenNemesisDetailsGuiRequest, IMessage> {
		@Override
		public IMessage onMessage(final MessageOpenNemesisDetailsGuiRequest message, MessageContext ctx) {
			final EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> sendResponse(message, player));
			return null;
		}
	}

	private static void sendResponse(MessageOpenNemesisDetailsGuiRequest message, EntityPlayerMP player) {
		NemesisEntry nemesis = NemesisRegistryProvider.get(player.world).getById(message.nemesisId);
		System.out.println("MessageOpenNemesisDetailsGuiRequest.handle: " + nemesis);
		NemesisSystem.NETWORK.sendTo(new MessageOpenNemesisDetailsGui(nemesis), player);
	}
}
