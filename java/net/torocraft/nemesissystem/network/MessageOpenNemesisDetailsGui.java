package net.torocraft.nemesissystem.network;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.nemesissystem.NemesisSystem;
import net.torocraft.nemesissystem.gui.NemesisSystemGuiHandler;
import net.torocraft.nemesissystem.registry.Nemesis;
import net.torocraft.nemesissystem.registry.NemesisRegistry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class MessageOpenNemesisDetailsGui implements IMessage {

	public static Nemesis NEMESIS;

	private Nemesis nemesis;


	public static void init(int packetId) {
		NemesisSystem.NETWORK.registerMessage(MessageOpenNemesisDetailsGui.Handler.class, MessageOpenNemesisDetailsGui.class, packetId, Side.CLIENT);
	}

	public MessageOpenNemesisDetailsGui() {

	}

	public MessageOpenNemesisDetailsGui(Nemesis nemesis) {
		this.nemesis = nemesis;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			NBTTagCompound c = ByteBufUtils.readTag(buf);
			this.nemesis = new Nemesis();
			nemesis.readFromNBT(c);
		}catch(Exception e){

		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		try {
			NBTTagCompound c = new NBTTagCompound();
			nemesis.readFromNBT(c);
			ByteBufUtils.writeTag(buf, c);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static class Handler implements IMessageHandler<MessageOpenNemesisDetailsGui, IMessage> {
		@Override
		public IMessage onMessage(final MessageOpenNemesisDetailsGui message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> work(message));
			return null;
		}
	}

	public static void work(MessageOpenNemesisDetailsGui message) {
		NEMESIS = message.nemesis;
		EntityPlayer player = Minecraft.getMinecraft().player;
		player.openGui(NemesisSystem.INSTANCE, NemesisSystemGuiHandler.NEMESIS_DETAILS_GUI, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
	}

}
