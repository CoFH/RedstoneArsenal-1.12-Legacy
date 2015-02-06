package cofh.redstonearsenal.item.tool;

import gnu.trove.set.hash.THashSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ItemPickaxeRF extends ItemToolRF {

	public THashSet<Block> effectiveBlocksCharged = new THashSet<Block>();

	public ItemPickaxeRF(Item.ToolMaterial toolMaterial) {

		super(toolMaterial);

		addToolClass("pickaxe");
		damage = 4;
		energyPerUseCharged = 800;

		effectiveBlocks.addAll(ItemPickaxe.field_150915_c);
		effectiveBlocksCharged.addAll(ItemPickaxe.field_150915_c);
		effectiveBlocksCharged.addAll(ItemSpade.field_150916_c);
		effectiveMaterials.add(Material.iron);
		effectiveMaterials.add(Material.anvil);
		effectiveMaterials.add(Material.rock);
	}

	public ItemPickaxeRF(Item.ToolMaterial toolMaterial, int harvestLevel) {

		this(toolMaterial);
		this.harvestLevel = harvestLevel;
	}

	@Override
	protected THashSet<Block> getEffectiveBlocks(ItemStack stack) {

		return isEmpowered(stack) ? effectiveBlocksCharged : effectiveBlocks;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {

		World world = player.worldObj;
		Block block = world.getBlock(x, y, z);

		float refStrength = ForgeHooks.blockStrength(block, player, world, x, y, z);
		if (refStrength != 0.0D) {
			if (isEmpowered(stack) && canHarvestBlock(block, stack)) {
				Material bMat = block.getMaterial();
				Block adjBlock = world.getBlock(x, y - 1, z);

				float strength = ForgeHooks.blockStrength(adjBlock, player, world, x, y - 1, z);
				if (strength > 0f && refStrength/strength <= 10f && adjBlock.getMaterial() == bMat) {
					harvestBlock(world, x, y - 1, z, player);
				}
				adjBlock = world.getBlock(x, y + 1, z);
				strength = ForgeHooks.blockStrength(adjBlock, player, world, x, y + 1, z);
				if (strength > 0f && refStrength/strength <= 10f && adjBlock.getMaterial() == bMat) {
					harvestBlock(world, x, y + 1, z, player);
				}
			}
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
		}
		return false;
	}

}
