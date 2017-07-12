package cofh.redstonearsenal.entity.projectile;

import cofh.core.init.CoreProps;
import cofh.core.util.helpers.DamageHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.init.RAProps;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nullable;

public class EntityFluxArrow extends EntityArrow {

	private static DataParameter<Boolean> EMPOWERED = EntityDataManager.createKey(EntityFluxArrow.class, DataSerializers.BOOLEAN);

	public static final int MAX_TICKS = 100;
	public static final float MIN_VELOCITY = 0.5F;
	public static final float EXPLOSION_STRENGTH = 4.0F;
	public static final double DAMAGE = 2.0D;
	public static final double DAMAGE_EMPOWERED = 4.0D;

	protected boolean empowered = false;

	public static void initialize(int id) {

		EntityRegistry.registerModEntity(new ResourceLocation("redstonearsenal:flux_arrow"), EntityFluxArrow.class, "redstonearsenal.flux_arrow", id, RedstoneArsenal.instance, CoreProps.ENTITY_TRACKING_DISTANCE, 1, true);
	}

	public EntityFluxArrow(World world) {

		this(world, false);
	}

	public EntityFluxArrow(World world, boolean empowered) {

		super(world);
		this.empowered = empowered;
		setDamage(empowered ? DAMAGE_EMPOWERED : DAMAGE);
		setNoGravity(true);
	}

	public EntityFluxArrow(World world, double x, double y, double z, boolean empowered) {

		super(world, x, y, z);
		this.empowered = empowered;
		setDamage(empowered ? DAMAGE_EMPOWERED : DAMAGE);
		setNoGravity(true);
	}

	public EntityFluxArrow(World world, EntityLivingBase shooter, boolean empowered) {

		super(world, shooter);
		this.empowered = empowered;
		setDamage(empowered ? DAMAGE_EMPOWERED : DAMAGE);
		setNoGravity(true);
	}

	@Override
	protected void entityInit() {

		super.entityInit();
		dataManager.register(EMPOWERED, false);
	}

