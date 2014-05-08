package redstonearsenal.item;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import redstonearsenal.RedstoneArsenal;
import redstonearsenal.block.BlockStorage;
import redstonearsenal.item.tool.ItemAxeRF;
import redstonearsenal.item.tool.ItemPickaxeRF;
import redstonearsenal.item.tool.ItemShovelRF;
import redstonearsenal.item.tool.ItemSickleRF;
import redstonearsenal.item.tool.ItemSwordRF;
import redstonearsenal.item.tool.ItemWrenchBattleRF;
import redstonearsenal.item.tool.ItemWrenchRF;
import cofh.api.core.IInitializer;
import cofh.item.ItemBase;
import cofh.util.EnergyHelper;
import cofh.util.ItemHelper;
import cofh.util.ThermalExpansionHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class RAItems {

	public static final String TOOL = "redstonearsenal.tool.";
	public static final String TOOL_CONFIG_FLUX = "Tool.Flux.";
	public static final String TOOL_TEX_FLUX = "redstonearsenal:tool/Flux";

	public static void preInit() {

		int harvestLevel = RedstoneArsenal.config.get("item.feature", "ToolFluxInfusedHarvestLevel", 4);

		if (harvestLevel < 1) {
			harvestLevel = 1;
		}
		itemMaterial = (ItemBase) new ItemBase("redstonearsenal").setUnlocalizedName("material").setCreativeTab(RedstoneArsenal.tab);

		itemWrench = new ItemWrenchRF(TOOL_MATERIAL_FLUX).setUnlocalizedName(TOOL + "wrench").setTextureName(TOOL_TEX_FLUX + "Wrench")
				.setCreativeTab(RedstoneArsenal.tab);
		itemBattleWrench = new ItemWrenchBattleRF(TOOL_MATERIAL_FLUX).setUnlocalizedName(TOOL + "battleWrench").setTextureName(TOOL_TEX_FLUX + "BattleWrench")
				.setCreativeTab(RedstoneArsenal.tab);
		itemSword = new ItemSwordRF(TOOL_MATERIAL_FLUX).setUnlocalizedName(TOOL + "sword").setTextureName(TOOL_TEX_FLUX + "Sword")
				.setCreativeTab(RedstoneArsenal.tab);
		itemShovel = new ItemShovelRF(TOOL_MATERIAL_FLUX, harvestLevel).setUnlocalizedName(TOOL + "shovel").setTextureName(TOOL_TEX_FLUX + "Shovel")
				.setCreativeTab(RedstoneArsenal.tab);
		itemPickaxe = new ItemPickaxeRF(TOOL_MATERIAL_FLUX, harvestLevel).setUnlocalizedName(TOOL + "pickaxe").setTextureName(TOOL_TEX_FLUX + "Pickaxe")
				.setCreativeTab(RedstoneArsenal.tab);
		itemAxe = new ItemAxeRF(TOOL_MATERIAL_FLUX, harvestLevel).setUnlocalizedName(TOOL + "axe").setTextureName(TOOL_TEX_FLUX + "Axe")
				.setCreativeTab(RedstoneArsenal.tab);
		itemSickle = new ItemSickleRF(TOOL_MATERIAL_FLUX).setUnlocalizedName(TOOL + "sickle").setTextureName(TOOL_TEX_FLUX + "Sickle")
				.setCreativeTab(RedstoneArsenal.tab);
		// itemBow = new ItemBowRF(RedstoneArsenal.config.getItemId(TOOL_CONFIG_FLUX + "Bow"), TOOL_MATERIAL_FLUX).setUnlocalizedName(TOOL + "bow")
		// .setTextureName(TOOL_TEX_FLUX + "Bow").setCreativeTab(RedstoneArsenal.tab);

		GameRegistry.registerItem(itemWrench, "tool.wrench");
		GameRegistry.registerItem(itemBattleWrench, "tool.battleWrench");
		GameRegistry.registerItem(itemSword, "tool.sword");
		GameRegistry.registerItem(itemShovel, "tool.shovel");
		GameRegistry.registerItem(itemPickaxe, "tool.pickaxe");
		GameRegistry.registerItem(itemAxe, "tool.axe");
		GameRegistry.registerItem(itemSickle, "tool.sickle");
		// GameRegistry.registerItem(itemBow, "tool.bow");

		blockStorage = new BlockStorage();
		((IInitializer) blockStorage).preInit();
	}

	public static void initialize() {

		loadItems();
	}

	private static void loadItems() {

		dustElectrumFlux = itemMaterial.addItem(0, "dustElectrumFlux", 1);
		ingotElectrumFlux = itemMaterial.addItem(32, "ingotElectrumFlux", 1);
		nuggetElectrumFlux = itemMaterial.addItem(64, "nuggetElectrumFlux", 1);
		gemCrystalFlux = itemMaterial.addItem(96, "gemCrystalFlux", 1);

		rodObsidian = itemMaterial.addItem(192, "rodObsidian");
		rodObsidianFlux = itemMaterial.addItem(193, "rodObsidianFlux", 1);

		OreDictionary.registerOre("dustElectrumFlux", dustElectrumFlux);
		OreDictionary.registerOre("ingotElectrumFlux", ingotElectrumFlux);
		OreDictionary.registerOre("nuggetElectrumFlux", nuggetElectrumFlux);
		OreDictionary.registerOre("gemCrystalFlux", gemCrystalFlux);

		/* Tools */
		fluxWrench = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemWrench), 0);
		fluxBattleWrench = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemBattleWrench), 0);
		fluxSword = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemSword), 0);
		fluxShovel = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemShovel), 0);
		fluxPickaxe = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemPickaxe), 0);
		fluxAxe = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemAxe), 0);
		fluxSickle = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemSickle), 0);
		// fluxBow = new ItemStack(itemBow);

		GameRegistry.registerCustomItemStack("fluxWrench", fluxWrench);
		GameRegistry.registerCustomItemStack("fluxBattleWrench", fluxBattleWrench);
		GameRegistry.registerCustomItemStack("fluxSword", fluxSword);
		GameRegistry.registerCustomItemStack("fluxShovel", fluxShovel);
		GameRegistry.registerCustomItemStack("fluxPickaxe", fluxPickaxe);
		GameRegistry.registerCustomItemStack("fluxAxe", fluxAxe);
		GameRegistry.registerCustomItemStack("fluxSickle", fluxSickle);
		// GameRegistry.registerCustomItemStack("fluxBow", fluxBow);

		if (Loader.isModLoaded("ThermalExpansion")) {
			ThermalExpansionHelper.addTransposerFill(8000, new ItemStack(Items.diamond), gemCrystalFlux,
					new FluidStack(FluidRegistry.getFluid("redstone"), 200), false);

			ThermalExpansionHelper.addTransposerFill(4000, GameRegistry.findItemStack("ThermalExpansion", "dustElectrum", 1), dustElectrumFlux, new FluidStack(
					FluidRegistry.getFluid("redstone"), 200), false);

			ThermalExpansionHelper.addSmelterBlastOre("ElectrumFlux");
		}
	}

	public static void postInit() {

		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(nuggetElectrumFlux, 9), "ingotElectrumFlux"));
		GameRegistry.addRecipe(new ShapedOreRecipe(ingotElectrumFlux, new Object[] { "III", "III", "III", 'I', "nuggetElectrumFlux" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(rodObsidianFlux, new Object[] { "  O", " B ", "O  ", 'B', rodObsidian, 'O', "gemCrystalFlux" }));

		if (!Loader.isModLoaded("ThermalExpansion")) {
			if (ItemHelper.oreNameExists("dustElectrum")) {
				GameRegistry.addRecipe(new ShapelessOreRecipe(dustElectrumFlux, new Object[] { "dustElectrum", "dustRedstone", "dustRedstone", "dustRedstone",
						"dustRedstone", "dustRedstone" }));
			} else {
				GameRegistry.addRecipe(new ShapelessOreRecipe(dustElectrumFlux, new Object[] { "ingotGold", "blockRedstone" }));
			}
			GameRegistry.addRecipe(new ShapelessOreRecipe(gemCrystalFlux, new Object[] { Items.diamond, "dustRedstone", "dustRedstone", "dustRedstone",
					"dustRedstone", "dustRedstone" }));
			FurnaceRecipes.smelting().func_151394_a(dustElectrumFlux, ingotElectrumFlux, 0.0F);
		}
		if (ItemHelper.oreNameExists("dustObsidian")) {
			GameRegistry.addRecipe(new ShapedOreRecipe(rodObsidian, new Object[] { "  O", " B ", "O  ", 'B', Items.blaze_powder, 'O', "dustObsidian" }));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(rodObsidian, new Object[] { "  O", " B ", "O  ", 'B', Items.blaze_powder, 'O', Blocks.obsidian }));
		}
		if (enable[0]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(fluxWrench, new Object[] { "I I", " R ", " I ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enable[1]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(fluxBattleWrench, new Object[] { "I I", "III", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enable[2]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(fluxSword, new Object[] { " I ", " I ", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enable[3]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(fluxShovel, new Object[] { " I ", " R ", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enable[4]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(fluxPickaxe, new Object[] { "III", " R ", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enable[5]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(fluxAxe, new Object[] { "II ", "IR ", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enable[6]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(fluxSickle, new Object[] { " I ", "  I", "RI ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		addReverseStorageRecipe(ingotElectrumFlux, "blockElectrumFlux");
		addReverseStorageRecipe(gemCrystalFlux, "blockCrystalFlux");

		((IInitializer) blockStorage).postInit();
	}

	static void addReverseStorageRecipe(ItemStack nine, String one) {

		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(nine, 9), new Object[] { one }));
	}

	public static boolean[] enable = new boolean[8];

	static {
		String category = "item.feature";
		enable[0] = RedstoneArsenal.config.get(category, TOOL_CONFIG_FLUX + "Wrench", true);
		enable[1] = RedstoneArsenal.config.get(category, TOOL_CONFIG_FLUX + "BattleWrench", true);
		enable[2] = RedstoneArsenal.config.get(category, TOOL_CONFIG_FLUX + "Sword", true);
		enable[3] = RedstoneArsenal.config.get(category, TOOL_CONFIG_FLUX + "Shovel", true);
		enable[4] = RedstoneArsenal.config.get(category, TOOL_CONFIG_FLUX + "Pickaxe", true);
		enable[5] = RedstoneArsenal.config.get(category, TOOL_CONFIG_FLUX + "Axe", true);
		enable[6] = RedstoneArsenal.config.get(category, TOOL_CONFIG_FLUX + "Sickle", true);
		// enable[7] = RedstoneArsenal.config.get(category, TOOL_CONFIG_FLUX + "Bow", true);
	}

	public static Block blockStorage;

	public static ItemBase itemMaterial;

	public static Item itemWrench;
	public static Item itemBattleWrench;
	public static Item itemSword;
	public static Item itemShovel;
	public static Item itemPickaxe;
	public static Item itemAxe;
	public static Item itemSickle;
	public static Item itemBow;

	public static ItemStack dustElectrumFlux;
	public static ItemStack ingotElectrumFlux;
	public static ItemStack nuggetElectrumFlux;
	public static ItemStack gemCrystalFlux;
	public static ItemStack rodObsidian;
	public static ItemStack rodObsidianFlux;

	public static ItemStack fluxWrench;
	public static ItemStack fluxBattleWrench;
	public static ItemStack fluxSword;
	public static ItemStack fluxShovel;
	public static ItemStack fluxPickaxe;
	public static ItemStack fluxAxe;
	public static ItemStack fluxSickle;
	public static ItemStack fluxBow;

	public static final Item.ToolMaterial TOOL_MATERIAL_FLUX = EnumHelper.addToolMaterial("RA_FLUX", 3, 100, 8.0F, 0, 25);
}
