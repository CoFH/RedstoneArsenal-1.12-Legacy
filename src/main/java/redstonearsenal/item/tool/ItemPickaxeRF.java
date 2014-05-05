package redstonearsenal.item.tool;

import gnu.trove.set.hash.THashSet;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entity) {

		if (!(entity instanceof EntityPlayer)) {
			return false;
		}
		EntityPlayer player = (EntityPlayer) entity;

		if (block.getBlockHardness(world, x, y, z) != 0.0D) {
			if (isEmpowered(stack) && canHarvestBlock(block, stack)) {
				Material bMat = world.getBlock(x, y, z).getMaterial();
				Block adjBlock = world.getBlock(x, y - 1, z);
				if (adjBlock != null && adjBlock.getMaterial() == bMat && adjBlock.getBlockHardness(world, x, y - 1, z) != -1) {
					harvestBlock(world, x, y - 1, z, player);
				}
				adjBlock = world.getBlock(x, y + 1, z);
				if (adjBlock != null && adjBlock.getMaterial() == bMat && adjBlock.getBlockHardness(world, x, y + 1, z) != -1) {
					harvestBlock(world, x, y + 1, z, player);
				}
			}
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
		}
		return true;
	}

}
