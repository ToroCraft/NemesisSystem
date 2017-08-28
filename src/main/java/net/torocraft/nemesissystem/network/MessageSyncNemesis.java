package net.torocraft.nemesissystem.network;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class MessageSyncNemesis implements IMessage {

	private NemesisEntry nemesis;

	public static void init(int packetId) {
		NemesisSystem.NETWORK.registerMessage(MessageSyncNemesis.Handler.class, MessageSyncNemesis.class, packetId, Side.CLIENT);
	}

	public MessageSyncNemesis() {

	}

	public MessageSyncNemesis(NemesisEntry nemesis) {
		this.nemesis = nemesis;
	}

	public MessageSyncNemesis(EntityPlayer player) {
		List<NemesisEntry> nemeses = NemesisRegistryProvider.get(player.getEntityWorld()).list();
		nemeses.removeIf(NemesisEntry::isDead);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nemesis = new NemesisEntry();
		nemesis.readFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound c = new NBTTagCompound();
		nemesis.writeToNBT(c);
		ByteBufUtils.writeTag(buf, c);
	}

	public static class Handler implements IMessageHandler<MessageSyncNemesis, IMessage> {
		@Override
		public IMessage onMessage(final MessageSyncNemesis message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> work(message));
			return null;
		}

		public static void work(MessageSyncNemesis message) {
			NemesisEntry nemesis = message.nemesis;

			if (nemesis == null) {
				return;
			}

			Entity entity = Minecraft.getMinecraft().world.getEntityByID(nemesis.getSpawned());

			if (entity == null) {
				return;
			}

			entity.addTag(NemesisSystem.TAG_NEMESIS);
		}
	}

}
