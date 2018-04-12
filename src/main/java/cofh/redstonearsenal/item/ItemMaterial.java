package cofh.redstonearsenal.item;

import cofh.api.util.ThermalExpansionHelper;
import cofh.core.item.ItemMulti;
import cofh.core.util.core.IInitializer;
import cofh.core.util.helpers.ItemHelper;
import cofh.redstonearsenal.RedstoneArsenal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

import static cofh.core.util.helpers.RecipeHelper.*;

public class ItemMaterial extends ItemMulti implements IInitializer {

	public ItemMaterial() {

		super("redstonearsenal");

		setUnlocalizedName("material");
		setCreativeTab(RedstoneArsenal.tabItems);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		dustElectrumFlux = addOreDictItem(0, "dustElectrumFlux", EnumRarity.UNCOMMON);
		ingotElectrumFlux = addOreDictItem(32, "ingotElectrumFlux", EnumRarity.UNCOMMON);
		nuggetElectrumFlux = addOreDictItem(64, "nuggetElectrumFlux", EnumRarity.UNCOMMON);
		gearElectrumFlux = addOreDictItem(96, "gearElectrumFlux", EnumRarity.UNCOMMON);

		plateElectrumFlux = addOreDictItem(128, "plateElectrumFlux", EnumRarity.UNCOMMON);

		gemCrystalFlux = addOreDictItem(160, "gemCrystalFlux", EnumRarity.UNCOMMON);

		rodObsidian = addItem(192, "rodObsidian");
		rodObsidianFlux = addItem(193, "rodObsidianFlux", EnumRarity.UNCOMMON);

		plateArmorFlux = addItem(224, "plateArmorFlux", EnumRarity.UNCOMMON);

		RedstoneArsenal.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean register() {

		addTwoWayStorageRecipe(ingotElectrumFlux, "ingotElectrumFlux", nuggetElectrumFlux, "nuggetElectrumFlux");

		addReverseStorageRecipe(ingotElectrumFlux, "blockElectrumFlux");
		addReverseStorageRecipe(gemCrystalFlux, "blockCrystalFlux");

		addGearRecipe(gearElectrumFlux, "ingotElectrumFlux");

		if (OreDictionary.doesOreNameExist("dustObsidian")) {
			addShapedRecipe(rodObsidian, "  O", " B ", "O  ", 'B', Items.BLAZE_POWDER, 'O', "dustObsidian");
		} else {
			addShapedRecipe(rodObsidian, "  O", " B ", "O  ", 'B', Items.BLAZE_POWDER, 'O', Blocks.OBSIDIAN);
		}
		addShapedRecipe(rodObsidianFlux, "  O", " B ", "O  ", 'B', rodObsidian, 'O', "gemCrystalFlux");
		addShapedRecipe(plateArmorFlux, " I ", "IGI", " I ", 'G', "gemCrystalFlux", 'I', "plateElectrumFlux");

		if (!Loader.isModLoaded("thermalexpansion")) {
			if (OreDictionary.doesOreNameExist("dustElectrum")) {
				addShapelessRecipe(dustElectrumFlux, "dustElectrum", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone");
			} else {
				addShapelessRecipe(dustElectrumFlux, "ingotGold", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone");
			}
			addShapelessRecipe(gemCrystalFlux, "gemDiamond", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone");
			addShapedRecipe(ItemHelper.cloneStack(plateElectrumFlux, 4), "II", "II", 'I', "ingotElectrumFlux");
			addSmelting(dustElectrumFlux, ingotElectrumFlux);
		} else {
			ItemStack dustElectrum = ItemHelper.cloneStack(OreDictionary.getOres("dustElectrum", false).get(0), 1);
			FluidStack fluidRedstone = new FluidStack(FluidRegistry.getFluid("redstone"), 500);

			ThermalExpansionHelper.addSmelterRecipe(4000, dustElectrumFlux, new ItemStack(Blocks.SAND), ingotElectrumFlux);

			ThermalExpansionHelper.addCompactorPressRecipe(4000, ingotElectrumFlux, plateElectrumFlux);

			ThermalExpansionHelper.addTransposerFill(400, dustElectrum, dustElectrumFlux, fluidRedstone, false);
			ThermalExpansionHelper.addTransposerFill(400, new ItemStack(Items.DIAMOND), gemCrystalFlux, fluidRedstone, false);
		}
		return true;
	}

	/* REFERENCES */
	public static ItemStack dustElectrumFlux;
	public static ItemStack ingotElectrumFlux;
	public static ItemStack nuggetElectrumFlux;
	public static ItemStack gearElectrumFlux;
	public static ItemStack plateElectrumFlux;

	public static ItemStack gemCrystalFlux;

	public static ItemStack rodObsidian;
	public static ItemStack rodObsidianFlux;

	public static ItemStack plateArmorFlux;

}
