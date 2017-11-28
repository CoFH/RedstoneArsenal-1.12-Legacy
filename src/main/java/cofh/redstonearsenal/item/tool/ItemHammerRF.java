package cofh.redstonearsenal.item.tool;

import cofh.core.util.RayTracer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemHammerRF extends ItemToolRF {

	public ItemHammerRF(ToolMaterial toolMaterial) {

		super(-3.2F, toolMaterial);
		addToolClass("pickaxe");
		addToolClass("hammer");
		damage = 9;
		damageCharged = 6;
		energyPerUseCharged = 1600;

		effectiveBlocks.addAll(ItemPickaxe.EFFECTIVE_ON);

		effectiveMaterials.add(Material.IRON);
		effectiveMaterials.add(Material.ANVIL);
		effectiveMaterials.add(Material.ROCK);
		effectiveMaterials.add(Material.ICE);
		effectiveMaterials.add(Material.PACKED_ICE);
		effectiveMaterials.add(Material.GLASS);
		effectiveMaterials.add(Material.REDSTONE_LIGHT);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {

		return false;
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
		boolean used = false;
		world.playEvent(2001, pos, Block.getStateId(state));

		float refStrength = state.getPlayerRelativeBlockHardness(player, world, pos);
		if (refStrength != 0.0F) {
			RayTraceResult traceResult = RayTracer.retrace(player);

			if (traceResult == null) {
				return false;
			}
			BlockPos adjPos;
			IBlockState adjState;
			float strength;

			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			int radius = isEmpowered(stack) ? 2 : 1;

			switch (traceResult.sideHit) {
				case DOWN:
				case UP:
					for (x = pos.getX() - radius; x <= pos.getX() + radius; x++) {
						for (z = pos.getZ() - radius; z <= pos.getZ() + radius; z++) {
							adjPos = new BlockPos(x, y, z);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
							if (strength > 0F && refStrength / strength <= 10F) {
								used |= harvestBlock(world, adjPos, player);
							}
						}
					}
					break;
				case NORTH:
				case SOUTH:
					for (x = pos.getX() - radius; x <= pos.getX() + radius; x++) {
						for (y = pos.getY() - radius; y <= pos.getY() + radius; y++) {
							adjPos = new BlockPos(x, y, z);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
							if (strength > 0F && refStrength / strength <= 10F) {
								used |= harvestBlock(world, adjPos, player);
							}
						}
					}
					break;
				case WEST:
				case EAST:
					for (y = pos.getY() - radius; y <= pos.getY() + radius; y++) {
						for (z = pos.getZ() - radius; z <= pos.getZ() + radius; z++) {
							adjPos = new BlockPos(x, y, z);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
							if (strength > 0F && refStrength / strength <= 10F) {
								used |= harvestBlock(world, adjPos, player);
							}
						}
					}
					break;
			}
			if (used && !player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
		}
		return true;
	}

}
