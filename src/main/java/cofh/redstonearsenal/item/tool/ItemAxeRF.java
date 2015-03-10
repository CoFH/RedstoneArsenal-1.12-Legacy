package cofh.redstonearsenal.item.tool;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAxeRF extends ItemToolRF {

	public ItemAxeRF(Item.ToolMaterial toolMaterial) {

		super(toolMaterial);

		addToolClass("axe");
		damage = 5;
		energyPerUseCharged = 1600;

		effectiveBlocks.addAll(ItemAxe.field_150917_c);
		effectiveMaterials.add(Material.wood);
		effectiveMaterials.add(Material.plants);
		effectiveMaterials.add(Material.vine);
		effectiveMaterials.add(Material.cactus);
		effectiveMaterials.add(Material.gourd);
	}

	public ItemAxeRF(Item.ToolMaterial toolMaterial, int harvestLevel) {

		this(toolMaterial);
		this.harvestLevel = harvestLevel;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {

		World world = player.worldObj;
		Block block = world.getBlock(x, y, z);
		if (block.getBlockHardness(world, x, y, z) == 0.0D) {
			return false;
		}

		if (isEmpowered(stack) && (block.isWood(world, x, y, z) || canHarvestBlock(block, stack))) {
			for (int i = x - 1; i <= x + 1; i++) {
				for (int k = z - 1; k <= z + 1; k++) {
					for (int j = y - 2; j <= y + 2; j++) {
						block = world.getBlock(i, j, k);
						if (block.isWood(world, i, j, k) || canHarvestBlock(block, stack)) {
							harvestBlock(world, i, j, k, player);
						}
					}
				}
			}
		}
		if (!player.capabilities.isCreativeMode) {
			useEnergy(stack, false);
		}
		return false;
	}

}
