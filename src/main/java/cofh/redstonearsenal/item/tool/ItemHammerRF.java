package cofh.redstonearsenal.item.tool;

import java.util.Set;

import com.google.common.collect.Sets;

import cofh.lib.util.helpers.BlockHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ItemHammerRF extends ItemToolRF {

	public static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE);

	private String name;

	public ItemHammerRF(ToolMaterial toolMaterial, String nameIn) {

		super(toolMaterial, nameIn);
		name = nameIn;
		addToolClass("pickaxe");
		addToolClass("hammer");
		damage = 5;
		energyPerUseCharged = 800;

		// effectiveBlocks.addAll(EFFECTIVE_ON);
		effectiveMaterials.add(Material.IRON);
		effectiveMaterials.add(Material.ANVIL);
		effectiveMaterials.add(Material.ROCK);
		effectiveMaterials.add(Material.ICE);
		effectiveMaterials.add(Material.PACKED_ICE);
		effectiveMaterials.add(Material.GLASS);
		effectiveMaterials.add(Material.REDSTONE_LIGHT);
	}

	public ItemHammerRF(Item.ToolMaterial toolMaterial, int harvestLevel, String nameIn) {

		this(toolMaterial, nameIn);
		this.harvestLevel = harvestLevel;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos1, EntityPlayer player) {

		World world = player.worldObj;
		IBlockState state = world.getBlockState(pos1);
		int x = pos1.getX();
		int y = pos1.getY();
		int z = pos1.getZ();

		if (!canHarvestBlock(state, stack)) {
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return false;
		}
		boolean used = false;

		float refStrength = ForgeHooks.blockStrength(state, player, world, pos1);
		if (refStrength != 0.0D && canHarvestBlock(state, stack)) {
			RayTraceResult pos = BlockHelper.getCurrentMovingObjectPosition(player, true);
			IBlockState adjBlock;
			float strength;

			int x2 = x;
			int y2 = y;
			int z2 = z;

			switch (pos.sideHit.ordinal()) {
			case 0:
			case 1:
				for (x2 = pos.getBlockPos().getX() - 1; x2 <= pos.getBlockPos().getX() + 1; x2++) {
					for (z2 = pos.getBlockPos().getZ() - 1; z2 <= pos.getBlockPos().getZ() + 1; z2++) {
						BlockPos adjPos = new BlockPos(x2, y2, z2);
						adjBlock = world.getBlockState(adjPos);
						strength = ForgeHooks.blockStrength(adjBlock, player, world, adjPos);
						if (strength > 0f && refStrength / strength <= 10f) {
							used |= harvestBlock(world, adjPos, player);
						}
					}
				}
				break;
			case 2:
			case 3:
				for (x2 = pos.getBlockPos().getX() - 1; x2 <= pos.getBlockPos().getX() + 1; x2++) {
					for (y2 = pos.getBlockPos().getY() - 1; y2 <= pos.getBlockPos().getY() + 1; y2++) {
						BlockPos adjPos = new BlockPos(x2, y2, z2);
						adjBlock = world.getBlockState(adjPos);
						strength = ForgeHooks.blockStrength(adjBlock, player, world, adjPos);
						if (strength > 0f && refStrength / strength <= 10f) {
							used |= harvestBlock(world, adjPos, player);
						}
					}
				}
				break;
			default:
				for (y2 = pos.getBlockPos().getY() - 1; y2 <= pos.getBlockPos().getY() + 1; y2++) {
					for (z2 = pos.getBlockPos().getZ() - 1; z2 <= pos.getBlockPos().getZ() + 1; z2++) {
						BlockPos adjPos = new BlockPos(x2, y2, z2);
						adjBlock = world.getBlockState(adjPos);
						strength = ForgeHooks.blockStrength(adjBlock, player, world, adjPos);
						if (strength > 0f && refStrength / strength <= 10f) {
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
