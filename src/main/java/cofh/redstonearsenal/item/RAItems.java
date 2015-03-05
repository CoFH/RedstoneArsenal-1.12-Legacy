package cofh.redstonearsenal.item;

import cofh.api.core.IInitializer;
import cofh.api.modhelpers.ThermalExpansionHelper;
import cofh.core.item.ItemBase;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.block.BlockStorage;
import cofh.redstonearsenal.item.armor.ItemArmorRF;
import cofh.redstonearsenal.item.tool.ItemAxeRF;
import cofh.redstonearsenal.item.tool.ItemBowRF;
import cofh.redstonearsenal.item.tool.ItemFishingRodRF;
import cofh.redstonearsenal.item.tool.ItemPickaxeRF;
import cofh.redstonearsenal.item.tool.ItemShovelRF;
import cofh.redstonearsenal.item.tool.ItemSickleRF;
import cofh.redstonearsenal.item.tool.ItemSwordRF;
import cofh.redstonearsenal.item.tool.ItemWrenchBattleRF;
import cofh.redstonearsenal.item.tool.ItemWrenchRF;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RAItems {

	public static void preInit() {

		int harvestLevel = MathHelper.clampI(RedstoneArsenal.config.get("Equipment.Flux-Infused.Tool", "HarvestLevel", 4), 1, Integer.MAX_VALUE);

		itemMaterial = (ItemBase) new ItemBase("redstonearsenal").setUnlocalizedName("material").setCreativeTab(RedstoneArsenal.tab);

		itemHelmetFlux = (ItemArmorRF) new ItemArmorRF(ARMOR_MATERIAL_FLUX, 0).setEnergyParams(armorRFCapacity, armorRFTransfer).setArmorTextures(TEXTURE_FLUX)
				.setUnlocalizedName(ARMOR + "fluxHelmet").setTextureName(ARMOR_TEX_FLUX + "Helmet").setCreativeTab(RedstoneArsenal.tab);
		itemPlateFlux = (ItemArmorRF) new ItemArmorRF(ARMOR_MATERIAL_FLUX, 1).setEnergyParams(armorRFCapacity, armorRFTransfer).setArmorTextures(TEXTURE_FLUX)
				.setUnlocalizedName(ARMOR + "fluxPlate").setTextureName(ARMOR_TEX_FLUX + "Chestplate").setCreativeTab(RedstoneArsenal.tab);
		itemLegsFlux = (ItemArmorRF) new ItemArmorRF(ARMOR_MATERIAL_FLUX, 2).setEnergyParams(armorRFCapacity, armorRFTransfer).setArmorTextures(TEXTURE_FLUX)
				.setUnlocalizedName(ARMOR + "fluxLegs").setTextureName(ARMOR_TEX_FLUX + "Legs").setCreativeTab(RedstoneArsenal.tab);
		itemBootsFlux = (ItemArmorRF) new ItemArmorRF(ARMOR_MATERIAL_FLUX, 3).setEnergyParams(armorRFCapacity, armorRFTransfer).setArmorTextures(TEXTURE_FLUX)
				.setUnlocalizedName(ARMOR + "fluxBoots").setTextureName(ARMOR_TEX_FLUX + "Boots").setCreativeTab(RedstoneArsenal.tab);

		itemWrenchFlux = new ItemWrenchRF(TOOL_MATERIAL_FLUX).setEnergyParams(toolRFCapacity[0], toolRFTransfer[0], toolRFUsed[0], toolRFCharged[0])
				.setUnlocalizedName(TOOL + "fluxWrench").setTextureName(TOOL_TEX_FLUX + "Wrench").setCreativeTab(RedstoneArsenal.tab);
		itemBattleWrenchFlux = new ItemWrenchBattleRF(TOOL_MATERIAL_FLUX)
		.setEnergyParams(toolRFCapacity[1], toolRFTransfer[1], toolRFUsed[1], toolRFCharged[1]).setUnlocalizedName(TOOL + "fluxBattleWrench")
		.setTextureName(TOOL_TEX_FLUX + "BattleWrench").setCreativeTab(RedstoneArsenal.tab);
		itemSwordFlux = new ItemSwordRF(TOOL_MATERIAL_FLUX).setEnergyParams(toolRFCapacity[2], toolRFTransfer[2], toolRFUsed[2], toolRFCharged[2])
				.setUnlocalizedName(TOOL + "fluxSword").setTextureName(TOOL_TEX_FLUX + "Sword").setCreativeTab(RedstoneArsenal.tab);
		itemShovelFlux = new ItemShovelRF(TOOL_MATERIAL_FLUX, harvestLevel)
		.setEnergyParams(toolRFCapacity[3], toolRFTransfer[3], toolRFUsed[3], toolRFCharged[3]).setUnlocalizedName(TOOL + "fluxShovel")
		.setTextureName(TOOL_TEX_FLUX + "Shovel").setCreativeTab(RedstoneArsenal.tab);
		itemPickaxeFlux = new ItemPickaxeRF(TOOL_MATERIAL_FLUX, harvestLevel)
		.setEnergyParams(toolRFCapacity[4], toolRFTransfer[4], toolRFUsed[4], toolRFCharged[4]).setUnlocalizedName(TOOL + "fluxPickaxe")
		.setTextureName(TOOL_TEX_FLUX + "Pickaxe").setCreativeTab(RedstoneArsenal.tab);
		itemAxeFlux = new ItemAxeRF(TOOL_MATERIAL_FLUX, harvestLevel).setEnergyParams(toolRFCapacity[5], toolRFTransfer[5], toolRFUsed[5], toolRFCharged[5])
				.setUnlocalizedName(TOOL + "fluxAxe").setTextureName(TOOL_TEX_FLUX + "Axe").setCreativeTab(RedstoneArsenal.tab);
		itemFishingRodFlux = new ItemFishingRodRF(TOOL_MATERIAL_FLUX).setEnergyParams(toolRFCapacity[6], toolRFTransfer[6], toolRFUsed[6], toolRFCharged[6])
				.setUnlocalizedName(TOOL + "fluxFishingRod").setTextureName(TOOL_TEX_FLUX + "FishingRod").setCreativeTab(RedstoneArsenal.tab);
		itemSickleFlux = new ItemSickleRF(TOOL_MATERIAL_FLUX).setEnergyParams(toolRFCapacity[7], toolRFTransfer[7], toolRFUsed[7], toolRFCharged[7])
				.setUnlocalizedName(TOOL + "fluxSickle").setTextureName(TOOL_TEX_FLUX + "Sickle").setCreativeTab(RedstoneArsenal.tab);
		itemBowFlux = new ItemBowRF(TOOL_MATERIAL_FLUX).setEnergyParams(toolRFCapacity[8], toolRFTransfer[8], toolRFUsed[8], toolRFCharged[8])
				.setArrowSpeed(3.0F).setArrowDamage(1.5F).setUnlocalizedName(TOOL + "fluxBow").setTextureName(TOOL_TEX_FLUX + "Bow")
				.setCreativeTab(RedstoneArsenal.tab);

		GameRegistry.registerItem(itemHelmetFlux, "armor.helmetFlux");
		GameRegistry.registerItem(itemPlateFlux, "armor.plateFlux");
		GameRegistry.registerItem(itemLegsFlux, "armor.legsFlux");
		GameRegistry.registerItem(itemBootsFlux, "armor.bootsFlux");

		GameRegistry.registerItem(itemWrenchFlux, "tool.wrenchFlux");
		GameRegistry.registerItem(itemBattleWrenchFlux, "tool.battleWrenchFlux");
		GameRegistry.registerItem(itemSwordFlux, "tool.swordFlux");
		GameRegistry.registerItem(itemShovelFlux, "tool.shovelFlux");
		GameRegistry.registerItem(itemPickaxeFlux, "tool.pickaxeFlux");
		GameRegistry.registerItem(itemAxeFlux, "tool.axeFlux");
		GameRegistry.registerItem(itemFishingRodFlux, "tool.fishingRodFlux");
		GameRegistry.registerItem(itemSickleFlux, "tool.sickleFlux");
		GameRegistry.registerItem(itemBowFlux, "tool.bowFlux");

		blockStorage = new BlockStorage();
		((IInitializer) blockStorage).preInit();
	}

	public static void initialize() {

		loadItems();
	}

	private static void loadItems() {

		dustElectrumFlux = itemMaterial.addOreDictItem(0, "dustElectrumFlux", 1);
		ingotElectrumFlux = itemMaterial.addOreDictItem(32, "ingotElectrumFlux", 1);
		nuggetElectrumFlux = itemMaterial.addOreDictItem(64, "nuggetElectrumFlux", 1);
		gemCrystalFlux = itemMaterial.addOreDictItem(96, "gemCrystalFlux", 1);
		plateFlux = itemMaterial.addItem(128, "plateFlux", 1);

		rodObsidian = itemMaterial.addItem(192, "rodObsidian");
		rodObsidianFlux = itemMaterial.addItem(193, "rodObsidianFlux", 1);

		/* Armor */
		armorFluxHelmet = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemHelmetFlux), 0);
		armorFluxPlate = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemPlateFlux), 0);
		armorFluxLegs = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemLegsFlux), 0);
		armorFluxBoots = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemBootsFlux), 0);

		GameRegistry.registerCustomItemStack("armorFluxHelmet", armorFluxHelmet);
		GameRegistry.registerCustomItemStack("armorFluxPlate", armorFluxPlate);
		GameRegistry.registerCustomItemStack("armorFluxLegs", armorFluxLegs);
		GameRegistry.registerCustomItemStack("armorFluxBoots", armorFluxBoots);

		/* Tools */
		toolFluxWrench = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemWrenchFlux), 0);
		toolFluxBattleWrench = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemBattleWrenchFlux), 0);
		toolFluxSword = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemSwordFlux), 0);
		toolFluxShovel = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemShovelFlux), 0);
		toolFluxPickaxe = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemPickaxeFlux), 0);
		toolFluxAxe = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemAxeFlux), 0);
		toolFluxSickle = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemSickleFlux), 0);
		toolFluxFishingRod = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemFishingRodFlux), 0);
		toolFluxBow = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemBowFlux), 0);

		GameRegistry.registerCustomItemStack("toolFluxWrench", toolFluxWrench);
		GameRegistry.registerCustomItemStack("toolFluxBattleWrench", toolFluxBattleWrench);
		GameRegistry.registerCustomItemStack("toolFluxSword", toolFluxSword);
		GameRegistry.registerCustomItemStack("toolFluxShovel", toolFluxShovel);
		GameRegistry.registerCustomItemStack("toolFluxPickaxe", toolFluxPickaxe);
		GameRegistry.registerCustomItemStack("toolFluxAxe", toolFluxAxe);
		GameRegistry.registerCustomItemStack("toolFluxFishingRod", toolFluxFishingRod);
		GameRegistry.registerCustomItemStack("toolFluxSickle", toolFluxSickle);
		GameRegistry.registerCustomItemStack("toolFluxBow", toolFluxBow);

		if (Loader.isModLoaded("ThermalExpansion")) {
			ThermalExpansionHelper.addTransposerFill(8000, new ItemStack(Items.diamond), gemCrystalFlux,
					new FluidStack(FluidRegistry.getFluid("redstone"), 200), false);
			ThermalExpansionHelper.addTransposerFill(4000, GameRegistry.findItemStack("ThermalFoundation", "dustElectrum", 1), dustElectrumFlux,
					new FluidStack(FluidRegistry.getFluid("redstone"), 200), false);
			ThermalExpansionHelper.addSmelterBlastOre("ElectrumFlux");
		}
	}

	public static void postInit() {

		ItemHelper.addStorageRecipe(ingotElectrumFlux, "nuggetElectrumFlux");
		ItemHelper.addReverseStorageRecipe(nuggetElectrumFlux, "ingotElectrumFlux");

		GameRegistry.addRecipe(new ShapedOreRecipe(rodObsidianFlux, new Object[] { "  O", " B ", "O  ", 'B', rodObsidian, 'O', "gemCrystalFlux" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(rodObsidian, new Object[] { "  O", " B ", "O  ", 'B', Items.blaze_powder, 'O', "dustObsidian" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(plateFlux, new Object[] { "NNN", "GIG", "NNN", 'G', "gemCrystalFlux", 'I', "ingotElectrumFlux", 'N',
		"nuggetElectrumFlux" }));

		if (!Loader.isModLoaded("ThermalExpansion")) {
			if (ItemHelper.oreNameExists("dustElectrum")) {
				GameRegistry.addRecipe(new ShapelessOreRecipe(dustElectrumFlux, new Object[] { "dustElectrum", "dustRedstone", "dustRedstone", "dustRedstone",
						"dustRedstone", "dustRedstone" }));
			} else {
				GameRegistry.addRecipe(new ShapelessOreRecipe(dustElectrumFlux, new Object[] { "ingotGold", "blockRedstone" }));
			}
			GameRegistry.addRecipe(new ShapelessOreRecipe(gemCrystalFlux, new Object[] { "gemDiamond", "dustRedstone", "dustRedstone", "dustRedstone",
					"dustRedstone", "dustRedstone" }));
			GameRegistry.addSmelting(dustElectrumFlux, ingotElectrumFlux, 0.0F);
		}
		if (ItemHelper.oreNameExists("dustObsidian")) {
			GameRegistry.addRecipe(new ShapedOreRecipe(rodObsidian, new Object[] { "  O", " B ", "O  ", 'B', Items.blaze_powder, 'O', "dustObsidian" }));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(rodObsidian, new Object[] { "  O", " B ", "O  ", 'B', Items.blaze_powder, 'O', Blocks.obsidian }));
		}

		/* Armor */
		if (enableArmor) {
			GameRegistry.addRecipe(new ShapedOreRecipe(armorFluxHelmet, new Object[] { "III", "I I", 'I', plateFlux }));
			GameRegistry.addRecipe(new ShapedOreRecipe(armorFluxPlate, new Object[] { "I I", "III", "III", 'I', plateFlux }));
			GameRegistry.addRecipe(new ShapedOreRecipe(armorFluxLegs, new Object[] { "III", "I I", "I I", 'I', plateFlux }));
			GameRegistry.addRecipe(new ShapedOreRecipe(armorFluxBoots, new Object[] { "I I", "I I", 'I', plateFlux }));
		}

		/* Tools */
		if (enableTool[0]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolFluxWrench, new Object[] { "I I", " R ", " I ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enableTool[1]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolFluxBattleWrench,
					new Object[] { "I I", "III", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enableTool[2]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolFluxSword, new Object[] { " I ", " I ", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enableTool[3]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolFluxShovel, new Object[] { " I ", " R ", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enableTool[4]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolFluxPickaxe, new Object[] { "III", " R ", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enableTool[5]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolFluxAxe, new Object[] { "II ", "IR ", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enableTool[6]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolFluxFishingRod, new Object[] { "  I", " IS", "R S", 'I', "ingotElectrumFlux", 'R', rodObsidian, 'S',
					Items.string }));
		}
		if (enableTool[7]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolFluxSickle, new Object[] { " I ", "  I", "RI ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux }));
		}
		if (enableTool[8]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(toolFluxBow, new Object[] { " IS", "R S", " IS", 'I', "ingotElectrumFlux", 'R', rodObsidian, 'S',
					Items.string }));
		}
		ItemHelper.addReverseStorageRecipe(ingotElectrumFlux, "blockElectrumFlux");
		ItemHelper.addReverseStorageRecipe(gemCrystalFlux, "blockCrystalFlux");

		((IInitializer) blockStorage).postInit();
	}

	public static boolean enableArmor = true;
	public static int armorRFCapacity;
	public static int armorRFTransfer;

	public static boolean[] enableTool = new boolean[9];
	public static int[] toolRFCapacity = new int[9];
	public static int[] toolRFTransfer = new int[9];
	public static int[] toolRFUsed = new int[9];
	public static int[] toolRFCharged = new int[9];

	public static final String[] toolNames = { "Wrench", "BattleWrench", "Sword", "Shovel", "Pickaxe", "Axe", "FishingRod", "Sickle", "Bow" };

	public static final int ARMOR_RF_CAPACITY = 400000;
	public static final int ARMOR_RF_TRANSFER = 2000;

	public static final int TOOL_RF_CAPACITY = 160000;
	public static final int TOOL_RF_TRANSFER = 1600;
	public static final int TOOL_RF_USE = 200;
	public static final int TOOL_RF_CHARGED = 800;

	static {
		String category2 = "Equipment.Flux-Infused";
		String category;

		category = category2 + ".Armor.";
		armorRFCapacity = RedstoneArsenal.config.get(category, "RF_Capacity", ARMOR_RF_CAPACITY);
		armorRFTransfer = RedstoneArsenal.config.get(category, "RF_Transfer", ARMOR_RF_TRANSFER);
		enableArmor = RedstoneArsenal.config.get(category, "Recipe", true);

		RedstoneArsenal.config.removeProperty(category2, "Armor");
		RedstoneArsenal.config.removeProperty(category2, "HarvestLevel");

		for (int i = 0; i < toolNames.length; i++) {
			category = category2 + ".Tool." + toolNames[i];
			enableTool[i] = RedstoneArsenal.config.get(category, "Recipe", true);
			toolRFCapacity[i] = RedstoneArsenal.config.get(category, "RF_Capacity", TOOL_RF_CAPACITY);
			toolRFTransfer[i] = RedstoneArsenal.config.get(category, "RF_Transfer", TOOL_RF_TRANSFER);
			toolRFUsed[i] = RedstoneArsenal.config.get(category, "RF_OnUse", TOOL_RF_USE);
			toolRFCharged[i] = RedstoneArsenal.config.get(category, "RF_ChargedUse", TOOL_RF_CHARGED);

			RedstoneArsenal.config.removeProperty(category2, toolNames[i]);
		}
	}

	public static Block blockStorage;

	public static ItemBase itemMaterial;

	public static ItemArmorRF itemHelmetFlux;
	public static ItemArmorRF itemPlateFlux;
	public static ItemArmorRF itemLegsFlux;
	public static ItemArmorRF itemBootsFlux;

	public static Item itemWrenchFlux;
	public static Item itemBattleWrenchFlux;
	public static Item itemSwordFlux;
	public static Item itemShovelFlux;
	public static Item itemPickaxeFlux;
	public static Item itemAxeFlux;
	public static Item itemFishingRodFlux;
	public static Item itemSickleFlux;
	public static Item itemBowFlux;

	public static ItemStack dustElectrumFlux;
	public static ItemStack ingotElectrumFlux;
	public static ItemStack nuggetElectrumFlux;
	public static ItemStack gemCrystalFlux;
	public static ItemStack plateFlux;
	public static ItemStack rodObsidian;
	public static ItemStack rodObsidianFlux;

	public static ItemStack armorFluxHelmet;
	public static ItemStack armorFluxPlate;
	public static ItemStack armorFluxLegs;
	public static ItemStack armorFluxBoots;

	public static ItemStack toolFluxWrench;
	public static ItemStack toolFluxBattleWrench;
	public static ItemStack toolFluxSword;
	public static ItemStack toolFluxShovel;
	public static ItemStack toolFluxPickaxe;
	public static ItemStack toolFluxAxe;
	public static ItemStack toolFluxFishingRod;
	public static ItemStack toolFluxSickle;
	public static ItemStack toolFluxBow;

	public static final Item.ToolMaterial TOOL_MATERIAL_FLUX = EnumHelper.addToolMaterial("RA_FLUX", 3, -1, 8.0F, 0, 25);
	public static final ItemArmor.ArmorMaterial ARMOR_MATERIAL_FLUX = EnumHelper.addArmorMaterial("RA_FLUX", -1, new int[] { 3, 8, 6, 3 }, 20);
	public static final String[] TEXTURE_FLUX = { "redstonearsenal:textures/armor/" + "Flux_1.png", "redstonearsenal:textures/armor/" + "Flux_2.png" };

	public static final String ARMOR = "redstonearsenal.armor.";
	public static final String TOOL = "redstonearsenal.tool.";
	public static final String ARMOR_TEX_FLUX = "redstonearsenal:armor/ArmorFlux";
	public static final String TOOL_TEX_FLUX = "redstonearsenal:tool/Flux";

}
