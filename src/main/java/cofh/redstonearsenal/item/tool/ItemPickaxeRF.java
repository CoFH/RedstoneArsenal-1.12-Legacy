package cofh.redstonearsenal.item.tool;

import gnu.trove.set.hash.THashSet;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

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

		World world = player.worldObj;
		IBlockState state = world.getBlockState(pos);

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		float refStrength = ForgeHooks.blockStrength(state, player, world, pos);
		if (refStrength != 0.0F) {
			if (isEmpowered(stack) && canHarvestBlock(state, stack)) {
				Material bMat = state.getMaterial();
				BlockPos adjPos = new BlockPos(x, y - 1, z);
				IBlockState adjState = world.getBlockState(adjPos);
				float strength = ForgeHooks.blockStrength(adjState, player, world, adjPos);
				if (strength > 0F && refStrength / strength <= 10F && state.getMaterial() == bMat) {
					harvestBlock(world, adjPos, player);
				}
				strength = ForgeHooks.blockStrength(adjState, player, world, adjPos);
				if (strength > 0F && refStrength / strength <= 10F && state.getMaterial() == bMat) {
					harvestBlock(world, adjPos, player);
				}
			}
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
		}
		return false;
	}

}
