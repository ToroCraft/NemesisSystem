package net.torocraft.nemesissystem.network;

import io.netty.buffer.ByteBuf;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.nemesissystem.NemesisSystem;

public class MessageReflectDamageAnimation implements IMessage {

	private int id;

	public static void init(int packetId) {
		NemesisSystem.NETWORK.registerMessage(MessageReflectDamageAnimation.Handler.class, MessageReflectDamageAnimation.class, packetId, Side.CLIENT);
	}

	public MessageReflectDamageAnimation() {

	}

	public MessageReflectDamageAnimation(int id) {
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
	}

	public static class Handler implements IMessageHandler<MessageReflectDamageAnimation, IMessage> {
		@Override
		public IMessage onMessage(final MessageReflectDamageAnimation message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> work(message));
			return null;
		}
	}

	public static void work(MessageReflectDamageAnimation message) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity e = mc.world.getEntityByID(message.id);
		if(e != null){
			spawnParticles(mc.world, e.getPosition(), mc.player.getRNG());
		}
	}

	private static void spawnParticles(World world, BlockPos pos, Random rand) {
		double x, y, z;
		for (int i = 0; i < 10; i++) {
			x = (double) ((float) pos.getX() + 0.5F) + (double) (rand.nextFloat() - 0.5F) * 1D;
			y = (double) ((float) pos.getY() + 0.4F) + (double) (rand.nextFloat() - 0.5F) * 1D;
			z = (double) ((float) pos.getZ() + 0.5F) + (double) (rand.nextFloat() - 0.5F) * 1D;
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

}
