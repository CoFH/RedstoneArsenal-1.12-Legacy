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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.*;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nullable;

public class ItemAxeRF extends ItemToolRF {

	private THashSet<Block> EFFECTIVE_ON_BLOCKS = new THashSet<Block>(Arrays.asList(new Block[] {
			Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE
	}));

	private Set<Material> EFFECTIVE_ON_MATERIALS = Sets.newHashSet(new Material[] {
			Material.WOOD, Material.PLANTS, Material.VINE, Material.CACTUS, Material.GOURD
	});

	private static String name;

	public ItemAxeRF(Item.ToolMaterial toolMaterial, String nameIn) {

		super(toolMaterial, nameIn, -2.8F);
		name = nameIn;
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setCreativeTab(RedstoneArsenal.tab);
		addToolClass("axe");
		damage = 5;
		energyPerUseCharged = 1600;

		effectiveMaterials.addAll(EFFECTIVE_ON_MATERIALS);

		addPropertyOverride(new ResourceLocation(name + "_empowered"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return ItemAxeRF.this.getEnergyStored(stack) > 0 && ItemAxeRF.this.isEmpowered(stack) ? 1F : 0F;
            }
        });
		addPropertyOverride(new ResourceLocation(name + "_active"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return ItemAxeRF.this.getEnergyStored(stack) > 0 && !ItemAxeRF.this.isEmpowered(stack) ? 1F : 0F;
            }
        });
	}

	public ItemAxeRF(Item.ToolMaterial toolMaterial, int harvestLevel, String nameIn) {

		this(toolMaterial, nameIn);
		this.harvestLevel = harvestLevel;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(RedstoneArsenal.modId + ":" + name, "inventory"));
	}

	@Override
	protected THashSet<Block> getEffectiveBlocks(ItemStack stack) {
		return EFFECTIVE_ON_BLOCKS;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		World world = player.worldObj;
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if (block.getBlockHardness(state, world, pos) == 0.0D) {
			return false;
		}

		if (isEmpowered(stack) && (block.isWood(world, pos) || canHarvestBlock(state, stack))) {
			for (int i = x - 1; i <= x + 1; i++) {
				for (int k = z - 1; k <= z + 1; k++) {
					for (int j = y - 2; j <= y + 2; j++) {
						BlockPos pos2 = new BlockPos(i, j, k);
						block = world.getBlockState(pos2).getBlock();
						if (block.isWood(world, pos2) || canHarvestBlock(state, stack)) {
							harvestBlock(world, pos2, player);
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
