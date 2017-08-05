package net.torocraft.nemesissystem.network;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class MessageOpenNemesisGui implements IMessage {

	public static List<NemesisEntry> NEMESES;

	private NBTTagCompound nemeses;

	public static void init(int packetId) {
		NemesisSystem.NETWORK.registerMessage(MessageOpenNemesisGui.Handler.class, MessageOpenNemesisGui.class, packetId, Side.CLIENT);
	}

	public MessageOpenNemesisGui() {

	}

	public MessageOpenNemesisGui(List<NemesisEntry> nemeses) {
		setNemeses(nemeses);
	}

	public MessageOpenNemesisGui(EntityPlayer player) {
		List<NemesisEntry> nemeses = NemesisRegistryProvider.get(player.getEntityWorld()).list();
		nemeses.removeIf(NemesisEntry::isDead);
		setNemeses(nemeses);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nemeses = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nemeses);
	}

	public static class Handler implements IMessageHandler<MessageOpenNemesisGui, IMessage> {
		@Override
		public IMessage onMessage(final MessageOpenNemesisGui message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> work(message));
			return null;
		}
	}

	public static void work(MessageOpenNemesisGui message) {
		NEMESES = message.getNemeses();
		EntityPlayer player = Minecraft.getMinecraft().player;
		player.openGui(NemesisSystem.INSTANCE, 0, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
	}

	public List<NemesisEntry> getNemeses() {
		if(nemeses == null){
			return null;
		}
		return NemesisRegistry.readNemesesFromNBT(nemeses);
	}

	public void setNemeses(List<NemesisEntry> nemeses) {
		this.nemeses = new NBTTagCompound();
		NemesisRegistry.writeNemesesToNBT(this.nemeses, nemeses);
	}
}
