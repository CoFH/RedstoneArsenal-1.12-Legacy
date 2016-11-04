package cofh.redstonearsenal.block;

import javax.annotation.Nullable;

import cofh.api.core.IInitializer;
import cofh.lib.util.helpers.DamageHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.redstonearsenal.RedstoneArsenal;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStorage extends Block implements IInitializer {

	static boolean enableDamage[] = new boolean[2];
	static boolean enableDamageCharge[] = new boolean[2];
	static double damage[] = new double[2];
	static int charge[] = new int[2];
	String name;

	@Override
	public boolean initialize() {
		return true;
	}

	@Override
	public boolean postInit() {
		return true;
	}

	public BlockStorage(String name) {
		super(Material.IRON);
		this.name = name;
		setHardness(25.0F);
		setResistance(120.0F);
		setSoundType(SoundType.METAL);
		setCreativeTab(RedstoneArsenal.tab);
		setUnlocalizedName(name);
		setRegistryName(name);
		setHarvestLevel("pickaxe", 2);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this) {
			@Override
			public String getItemStackDisplayName(ItemStack item) {

				return StringHelper.localize(getUnlocalizedName(item));
			}

			@Override
			public String getUnlocalizedName(ItemStack item) {

				return "tile.redstonearsenal.storage." + name + ".name";
			}

			@Override
			public EnumRarity getRarity(ItemStack stack) {

				return EnumRarity.UNCOMMON;
			}
		}, getRegistryName());
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getBlock().getMetaFromState(state);
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, net.minecraft.entity.EntityLiving.SpawnPlacementType type) {
		return false;
	}

	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
		return true;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return 7;
	}

	@Override
	@Deprecated
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.95D, 1.0D);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (ServerHelper.isClientWorld(world) || entity instanceof EntityItem) {
			return;
		}
		double fluxDamage = 0;
		int chargeRate = 0;

		switch (state.getBlock().getMetaFromState(state)) {
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

	/* IInitializer */
	@Override
	public boolean preInit() {
		initPre();
		ItemHelper.registerWithHandlers("block_" + name, new ItemStack(this));
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			initModel();
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	private void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(RedstoneArsenal.modId + ":" + name, "inventory"));
	}

	private void initPre() {
		String comment;
		String category;
		switch (name) {
		case "electrum_flux":
			category = "Storage.FluxedElectrum";

			comment = "Set to false to prevent this block from damaging entities.";
			enableDamage[0] = RedstoneArsenal.config.get(category, "Damage.Enable", true, comment);

			comment = "Set to false to prevent this block from charging held items.";
			enableDamageCharge[0] = RedstoneArsenal.config.get(category, "Charge.Enable", true, comment);

			comment = "Base damage dealt to entities for touching this block.";
			damage[0] = RedstoneArsenal.config.get(category, "Damage.Amount", 0.5D, comment);

			comment = "Base rate of flux charge per tick while entities are in contact with this block; multiplied by damage dealt by the block.";
			charge[0] = RedstoneArsenal.config.get(category, "Charge.Amount", 50, comment);
			break;
		case "crystal_flux":
			category = "Storage.FluxedCrystal";

			comment = "Set to false to prevent this block from damaging entities.";
			enableDamage[1] = RedstoneArsenal.config.get(category, "Damage.Enable", true, comment);

			comment = "Set to false to prevent this block from charging held items.";
			enableDamageCharge[1] = RedstoneArsenal.config.get(category, "Charge.Enable", true, comment);

			comment = "Base damage dealt to entities for touching this block.";
			damage[1] = RedstoneArsenal.config.get(category, "Damage.Amount", 1.0D, comment);

			comment = "Base rate of flux charge per tick while entities are in contact with this block; multiplied by damage dealt by the block.";
			charge[1] = RedstoneArsenal.config.get(category, "Charge.Amount", 50, comment);
			break;
		}
	}

}
