package net.torocraft.nemesissystem;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.nemesissystem.Nemesis.Trait;

public class SpawnHandler {
	public static final UUID EMPTY_UUID = new UUID(0, 0);

	public static final String TAG_BODY_GUARD = "nemesis_body_guard";


	public static void init() {
		MinecraftForge.EVENT_BUS.register(new SpawnHandler());
	}

	//@SubscribeEvent
	public void scaleEntity(RenderLivingEvent.Pre event) {
		float scale = 2f;
		if (modelShouldBeScaled(event)) {
			GlStateManager.pushAttrib();
			GlStateManager.pushMatrix();
			GlStateManager.scale(scale, scale, scale);
		}
	}

	//@SubscribeEvent
	public void scaleEntity(RenderLivingEvent.Post event) {
		if (modelShouldBeScaled(event)) {
			GlStateManager.popMatrix();
			GlStateManager.popAttrib();
		}
	}

	private boolean modelShouldBeScaled(RenderLivingEvent event) {
		if (event.getEntity().getEntityData().getUniqueId(EntityDecorator.NBT_ID) != null) {
			return false;
		}
		if (!(event.getRenderer().getMainModel() instanceof ModelBiped)) {
			return false;
		}
		return true;
	}

	// TODO handle nemesis death (drop loot)

	// TODO handle player death

	@SubscribeEvent
	public void handleSpawn(EntityJoinWorldEvent event) {
		if (event.getEntity().world.isRemote || !nemesisClassEntity(event.getEntity())) {
			return;
		}

		if(event.getEntity().getTags().contains(EntityDecorator.TAG)){
			return;
		}

		if(event.getEntity().getTags().contains(TAG_BODY_GUARD)){
			return;
		}

		Nemesis nemesis = getNemesisForSpawn(event);

		if (nemesis == null) {
			return;
		}

		// TODO sound horn

		// TODO chat to near by players

		EntityDecorator.decorate((EntityLiving)event.getEntity(), nemesis);

		spawnBodyGuard((EntityLiving)event.getEntity(), nemesis);

		System.out.println("Spawning: " + event.getEntity().getName() + " at " + event.getEntity().getPosition());
	}



	private void spawnBodyGuard(EntityLiving entity, Nemesis nemesis) {

		int count =  5 + nemesis.getLevel() * 5;

		for(int i = 0; i < count; i++){
			// TODO spawn different body guards based on nemesis mob type
			EntityZombie bodyGuard = new EntityZombie(entity.getEntityWorld());
			bodyGuard.addTag(TAG_BODY_GUARD);
			bodyGuard.getEntityData().setUniqueId(EntityDecorator.NBT_ID, nemesis.getId());
			//TODO armor based on title
			SpawnUtil.spawnEntityLiving(entity.getEntityWorld(), bodyGuard, entity.getPosition(), 10);
			setFollowSpeed(bodyGuard, 1.5);
		}

		// TODO add ai to keep close to nemesis

		// TODO when nemesis is hit, closer body guards attack with a random factor

		// TODO nemesis can heal body guards?


	}

	private void setFollowSpeed(EntityCreature bodyGuard, double followSpeed) {
		EntityAIMoveTowardsRestriction ai = null;
		for(EntityAITaskEntry entry : bodyGuard.tasks.taskEntries){
			if(entry.action instanceof EntityAIMoveTowardsRestriction){
				ai = (EntityAIMoveTowardsRestriction) entry.action;
			}
		}
		if(ai == null){
			System.out.println("guard ai not found");
			return;
		}
		//not sure field_75433_e is the correct name for EntityAIMoveTowardsRestriction.movementSpeed
		ObfuscationReflectionHelper.setPrivateValue(EntityAIMoveTowardsRestriction.class, ai, followSpeed, "field_75433_e", "movementSpeed");
	}

	private boolean nemesisClassEntity(Entity entity) {
		// TODO blacklist

		// TODO whitelist

		return entity instanceof EntityMob;
	}

	private Nemesis getNemesisForSpawn(EntityEvent event) {

		// TODO check location for unspawned nemeses

		if (!(event.getEntity() instanceof EntityLiving)) {
			return null;
		}

		EntityLiving entity = (EntityLiving) event.getEntity();
		World world = entity.world;
		Random rand = entity.getRNG();

		if(!playerInRange(entity, world)){
			return null;
		}

		if(otherNemesisNearby(entity, world)){
			return null;
		}

		List<Nemesis> nemeses = NemesisRegistryProvider.get(event.getEntity().world).list();

		if (nemeses == null || nemeses.size() < 1) {
			return null;
		}

		String entityType = getEntityType(event.getEntity());

		nemeses.removeIf(nemesis -> !nemesis.getMob().equals(entityType));

		if (nemeses.size() < 1) {
			return null;
		}

		return nemeses.get(event.getEntity().world.rand.nextInt(nemeses.size()));
	}

	private boolean otherNemesisNearby(EntityLiving entity, World world) {
		int distance = 100;
		List<EntityLiving> entities = world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(entity.getPosition()).grow(distance, distance, distance));
		for(EntityLiving e : entities){
			if(e.getTags().contains(EntityDecorator.TAG)){
				return true;
			}
		}
		return false;
	}

	private boolean playerInRange(EntityLiving entity, World world) {
		int distance = 100;
		List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(entity.getPosition()).grow(distance, distance, distance));
		for(EntityPlayer player : players){
			if(entity.getEntitySenses().canSee(player)) {
				return true;
			}
		}
		return false;
	}

	public static String getEntityType(Entity entityIn) {
		EntityEntry entry = EntityRegistry.getEntry(entityIn.getClass());
		if (entry == null) {
			return "";
		}
		return entry.getRegistryName().toString();
	}
}
