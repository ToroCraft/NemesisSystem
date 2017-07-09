package net.torocraft.nemesissystem.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.nemesissystem.NemesisSystem;

public class MessageOpenGui implements IMessage {

	private int modGuiId;

	public static void init(int packetId) {
		NemesisSystem.NETWORK.registerMessage(MessageOpenGui.Handler.class, MessageOpenGui.class, packetId, Side.CLIENT);
	}

	public MessageOpenGui() {

	}

	public MessageOpenGui(int modGuiId) {
		this.modGuiId = modGuiId;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		modGuiId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(modGuiId);
	}

	public static class Handler implements IMessageHandler<MessageOpenGui, IMessage> {
		@Override
		public IMessage onMessage(final MessageOpenGui message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> openGui(message.getModGuiId()));
			return null;
		}
	}

	public static void openGui(int modGuiId) {
		System.out.println("open gui request: " + modGuiId);
		EntityPlayer player = Minecraft.getMinecraft().player;
		player.openGui(NemesisSystem.INSTANCE, modGuiId, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
	}

	public int getModGuiId() {
		return modGuiId;
	}

	public void setModGuiId(int modGuiId) {
		this.modGuiId = modGuiId;
	}
}
