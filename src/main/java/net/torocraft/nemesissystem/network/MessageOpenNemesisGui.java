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
import net.torocraft.nemesissystem.discovery.PlayerKnowledgeBase;
import net.torocraft.nemesissystem.gui.NemesisSystemGuiHandler;
import net.torocraft.nemesissystem.registry.NemesisEntry;
import net.torocraft.nemesissystem.registry.NemesisRegistry;
import net.torocraft.nemesissystem.registry.NemesisRegistryProvider;

public class MessageOpenNemesisGui implements IMessage {

	private List<NemesisEntry> nemeses;
	private PlayerKnowledgeBase knowledgeBase;

	public static void init(int packetId) {
		NemesisSystem.NETWORK.registerMessage(MessageOpenNemesisGui.Handler.class, MessageOpenNemesisGui.class, packetId, Side.CLIENT);
	}

	public MessageOpenNemesisGui() {

	}

	public MessageOpenNemesisGui(List<NemesisEntry> nemeses, PlayerKnowledgeBase knowledgeBase) {
		this.nemeses = nemeses;
		this.knowledgeBase = knowledgeBase;
	}

	public MessageOpenNemesisGui(EntityPlayerMP player) {
		List<NemesisEntry> nemeses = NemesisRegistryProvider.get(player.getEntityWorld()).list();
		nemeses.removeIf(NemesisEntry::isDead);
		this.nemeses = nemeses;
		this.knowledgeBase = PlayerKnowledgeBase.get(player);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nemeses = NemesisRegistry.readNemesesFromNBT(ByteBufUtils.readTag(buf));
		knowledgeBase = PlayerKnowledgeBase.load(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound c = new NBTTagCompound();
		NemesisRegistry.writeNemesesToNBT(c, nemeses);
		ByteBufUtils.writeTag(buf, c);
		ByteBufUtils.writeTag(buf, PlayerKnowledgeBase.save(knowledgeBase));
	}

	public static class Handler implements IMessageHandler<MessageOpenNemesisGui, IMessage> {
		@Override
		public IMessage onMessage(final MessageOpenNemesisGui message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> work(message));
			return null;
		}

		public static void work(MessageOpenNemesisGui message) {
			NemesisSystem.NEMESES = message.nemeses;
			NemesisSystem.KNOWLEDGE_BASE = message.knowledgeBase;

			//EntityPlayer player = Minecraft.getMinecraft().player;
			//player.openGui(NemesisSystem.INSTANCE, NemesisSystemGuiHandler.NEMESIS_LIST_GUI, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
			NemesisSystem.PROXY.openGui(NemesisSystemGuiHandler.NEMESIS_LIST_GUI);
		}
	}
}
