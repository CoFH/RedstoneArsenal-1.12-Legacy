package cofh.redstonearsenal.item.tool;

import cofh.lib.util.RayTracer;
import cofh.lib.util.helpers.MathHelper;
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

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

public class ItemPickaxeRF extends ItemToolRF {

	public THashSet<Block> effectiveBlocksCharged = new THashSet<>();

	public ItemPickaxeRF(ToolMaterial toolMaterial) {

		super(-2.7F, toolMaterial);
		addToolClass("pickaxe");
		damage = 4;
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
	}

	@Override
	protected THashSet<Block> getEffectiveBlocks(ItemStack stack) {

		return isEmpowered(stack) ? effectiveBlocksCharged : super.getEffectiveBlocks(stack);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		World world = player.world;
		IBlockState state = world.getBlockState(pos);

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

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

				if (traceResult.sideHit == DOWN || traceResult.sideHit == UP) {
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
				} else {
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

}
