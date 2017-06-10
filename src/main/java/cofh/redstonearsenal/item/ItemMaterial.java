package cofh.redstonearsenal.item;

import cofh.api.util.ThermalExpansionHelper;
import cofh.core.item.ItemMulti;
import cofh.core.util.core.IInitializer;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

import static cofh.lib.util.helpers.ItemHelper.*;

public class ItemMaterial extends ItemMulti implements IInitializer {

	public ItemMaterial() {

		super("redstonearsenal");

		setUnlocalizedName("material");
		setCreativeTab(RedstoneArsenal.tabCommon);
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		dustElectrumFlux = addOreDictItem(0, "dustElectrumFlux", EnumRarity.UNCOMMON);
		ingotElectrumFlux = addOreDictItem(32, "ingotElectrumFlux", EnumRarity.UNCOMMON);
		nuggetElectrumFlux = addOreDictItem(64, "nuggetElectrumFlux", EnumRarity.UNCOMMON);
		gearElectrumFlux = addOreDictItem(96, "gearElectrumFlux", EnumRarity.UNCOMMON);

		gemCrystalFlux = addOreDictItem(128, "gemCrystalFlux", EnumRarity.UNCOMMON);

		plateFlux = addItem(160, "plateFlux", EnumRarity.UNCOMMON);

		rodObsidian = addItem(192, "rodObsidian");
		rodObsidianFlux = addItem(193, "rodObsidianFlux", EnumRarity.UNCOMMON);

		return true;
	}

	@Override
	public boolean initialize() {

		addTwoWayStorageRecipe(ingotElectrumFlux, "ingotElectrumFlux", nuggetElectrumFlux, "nuggetElectrumFlux");

		addReverseStorageRecipe(ingotElectrumFlux, "blockElectrumFlux");
		addReverseStorageRecipe(gemCrystalFlux, "blockCrystalFlux");

		addGearRecipe(gearElectrumFlux, "ingotElectrumFlux");

		addRecipe(ShapedRecipe(plateFlux, "NNN", "GIG", "NNN", 'G', "gemCrystalFlux", 'I', "ingotElectrumFlux", 'N', "nuggetElectrumFlux"));
		addRecipe(ShapedRecipe(rodObsidianFlux, "  O", " B ", "O  ", 'B', rodObsidian, 'O', "gemCrystalFlux"));
		addRecipe(ShapedRecipe(rodObsidian, "  O", " B ", "O  ", 'B', Items.BLAZE_POWDER, 'O', "dustObsidian"));

		if (!Loader.isModLoaded("thermalexpansion")) {
			addRecipe(ShapelessRecipe(dustElectrumFlux, "dustElectrum", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone"));
			addRecipe(ShapelessRecipe(gemCrystalFlux, "gemDiamond", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone"));
			addSmelting(ingotElectrumFlux, dustElectrumFlux, 0.0F);
		} else {
			ThermalExpansionHelper.addSmelterRecipe(4000, dustElectrumFlux, new ItemStack(Blocks.SAND), ingotElectrumFlux);
			ThermalExpansionHelper.addTransposerFill(4000, cofh.thermalfoundation.item.ItemMaterial.dustElectrum, dustElectrumFlux, new FluidStack(TFFluids.fluidRedstone, 500), false);
			ThermalExpansionHelper.addTransposerFill(4000, new ItemStack(Items.DIAMOND), gemCrystalFlux, new FluidStack(TFFluids.fluidRedstone, 500), false);
		}
		return true;
	}

	@Override
	public boolean postInit() {

		return true;
	}

	/* REFERENCES */
	public static ItemStack dustElectrumFlux;
	public static ItemStack ingotElectrumFlux;
	public static ItemStack nuggetElectrumFlux;
	public static ItemStack gearElectrumFlux;

	public static ItemStack gemCrystalFlux;

	public static ItemStack plateFlux;

	public static ItemStack rodObsidian;
	public static ItemStack rodObsidianFlux;

}
