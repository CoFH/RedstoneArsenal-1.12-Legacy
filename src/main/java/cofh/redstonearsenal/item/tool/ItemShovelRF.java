package cofh.redstonearsenal.item.tool;

import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cpw.mods.fml.common.eventhandler.Event.Result;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class ItemShovelRF extends ItemToolRF {

	int range = 5;

	public ItemShovelRF(Item.ToolMaterial toolMaterial) {

		super(toolMaterial);

		addToolClass("shovel");
		damage = 3;
		energyPerUseCharged = 800;

		effectiveBlocks.addAll(ItemSpade.field_150916_c);
	}

	public ItemShovelRF(Item.ToolMaterial toolMaterial, int harvestLevel) {

		this(toolMaterial);
		this.harvestLevel = harvestLevel;
	}

	protected boolean hoeBlock(World world, int x, int y, int z, int hitSide, EntityPlayer player) {

		if (hitSide == 0) {
			return false;
		}
		Block farmland = Blocks.farmland;
		Block block = world.getBlock(x, y, z);
		boolean air = world.isAirBlock(x, y + 1, z);

		if (!air) {
			Block block2 = world.getBlock(x, y + 1, z);
			if (block2.getBlockHardness(world, x, y + 1, z) == 0.0D) {
				air = harvestBlock(world, x, y + 1, z, player);
			}
		}
		if (air && (block == Blocks.grass || block == Blocks.dirt)) {
			world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, farmland.stepSound.getStepResourcePath(), (farmland.stepSound.getVolume() + 1.0F) / 2.0F,
					farmland.stepSound.getPitch() * 0.8F);
			if (ServerHelper.isServerWorld(world)) {
				world.setBlock(x, y, z, farmland);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean func_150897_b(Block block) {

		return block == Blocks.snow_layer ? true : block == Blocks.snow;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {

		World world = player.worldObj;
		Block block = world.getBlock(x, y, z);
		if (block.getBlockHardness(world, x, y, z) == 0.0D) {
			return false;
		}

		if (effectiveBlocks.contains(block) && isEmpowered(stack)) {
			int facing = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
			switch (facing) {
			case 0:
				for (int i = ++z; i < z + range; i++) {
					if (!effectiveBlocks.contains(world.getBlock(x, y, i))) {
						break;
					}
					if (!harvestBlock(world, x, y, i, player)) {
						break;
					}
				}
				break;
			case 1:
				for (int i = --x; i > x - range; i--) {
					if (!effectiveBlocks.contains(world.getBlock(i, y, z))) {
						break;
					}
					if (!harvestBlock(world, i, y, z, player)) {
						break;
					}
				}
				break;
			case 2:
				for (int i = --z; i > z - range; i--) {
					if (!effectiveBlocks.contains(world.getBlock(x, y, i))) {
						break;
					}
					if (!harvestBlock(world, x, y, i, player)) {
						break;
					}
				}
				break;
			case 3:
				for (int i = ++x; i < x + range; i++) {
					if (!effectiveBlocks.contains(world.getBlock(i, y, z))) {
						break;
					}
					if (!harvestBlock(world, i, y, z, player)) {
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

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		if (!player.canPlayerEdit(x, y, z, hitSide, stack) || !player.capabilities.isCreativeMode && getEnergyStored(stack) < getEnergyPerUse(stack)) {
			return false;
		}
		UseHoeEvent event = new UseHoeEvent(player, stack, world, x, y, z);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}
		if (event.getResult() == Result.ALLOW) {
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return true;
		}
		int hoeRange = 1;
		if (isEmpowered(stack)) {
			hoeRange = range;
		}
		int facing = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		boolean used = false;

		switch (facing) {
		case 0:
			for (int i = z; i < z + hoeRange; i++) {
				if (!hoeBlock(world, x, y, i, hitSide, player)) {
					break;
				}
				used = true;
			}
			break;
		case 1:
			for (int i = x; i > x - hoeRange; i--) {
				if (!hoeBlock(world, i, y, z, hitSide, player)) {
					break;
				}
				used = true;
			}
			break;
		case 2:
			for (int i = z; i > z - hoeRange; i--) {
				if (!hoeBlock(world, x, y, i, hitSide, player)) {
					break;
				}
				used = true;
			}
			break;
		case 3:
			for (int i = x; i < x + hoeRange; i++) {
				if (!hoeBlock(world, i, y, z, hitSide, player)) {
					break;
				}
				used = true;
			}
			break;
		}
		if (used && !player.capabilities.isCreativeMode) {
			useEnergy(stack, false);
		}
		return used;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		super.addInformation(stack, player, list, check);
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		list.add(StringHelper.getFlavorText("info.redstonearsenal.tool.shovel"));
	}

}
