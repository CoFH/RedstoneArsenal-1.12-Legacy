package cofh.redstonearsenal.block;

import cofh.api.core.IInitializer;
import cofh.lib.util.helpers.DamageHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.redstonearsenal.RedstoneArsenal;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

//todo Perhaps this should be changed to BlockFlux?
public class BlockStorage extends Block implements IInitializer {

    public static final PropertyEnum<Type> TYPES = PropertyEnum.create("type", Type.class);

    static boolean enableDamage[] = new boolean[2];
    static boolean enableDamageCharge[] = new boolean[2];
    static double damage[] = new double[2];
    static int charge[] = new int[2];

    public BlockStorage() {
        super(Material.IRON);
        this.setHardness(25.0F);
        this.setResistance(120.0F);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(RedstoneArsenal.tab);
        this.setUnlocalizedName("redstonearsenal.storage");
        this.setHarvestLevel("pickaxe", 2);
    }

    //region Blockstate

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPES).getMetadata();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPES, Type.byMetadata(meta));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPES);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
    }

    //endregion

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public boolean postInit() {
        return true;
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
        return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.99D, 1.0D);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (ServerHelper.isClientWorld(world) || entity instanceof EntityItem) {
            return;
        }
        double fluxDamage = 0;
        int chargeRate = 0;

        if (state.getValue(TYPES) == Type.FLUX_ELECTRUM) {
            if (enableDamage[0]) {
                fluxDamage = damage[0];

                if (enableDamageCharge[0]) {
                    chargeRate = charge[0];
                }
            }
        }
        else {
            if (enableDamage[1]) {
                fluxDamage = damage[1];

                if (enableDamageCharge[1]) {
                    chargeRate = charge[1];
                }
            }
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

        return true;
    }

    @SideOnly(Side.CLIENT)
    public void initModels() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(RedstoneArsenal.modId + ":block_flux", "type=flux_electrum"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 1, new ModelResourceLocation(RedstoneArsenal.modId + ":block_flux", "type=flux_crystal"));
    }

    public enum Type implements IStringSerializable {
        FLUX_ELECTRUM("electrumFlux"),
        FLUX_CRYSTAL("crystalFlux");

        private final String unlocalizedName;

        Type(String unlocalizedName) {
            this.unlocalizedName = unlocalizedName;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public String getUnlocalizedName() {
            return unlocalizedName;
        }

        public int getMetadata() {
            return ordinal();
        }

        public static Type byMetadata(int metadata) {
            if (metadata >= 0 && metadata < values().length) {
                return Type.values()[metadata];
            }
            return FLUX_ELECTRUM;
        }
    }
}
