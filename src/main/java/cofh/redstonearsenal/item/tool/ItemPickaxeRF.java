package cofh.redstonearsenal.item.tool;

import java.util.Set;

import com.google.common.collect.Sets;

import cofh.redstonearsenal.RedstoneArsenal;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.*;

import javax.annotation.Nullable;

public class ItemPickaxeRF extends ItemToolRF {

	private static Set<Block> EFFECTIVE_ON_BLOCKS = Sets.newHashSet(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SNOW_LAYER, Blocks.SOUL_SAND, Blocks.GRASS_PATH);

	private static Set<Material> EFFECTIVE_ON_MATERIALS = Sets.newHashSet(Material.IRON, Material.ANVIL, Material.ROCK, Material.ICE, Material.PACKED_ICE, Material.GLASS, Material.REDSTONE_LIGHT);

	public THashSet<Block> effectiveBlocksCharged = new THashSet<Block>();

	private String name;

	public ItemPickaxeRF(Item.ToolMaterial toolMaterial, String nameIn) {
		super(toolMaterial, nameIn, -2.7F);
		name = nameIn;
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setCreativeTab(RedstoneArsenal.tab);
		addToolClass("pickaxe");
		damage = 4;
		energyPerUseCharged = 800;
		effectiveBlocksCharged.addAll(EFFECTIVE_ON_BLOCKS);
		effectiveMaterials.addAll(EFFECTIVE_ON_MATERIALS);
		addPropertyOverride(new ResourceLocation("flux_pickaxe_empowered"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return ItemPickaxeRF.this.getEnergyStored(stack) > 0 && ItemPickaxeRF.this.isEmpowered(stack) ? 1F : 0F;
            }
        });
		addPropertyOverride(new ResourceLocation("flux_pickaxe_active"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return ItemPickaxeRF.this.getEnergyStored(stack) > 0 && !ItemPickaxeRF.this.isEmpowered(stack) ? 1F : 0F;
            }
        });
	}

	public ItemPickaxeRF(Item.ToolMaterial toolMaterial, int harvestLevel, String nameIn) {
		this(toolMaterial, nameIn);
		this.harvestLevel = harvestLevel;
	}

	@Override
	protected THashSet<Block> getEffectiveBlocks(ItemStack stack) {
		return isEmpowered(stack) ? effectiveBlocksCharged : (THashSet<Block>) super.getEffectiveBlocks(stack);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
		World world = player.worldObj;
		IBlockState state = world.getBlockState(pos);
		state.getBlock();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		float refStrength = ForgeHooks.blockStrength(state, player, world, pos);
		if (refStrength != 0.0D) {
			if (isEmpowered(stack) && canHarvestBlock(state, stack)) {
				Material bMat = state.getMaterial();
				BlockPos adjPos = new BlockPos(x, y - 1, z);
				IBlockState adjState = world.getBlockState(adjPos);
				float strength = ForgeHooks.blockStrength(adjState, player, world, adjPos);
				if (strength > 0f && refStrength / strength <= 10f && state.getMaterial() == bMat) {
					harvestBlock(world, adjPos, player);
				}
				strength = ForgeHooks.blockStrength(adjState, player, world, adjPos);
				if (strength > 0f && refStrength / strength <= 10f && state.getMaterial() == bMat) {
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
