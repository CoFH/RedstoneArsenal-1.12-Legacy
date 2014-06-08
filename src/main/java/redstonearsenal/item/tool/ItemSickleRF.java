package redstonearsenal.item.tool;

import cofh.util.CoreUtils;
import cofh.util.ServerHelper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSickleRF extends ItemToolRF {

	public int radius = 3;

	public ItemSickleRF(Item.ToolMaterial toolMaterial) {

		super(toolMaterial);

		damage = 5;
		energyPerUseCharged = 1600;

		effectiveMaterials.add(Material.leaves);
		effectiveMaterials.add(Material.plants);
		effectiveMaterials.add(Material.vine);
		effectiveMaterials.add(Material.web);
	}

	public ItemSickleRF setRadius(int radius) {

		this.radius = radius;
		return this;
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack) {

		return block == Blocks.web || block == Blocks.vine;
	}

	@Override
	protected void harvestBlock(World world, int x, int y, int z, EntityPlayer player) {

		Block block = world.getBlock(x, y, z);

		if (block.getBlockHardness(world, x, y, z) < 0 || block.equals(Blocks.waterlily)) {
			return;
		}
		int bMeta = world.getBlockMetadata(x, y, z);

		if (block.canHarvestBlock(player, bMeta)) {
			block.harvestBlock(world, player, x, y, z, bMeta);
		}
		if (ServerHelper.isServerWorld(world) && block.equals(Blocks.vine)) {
			CoreUtils.dropItemStackIntoWorldWithVelocity(new ItemStack(Blocks.vine), world, x, y, z);
		}
		world.setBlockToAir(x, y, z);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entity) {

		if (!(entity instanceof EntityPlayer)) {
			return false;
		}
		EntityPlayer player = (EntityPlayer) entity;

		if (block.getBlockHardness(world, x, y, z) != 0.0D && !effectiveMaterials.contains(block.getMaterial())) {
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return false;
		}
		boolean used = false;
		int boost = isEmpowered(stack) ? 1 : 0;

		for (int i = x - (radius + boost); i <= x + (radius + boost); i++) {
			for (int k = z - (radius + boost); k <= z + (radius + boost); k++) {
				for (int j = y - boost; j <= y + boost; j++) {
					if (isValidHarvestMaterial(stack, world, i, j, k)) {
						harvestBlock(world, i, j, k, player);
						used = true;
					}
				}
			}
		}
		if (used) {
			useEnergy(stack, false);
		}
		return used;
	}

}
