package cofh.redstonearsenal.item.tool;

import cofh.core.item.IAOEBreakItem;
import cofh.core.util.RayTracer;
import cofh.core.util.helpers.MathHelper;
import com.google.common.collect.ImmutableList;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ItemPickaxeFlux extends ItemToolFlux implements IAOEBreakItem {

	protected THashSet<Block> effectiveBlocksCharged = new THashSet<>();
	protected THashSet<Material> effectiveMaterialsCharged = new THashSet<>();

	public ItemPickaxeFlux(ToolMaterial toolMaterial) {

		super(-2.8F, toolMaterial);
		addToolClass("pickaxe");
		damage = 3;
		energyPerUseCharged = 800;

		effectiveBlocks.addAll(ItemPickaxe.EFFECTIVE_ON);
		effectiveBlocksCharged.addAll(ItemPickaxe.EFFECTIVE_ON);
		effectiveBlocksCharged.addAll(ItemSpade.EFFECTIVE_ON);

		effectiveMaterials.add(Material.IRON);
		effectiveMaterials.add(Material.ANVIL);
		effectiveMaterials.add(Material.ROCK);
		effectiveMaterials.add(Material.ICE);
		effectiveMaterials.add(Material.PACKED_ICE);
		effectiveMaterials.add(Material.GLASS);
		effectiveMaterials.add(Material.REDSTONE_LIGHT);

		effectiveMaterialsCharged.add(Material.IRON);
		effectiveMaterialsCharged.add(Material.ANVIL);
		effectiveMaterialsCharged.add(Material.ROCK);
		effectiveMaterialsCharged.add(Material.ICE);
		effectiveMaterialsCharged.add(Material.PACKED_ICE);
		effectiveMaterialsCharged.add(Material.GLASS);
		effectiveMaterialsCharged.add(Material.REDSTONE_LIGHT);
		effectiveMaterialsCharged.add(Material.GROUND);
		effectiveMaterialsCharged.add(Material.GRASS);
		effectiveMaterialsCharged.add(Material.SAND);
		effectiveMaterialsCharged.add(Material.SNOW);
		effectiveMaterialsCharged.add(Material.CRAFTED_SNOW);
		effectiveMaterialsCharged.add(Material.CLAY);
	}

	@Override
	protected THashSet<Block> getEffectiveBlocks(ItemStack stack) {

		return isEmpowered(stack) ? effectiveBlocksCharged : super.getEffectiveBlocks(stack);
	}

	protected THashSet<Material> getEffectiveMaterials(ItemStack stack) {

		return isEmpowered(stack) ? effectiveMaterialsCharged : super.getEffectiveMaterials(stack);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		World world = player.world;
		IBlockState state = world.getBlockState(pos);

		if (state.getBlockHardness(world, pos) == 0.0F) {
			return false;
		}
		if (!canHarvestBlock(state, stack)) {
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return false;
		}
		float refStrength = state.getPlayerRelativeBlockHardness(player, world, pos);

		if (refStrength != 0.0F) {
			if (isEmpowered(stack) && canHarvestBlock(state, stack)) {
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

				switch (traceResult.sideHit) {
					case DOWN:
					case UP:
						int facing = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
						if (facing % 2 == 0) {
							adjPos = new BlockPos(x, y, z - 1);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
							if (strength > 0F && refStrength / strength <= 10F) {
								harvestBlock(world, adjPos, player);
							}
							adjPos = new BlockPos(x, y, z + 1);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
							if (strength > 0F && refStrength / strength <= 10F) {
								harvestBlock(world, adjPos, player);
							}
						} else {
							adjPos = new BlockPos(x - 1, y, z);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
							if (strength > 0F && refStrength / strength <= 10F) {
								harvestBlock(world, adjPos, player);
							}
							adjPos = new BlockPos(x + 1, y, z);
							adjState = world.getBlockState(adjPos);
							strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
							if (strength > 0F && refStrength / strength <= 10F) {
								harvestBlock(world, adjPos, player);
							}
						}
						break;
					default:
						adjPos = new BlockPos(x, y - 1, z);
						adjState = world.getBlockState(adjPos);
						strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
						if (strength > 0F && refStrength / strength <= 10F) {
							harvestBlock(world, adjPos, player);
						}
						adjPos = new BlockPos(x, y + 1, z);
						adjState = world.getBlockState(adjPos);
						strength = adjState.getPlayerRelativeBlockHardness(player, world, adjPos);
						if (strength > 0F && refStrength / strength <= 10F) {
							harvestBlock(world, adjPos, player);
						}
				}
			}
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
		}
		return false;
	}

	/* IAOEBreakItem */
	@Override
	public ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, BlockPos pos, EntityPlayer player) {

		ArrayList<BlockPos> area = new ArrayList<>();
		World world = player.getEntityWorld();

		RayTraceResult traceResult = RayTracer.retrace(player);
		if (traceResult == null || !isEmpowered(stack) || !canHarvestBlock(world.getBlockState(pos), stack)) {
			return ImmutableList.copyOf(area);
		}
		BlockPos harvestPos;

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		switch (traceResult.sideHit) {
			case DOWN:
			case UP:
				int facing = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
				if (facing % 2 == 0) {
					harvestPos = new BlockPos(x, y, z - 1);
					if (canHarvestBlock(world.getBlockState(harvestPos), stack)) {
						area.add(harvestPos);
					}
					harvestPos = new BlockPos(x, y, z + 1);
					if (canHarvestBlock(world.getBlockState(harvestPos), stack)) {
						area.add(harvestPos);
					}
				} else {
					harvestPos = new BlockPos(x - 1, y, z);
					if (canHarvestBlock(world.getBlockState(harvestPos), stack)) {
						area.add(harvestPos);
					}
					harvestPos = new BlockPos(x + 1, y, z);
					if (canHarvestBlock(world.getBlockState(harvestPos), stack)) {
						area.add(harvestPos);
					}
				}
			default:
				harvestPos = new BlockPos(x, y - 1, z);
				if (canHarvestBlock(world.getBlockState(harvestPos), stack)) {
					area.add(harvestPos);
				}
				harvestPos = new BlockPos(x, y + 1, z);
				if (canHarvestBlock(world.getBlockState(harvestPos), stack)) {
					area.add(harvestPos);
				}
		}
		return ImmutableList.copyOf(area);
	}

}