	@Override
	protected void onHit(RayTraceResult traceResult) {

		Entity entity = traceResult.entityHit;

		if (entity != null) {
			float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
			int i = MathHelper.ceil((double) f * this.damage);

			if (this.getIsCritical()) {
				i += this.rand.nextInt(i / 2 + 2);
			}
			DamageSource damagesource;

			if (this.shootingEntity == null) {
				damagesource = DamageHelper.flux;
			} else {
				damagesource = DamageHelper.causeEntityFluxDamage("arrow", this.shootingEntity);
			}
			if (this.isBurning() && !(entity instanceof EntityEnderman)) {
				entity.setFire(5);
			}
			if (entity.attackEntityFrom(damagesource, (float) i)) {
				if (entity instanceof EntityLivingBase) {
					EntityLivingBase entitylivingbase = (EntityLivingBase) entity;

					if (this.knockbackStrength > 0) {
						float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
						if (f1 > 0.0F) {
							entitylivingbase.addVelocity(this.motionX * (double) this.knockbackStrength * 0.6000000238418579D / (double) f1, 0.1D, this.motionZ * (double) this.knockbackStrength * 0.6000000238418579D / (double) f1);
						}
					}
					if (this.shootingEntity instanceof EntityLivingBase) {
						EnchantmentHelper.applyThornEnchantments(entitylivingbase, this.shootingEntity);
						EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) this.shootingEntity, entitylivingbase);
					}
					this.arrowHit(entitylivingbase);

					if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
						((EntityPlayerMP) this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
					}
				}
				this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

				if (!(entity instanceof EntityEnderman)) {
					this.setDead();
				}
			} else {
				this.motionX *= -0.10000000149011612D;
				this.motionY *= -0.10000000149011612D;
				this.motionZ *= -0.10000000149011612D;
				this.rotationYaw += 180.0F;
				this.prevRotationYaw += 180.0F;
				this.ticksInAir = 0;

				if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D) {
					if (this.pickupStatus == EntityArrow.PickupStatus.ALLOWED) {
						this.entityDropItem(this.getArrowStack(), 0.1F);
					}
					this.setDead();
				}
			}
		} else {
			BlockPos blockpos = traceResult.getBlockPos();
			this.xTile = blockpos.getX();
			this.yTile = blockpos.getY();
			this.zTile = blockpos.getZ();
			IBlockState iblockstate = this.world.getBlockState(blockpos);
			this.inTile = iblockstate.getBlock();
			this.inData = this.inTile.getMetaFromState(iblockstate);
			this.motionX = (double) ((float) (traceResult.hitVec.x - this.posX));
			this.motionY = (double) ((float) (traceResult.hitVec.y - this.posY));
			this.motionZ = (double) ((float) (traceResult.hitVec.z - this.posZ));
			float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
			this.posX -= this.motionX / (double) f2 * 0.05000000074505806D;
			this.posY -= this.motionY / (double) f2 * 0.05000000074505806D;
			this.posZ -= this.motionZ / (double) f2 * 0.05000000074505806D;
			this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
			this.inGround = true;
			this.arrowShake = 7;
			this.setIsCritical(false);

			if (iblockstate.getMaterial() != Material.AIR) {
				this.inTile.onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
			}
		}
		if (!world.isRemote && empowered) {
			world.createExplosion(this, posX, posY, posZ, EXPLOSION_STRENGTH, RAProps.explosionsDestroyBlocks);
		}
		this.setDead();
	}

	@Override
	public void onUpdate() {

		if (!this.world.isRemote) {
			this.setFlag(6, this.isGlowing());
		}
		this.onEntityUpdate();

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
			this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) f) * (180D / Math.PI));
			this.prevRotationYaw = this.rotationYaw;
			this.prevRotationPitch = this.rotationPitch;
		}
		BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
		IBlockState iblockstate = this.world.getBlockState(blockpos);

		if (iblockstate.getMaterial() != Material.AIR) {
			AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);

			if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
				this.inGround = true;
			}
		}
		if (this.arrowShake > 0) {
			--this.arrowShake;
		}
		if (this.inGround) {
			this.setDead();
		} else {
			this.timeInGround = 0;
			++this.ticksInAir;
			Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
			Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			RayTraceResult traceResult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
			vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
			vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

			if (traceResult != null) {
				vec3d = new Vec3d(traceResult.hitVec.x, traceResult.hitVec.y, traceResult.hitVec.z);
			}
			Entity entity = this.findEntityOnPath(vec3d1, vec3d);

			if (entity != null) {
				traceResult = new RayTraceResult(entity);
			}
			if (traceResult != null && traceResult.entityHit instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) traceResult.entityHit;

				if (this.shootingEntity instanceof EntityPlayer && !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer)) {
					traceResult = null;
				}
			}
			if (traceResult != null) {
				this.onHit(traceResult);
			}
			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			float f4 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

			for (this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) f4) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
				;
			}
			while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
				this.prevRotationPitch += 360.0F;
			}
			while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
				this.prevRotationYaw -= 360.0F;
			}
			while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
				this.prevRotationYaw += 360.0F;
			}
			this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
			this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

			if (this.isInWater()) {
				for (int i = 0; i < 4; ++i) {
					this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
				}
				this.motionX *= 0.6D;
				this.motionY *= 0.6D;
				this.motionZ *= 0.6D;
			}
			if (this.isWet()) {
				this.extinguish();
			}

			this.setPosition(this.posX, this.posY, this.posZ);
			this.doBlockCollisions();
		}
		if (world.isRemote) {
			spawnParticles(empowered ? 8 : 2);
		}
		if (this.ticksInAir >= MAX_TICKS) {
			if (!world.isRemote && empowered) {
				world.createExplosion(this, posX, posY, posZ, EXPLOSION_STRENGTH, RAProps.explosionsDestroyBlocks);
			}
			this.setDead();
		}
	}

	public void spawnParticles(int particleCount) {

		if (particleCount > 0) {
			for (int i = 0; i < particleCount; i++) {
				getEntityWorld().spawnParticle(EnumParticleTypes.REDSTONE, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, 0.0D, 0.0D, 0.0D, new int[0]);
			}
		}
	}

	@Override
	public void setAim(Entity shooter, float pitch, float yaw, float roll, float velocity, float inaccuracy) {

		super.setAim(shooter, pitch, yaw, roll, Math.max(MIN_VELOCITY, velocity), inaccuracy);
	}

	@Override
	protected ItemStack getArrowStack() {

		return ItemStack.EMPTY;
	}

	@Nullable
	protected Entity findEntityOnPath(Vec3d start, Vec3d end) {

		return ServerHelper.isClientWorld(world) ? null : super.findEntityOnPath(start, end);
	}

	/* NBT METHODS */
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {

		super.readEntityFromNBT(nbt);

		empowered = nbt.getBoolean("Empowered");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {

		super.writeEntityToNBT(nbt);

		nbt.setBoolean("Empowered", empowered);
	}

}
