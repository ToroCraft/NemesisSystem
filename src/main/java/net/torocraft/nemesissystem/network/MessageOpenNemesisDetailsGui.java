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
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.gui.NemesisSystemGuiHandler;
import net.torocraft.nemesissystem.registry.NemesisEntry;

public class MessageOpenNemesisDetailsGui implements IMessage {

	public static NemesisEntry NEMESIS;

	private NBTTagCompound nemesisCompound;

	public static void init(int packetId) {
		NemesisSystem.NETWORK.registerMessage(MessageOpenNemesisDetailsGui.Handler.class, MessageOpenNemesisDetailsGui.class, packetId, Side.CLIENT);
	}

	public MessageOpenNemesisDetailsGui() {

	}

	public MessageOpenNemesisDetailsGui(NemesisEntry nemesis) {
		NBTTagCompound c = new NBTTagCompound();
		nemesis.writeToNBT(c);
		this.nemesisCompound = c;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nemesisCompound = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nemesisCompound);
	}

	public static class Handler implements IMessageHandler<MessageOpenNemesisDetailsGui, IMessage> {
		@Override
		public IMessage onMessage(final MessageOpenNemesisDetailsGui message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> work(message));
			return null;
		}
	}

	public static void work(MessageOpenNemesisDetailsGui message) {
		NEMESIS = new NemesisEntry();
		NEMESIS.readFromNBT(message.nemesisCompound);
		EntityPlayer player = Minecraft.getMinecraft().player;
		player.openGui(NemesisSystem.INSTANCE, NemesisSystemGuiHandler.NEMESIS_DETAILS_GUI, player.world, (int) player.posX, (int) player.posY,
				(int) player.posZ);
	}

}
