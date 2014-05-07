package redstonearsenal.block;

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
import redstonearsenal.RedstoneArsenal;
import redstonearsenal.util.Utils;
import cofh.api.core.IInitializer;
import cofh.render.IconRegistry;
import cofh.util.EnergyHelper;
import cofh.util.ItemHelper;
import cofh.util.MathHelper;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStorage extends Block implements IInitializer {

	public BlockStorage() {

		super(Material.iron);
		setHardness(25.0F);
		setResistance(120.0F);
		setStepSound(soundTypeMetal);
		setCreativeTab(RedstoneArsenal.tab);
		setBlockName("redstonearsenal.storage");
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
		return AxisAlignedBB.getAABBPool().getAABB(x + f, y, z + f, x + 1 - f, y + 1 - f, z + 1 - f);
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

		switch (world.getBlockMetadata(x, y, z)) {
		case 0:
			if (enableDamage[0]) {
				fluxDamage = damage[0];
			}
			break;
		case 1:
			if (enableDamage[1]) {
				fluxDamage = damage[1];
			}
			break;
		}
		if (fluxDamage > 0) {
			entity.attackEntityFrom(Utils.flux, (float) fluxDamage);

			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) entity;
				if (EnergyHelper.isPlayerHoldingEnergyContainerItem(player)) {
					EnergyHelper.insertEnergyIntoHeldContainer(player, (int) (chargeRate * fluxDamage), false);
				}
			}
		}
	}

	@Override
	public IIcon getIcon(int side, int metadata) {

		return IconRegistry.getIcon("StorageFlux", metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		for (int i = 0; i < NAMES.length; i++) {
			IconRegistry.addIcon("StorageFlux" + i, "redstonearsenal:storage/Block_" + StringHelper.titleCase(NAMES[i]), ir);
		}
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		return true;
	}

	@Override
	public boolean initialize() {

		String category = "block.feature";
		enableDamage[0] = RedstoneArsenal.config.get(category, "Storage.Electrum.Damage.Enable", true);
		enableDamage[1] = RedstoneArsenal.config.get(category, "Storage.Crystal.Damage.Enable", true);

		enableDamageCharge[0] = RedstoneArsenal.config.get(category, "Storage.Electrum.Damage.Charge", true);
		enableDamageCharge[1] = RedstoneArsenal.config.get(category, "Storage.Crystal.Damage.Charge", true);

		damage[0] = RedstoneArsenal.config.get(category, "Storage.Electrum.Damage.Amount", 0.5);
		damage[1] = RedstoneArsenal.config.get(category, "Storage.Crystal.Damage.Amount", 1);

		String comment = "Amount of Redstone Flux charged per tick while touching a block; multiplied by damage dealt by the block. Max: 1000; Default: 50";
		chargeRate = RedstoneArsenal.config.get(category, "Storage.FluxPerTickPerDamage", chargeRate, comment);
		chargeRate = MathHelper.clampI(chargeRate, 0, 1000);

		blockElectrumFlux = new ItemStack(this, 1, 0);
		blockCrystalFlux = new ItemStack(this, 1, 1);

		ItemHelper.registerWithHandlers("blockElectrumFlux", blockElectrumFlux);
		ItemHelper.registerWithHandlers("blockCrystalFlux", blockCrystalFlux);

		return true;
	}

	@Override
	public boolean postInit() {

		ItemHelper.addStorageRecipe(blockElectrumFlux, "ingotElectrumFlux");
		ItemHelper.addStorageRecipe(blockCrystalFlux, "ingotCrystalFlux");

		return true;
	}

	public static final String[] NAMES = { "electrumFlux", "crystalFlux" };

	public static boolean enableDamage[] = new boolean[2];
	public static boolean enableDamageCharge[] = new boolean[2];
	public static double damage[] = new double[2];
	public static int chargeRate = 50;

	public static ItemStack blockElectrumFlux;
	public static ItemStack blockCrystalFlux;

}
