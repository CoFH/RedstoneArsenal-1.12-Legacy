package cofh.redstonearsenal.block;

import cofh.api.core.IInitializer;
import cofh.lib.util.helpers.DamageHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.redstonearsenal.RedstoneArsenal;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStorage extends Block implements IInitializer {

	public BlockStorage() {

		super(Material.iron);
		setHardness(25.0F);
		setResistance(120.0F);
		setStepSound(soundTypeMetal);
		setCreativeTab(RedstoneArsenal.tab);
		setBlockName("redstonearsenal.storage");

		setHarvestLevel("pickaxe", 2);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < NAMES.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public int damageDropped(int i) {

		return i;
	}

	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {

		return false;
	}

	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {

		return true;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {

		final float f = 0.0625F;
		return AxisAlignedBB.getBoundingBox(x + f, y, z + f, x + 1 - f, y + 1 - f, z + 1 - f);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {

		return 7;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

		if (ServerHelper.isClientWorld(world) || entity instanceof EntityItem) {
			return;
		}
		double fluxDamage = 0;
		int chargeRate = 0;

		switch (world.getBlockMetadata(x, y, z)) {
		case 0:
			if (enableDamage[0]) {
				fluxDamage = damage[0];

				if (enableDamageCharge[0]) {
					chargeRate = charge[0];
				}
			}
			break;
		case 1:
			if (enableDamage[1]) {
				fluxDamage = damage[1];

				if (enableDamageCharge[1]) {
					chargeRate = charge[1];
				}
			}
			break;
		}
		if (fluxDamage > 0) {
			entity.attackEntityFrom(DamageHelper.flux, (float) fluxDamage);

			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) entity;
				if (chargeRate > 0 && EnergyHelper.isPlayerHoldingEnergyContainerItem(player)) {
					EnergyHelper.insertEnergyIntoHeldContainer(player, (int) (chargeRate * fluxDamage), false);
				}
			}
		}
	}

	@Override
	public IIcon getIcon(int side, int metadata) {

		return TEXTURES[metadata];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		for (int i = 0; i < NAMES.length; i++) {
			TEXTURES[i] = ir.registerIcon("redstonearsenal:storage/Block_" + StringHelper.titleCase(NAMES[i]));
		}
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		GameRegistry.registerBlock(this, ItemBlockStorage.class, "Storage");

		String comment;
		String category;

		category = "Storage.FluxedElectrum";

		comment = "Set to false to prevent this block from damaging entities.";
		enableDamage[0] = RedstoneArsenal.config.get(category, "Damage.Enable", true, comment);

		comment = "Set to false to prevent this block from charging held items.";
		enableDamageCharge[0] = RedstoneArsenal.config.get(category, "Charge.Enable", true, comment);

		comment = "Base damage dealt to entities for touching this block.";
		damage[0] = RedstoneArsenal.config.get(category, "Damage.Amount", 0.5D, comment);

		comment = "Base rate of flux charge per tick while entities are in contact with this block; multiplied by damage dealt by the block.";
		charge[0] = RedstoneArsenal.config.get(category, "Charge.Amount", 50, comment);

		category = "Storage.FluxedCrystal";

		comment = "Set to false to prevent this block from damaging entities.";
		enableDamage[1] = RedstoneArsenal.config.get(category, "Damage.Enable", true, comment);

		comment = "Set to false to prevent this block from charging held items.";
		enableDamageCharge[1] = RedstoneArsenal.config.get(category, "Charge.Enable", true, comment);

		comment = "Base damage dealt to entities for touching this block.";
		damage[1] = RedstoneArsenal.config.get(category, "Damage.Amount", 1.0D, comment);

		comment = "Base rate of flux charge per tick while entities are in contact with this block; multiplied by damage dealt by the block.";
		charge[1] = RedstoneArsenal.config.get(category, "Charge.Amount", 50, comment);

		blockElectrumFlux = new ItemStack(this, 1, 0);
		blockCrystalFlux = new ItemStack(this, 1, 1);

		ItemHelper.registerWithHandlers("blockElectrumFlux", blockElectrumFlux);
		ItemHelper.registerWithHandlers("blockCrystalFlux", blockCrystalFlux);

		return true;
	}

	@Override
	public boolean initialize() {

		return true;
	}

	@Override
	public boolean postInit() {

		ItemHelper.addStorageRecipe(blockElectrumFlux, "ingotElectrumFlux");
		ItemHelper.addStorageRecipe(blockCrystalFlux, "gemCrystalFlux");

		return true;
	}

	static final String[] NAMES = { "electrumFlux", "crystalFlux" };
	static final IIcon[] TEXTURES = new IIcon[NAMES.length];

	static boolean enableDamage[] = new boolean[2];
	static boolean enableDamageCharge[] = new boolean[2];
	static double damage[] = new double[2];
	static int charge[] = new int[2];

	public static ItemStack blockElectrumFlux;
	public static ItemStack blockCrystalFlux;

}
