package cofh.redstonearsenal.item.tool;

import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import javax.annotation.Nullable;
import java.util.List;

public class ItemShovelRF extends ItemToolRF {

	int range = 5;

	public ItemShovelRF(ToolMaterial toolMaterial) {

		super(-3.0F, toolMaterial);
		addToolClass("shovel");
		damage = 3;
		energyPerUseCharged = 800;

		effectiveBlocks.addAll(ItemSpade.EFFECTIVE_ON);

		effectiveMaterials.add(Material.GROUND);
		effectiveMaterials.add(Material.GRASS);
		effectiveMaterials.add(Material.SAND);
		effectiveMaterials.add(Material.SNOW);
		effectiveMaterials.add(Material.CRAFTED_SNOW);
		effectiveMaterials.add(Material.CLAY);
	}

	protected boolean hoeBlock(World world, int x, int y, int z, int hitSide, EntityPlayer player) {

		if (hitSide == 0) {
			return false;
		}
		BlockPos pos = new BlockPos(x, y, z);
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (world.isAirBlock(pos.up())) {
			if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
				world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (ServerHelper.isServerWorld(world)) {
					world.setBlockState(pos, Blocks.FARMLAND.getDefaultState());
				}
				return true;
			}
			if (block == Blocks.DIRT) {
				switch (state.getValue(BlockDirt.VARIANT)) {
					case DIRT:
						if (ServerHelper.isServerWorld(world)) {
							world.setBlockState(pos, Blocks.FARMLAND.getDefaultState());
						}
						return true;
					case COARSE_DIRT:
						if (ServerHelper.isServerWorld(world)) {
							world.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
						}
						return true;
				}
			}
		}
		return false;
	}

	protected boolean makePath(World world, int x, int y, int z, int hitSide, EntityPlayer player) {

		if (hitSide == 0) {
			return false;
		}
		BlockPos pos = new BlockPos(x, y, z);
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (world.isAirBlock(pos.up()) && block == Blocks.GRASS) {
			world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

			if (ServerHelper.isServerWorld(world)) {
				world.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState(), 11);
			}
			return true;
		}
		return false;
	}

	protected Block getBlockFromPos(World world, int x, int y, int z) {

		return world.getBlockState(new BlockPos(x, y, z)).getBlock();
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		super.addInformation(stack, worldIn, tooltip, flagIn);

		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		tooltip.add(StringHelper.getFlavorText("info.redstonearsenal.tool.shovel"));
	}

	@Override
	public boolean canHarvestBlock(IBlockState state) {

		return state.getBlock() == Blocks.SNOW_LAYER ? true : state.getBlock() == Blocks.SNOW;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		World world = player.world;
		IBlockState state = world.getBlockState(pos);

		if (state.getBlockHardness(world, pos) == 0.0D) {
			return false;
		}
		Block block = state.getBlock();

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if (getEffectiveBlocks(stack).contains(block) && isEmpowered(stack)) {
			int facing = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
			switch (facing) {
				case 0:
					for (int i = ++z; i < z + range; i++) {
						if (!getEffectiveBlocks(stack).contains(getBlockFromPos(world, x, y, i))) {
							break;
						}
						if (!harvestBlock(world, new BlockPos(x, y, i), player)) {
							break;
						}
					}
					break;
				case 1:
					for (int i = --x; i > x - range; i--) {
						if (!getEffectiveBlocks(stack).contains(getBlockFromPos(world, i, y, z))) {
							break;
						}
						if (!harvestBlock(world, new BlockPos(i, y, z), player)) {
							break;
						}
					}
					break;
				case 2:
					for (int i = --z; i > z - range; i--) {
						if (!getEffectiveBlocks(stack).contains(getBlockFromPos(world, x, y, i))) {
							break;
						}
						if (!harvestBlock(world, new BlockPos(x, y, i), player)) {
							break;
						}
					}
					break;
				case 3:
					for (int i = ++x; i < x + range; i++) {
						if (!getEffectiveBlocks(stack).contains(getBlockFromPos(world, i, y, z))) {
							break;
						}
						if (!harvestBlock(world, new BlockPos(i, y, z), player)) {
							break;
						}
					}
					break;
			}
		}
		if (!player.capabilities.isCreativeMode) {
			useEnergy(stack, false);
		}
		return false;
	}

	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		ItemStack stack = player.getHeldItem(hand);
		if (!player.canPlayerEdit(pos, facing, stack) || !player.capabilities.isCreativeMode && getEnergyStored(stack) < getEnergyPerUse(stack)) {
			return EnumActionResult.FAIL;
		}
		EnumActionResult used = EnumActionResult.FAIL;

		if (player.isSneaking()) {
			UseHoeEvent event = new UseHoeEvent(player, stack, worldIn, pos);

			if (MinecraftForge.EVENT_BUS.post(event)) {
				return EnumActionResult.FAIL;
			}
			if (event.getResult() == Result.ALLOW) {
				if (!player.capabilities.isCreativeMode) {
					useEnergy(stack, false);
				}
				return EnumActionResult.SUCCESS;
			}
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			int effRange = 1;
			if (isEmpowered(stack)) {
				effRange = range;
			}
			int hitVec = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

			switch (hitVec) {
				case 0:
					for (int i = z; i < z + effRange; i++) {
						if (!hoeBlock(worldIn, x, y, i, facing.ordinal(), player)) {
							break;
						}
						used = EnumActionResult.SUCCESS;
					}
					break;
				case 1:
					for (int i = x; i > x - effRange; i--) {
						if (!hoeBlock(worldIn, i, y, z, facing.ordinal(), player)) {
							break;
						}
						used = EnumActionResult.SUCCESS;
					}
					break;
				case 2:
					for (int i = z; i > z - effRange; i--) {
						if (!hoeBlock(worldIn, x, y, i, facing.ordinal(), player)) {
							break;
						}
						used = EnumActionResult.SUCCESS;
					}
					break;
				case 3:
					for (int i = x; i < x + effRange; i++) {
						if (!hoeBlock(worldIn, i, y, z, facing.ordinal(), player)) {
							break;
						}
						used = EnumActionResult.SUCCESS;
					}
					break;
			}
		} else {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			int effRange = 1;
			if (isEmpowered(stack)) {
				effRange = range;
			}
			int hitVec = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

			switch (hitVec) {
				case 0:
					for (int i = z; i < z + effRange; i++) {
						if (!makePath(worldIn, x, y, i, facing.ordinal(), player)) {
							break;
						}
						used = EnumActionResult.SUCCESS;
					}
					break;
				case 1:
					for (int i = x; i > x - effRange; i--) {
						if (!makePath(worldIn, i, y, z, facing.ordinal(), player)) {
							break;
						}
						used = EnumActionResult.SUCCESS;
					}
					break;
				case 2:
					for (int i = z; i > z - effRange; i--) {
						if (!makePath(worldIn, x, y, i, facing.ordinal(), player)) {
							break;
						}
						used = EnumActionResult.SUCCESS;
					}
					break;
				case 3:
					for (int i = x; i < x + effRange; i++) {
						if (!makePath(worldIn, i, y, z, facing.ordinal(), player)) {
							break;
						}
						used = EnumActionResult.SUCCESS;
					}
					break;
			}
		}
		if (used == EnumActionResult.SUCCESS && !player.capabilities.isCreativeMode) {
			useEnergy(stack, false);
		}
		return used;
	}

}
