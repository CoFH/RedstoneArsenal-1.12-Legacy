package cofh.redstonearsenal.item.tool;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSickleRF extends ItemToolRF {

	public int radius = 3;

	public ItemSickleRF(ToolMaterial toolMaterial) {

		super(-2.2F, toolMaterial);
		addToolClass("sickle");
		damage = 5;
		energyPerUseCharged = 1200;

		effectiveBlocks.add(Blocks.WEB);
		effectiveBlocks.add(Blocks.VINE);
		effectiveBlocks.add(Blocks.LEAVES);
		effectiveBlocks.add(Blocks.LEAVES2);

		effectiveMaterials.add(Material.LEAVES);
		effectiveMaterials.add(Material.PLANTS);
		effectiveMaterials.add(Material.VINE);
		effectiveMaterials.add(Material.WEB);
	}

	public ItemSickleRF setRadius(int radius) {

		this.radius = radius;
		return this;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		World world = player.world;
		IBlockState state = world.getBlockState(pos);

		if (!canHarvestBlock(state, stack)) {
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return false;
		}
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		boolean used = false;
		int boost = isEmpowered(stack) ? 2 : 0;
		world.playEvent(2001, pos, Block.getStateId(state));

		for (int i = x - (radius + boost); i <= x + (radius + boost); i++) {
			for (int k = z - (radius + boost); k <= z + (radius + boost); k++) {
				for (int j = y - boost; j <= y + boost; j++) {
					used |= harvestBlock(world, new BlockPos(i, j, k), player);
				}
			}
		}
		if (used && !player.capabilities.isCreativeMode) {
			useEnergy(stack, false);
		}
		return true;
	}

}
