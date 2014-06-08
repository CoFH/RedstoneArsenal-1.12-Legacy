package redstonearsenal.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;

public class EntityArrowExplosive extends EntityArrow {

	public static final float EXPLOSION_POWER = 2.5F;

	public EntityArrowExplosive(World world) {

		super(world);
	}

	public EntityArrowExplosive(World world, double x, double y, double z) {

		super(world, x, y, z);
	}

	public EntityArrowExplosive(World world, EntityLivingBase shooter, EntityLivingBase other, float v, float rand) {

		super(world, shooter, other, v, rand);
	}

	public EntityArrowExplosive(World world, EntityLivingBase shooter, float v) {

		super(world, shooter, v);
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		// if (this.inGround || this.isDead) {
		// this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, EXPLOSION_POWER, true);
		// }
	}
}
