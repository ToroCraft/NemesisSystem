package net.torocraft.nemesissystem.network;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class MessageSyncNemesisRequest implements IMessage {

	private UUID entityUuid;

	public static void init(int packetId) {
		NemesisSystem.NETWORK.registerMessage(MessageSyncNemesisRequest.Handler.class, MessageSyncNemesisRequest.class, packetId, Side.SERVER);
	}

	public MessageSyncNemesisRequest() {

	}

	public MessageSyncNemesisRequest(UUID entityUuid) {
		this.entityUuid = entityUuid;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityUuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, entityUuid.toString());
	}

	public static class Handler implements IMessageHandler<MessageSyncNemesisRequest, IMessage> {
		@Override
		public IMessage onMessage(final MessageSyncNemesisRequest message, MessageContext ctx) {
			final EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> sendResponse(player, message));
			return null;
		}
	}

	private static void sendResponse(EntityPlayerMP player, MessageSyncNemesisRequest message) {

		UUID entityUuid = message.entityUuid;

		if (entityUuid == null) {
			return;
		}

		List<NemesisEntry> nemeses = NemesisRegistryProvider.get(player.world).list();

		for (NemesisEntry nemesis : nemeses) {
			if (entityUuid.equals(nemesis.getEntityUuid())) {
				sendPacketToClient(player, nemesis);
				return;
			}
		}
	}

	private static void sendPacketToClient(EntityPlayerMP player, NemesisEntry nemesis) {
		updateNameTag(player, nemesis);
		NemesisSystem.NETWORK.sendTo(new MessageSyncNemesis(nemesis), player);
	}

	private static void updateNameTag(EntityPlayerMP player, NemesisEntry nemesis) {
		Entity entity = player.world.getEntityByID(nemesis.getSpawned());
		if (entity != null) {
			entity.setCustomNameTag(nemesis.getNameAndTitle());
		}
	}

}
