package cofh.redstonearsenal.item;

import static cofh.lib.util.helpers.ItemHelper.*;

import codechicken.lib.item.ItemStackRegistry;
import cofh.api.modhelpers.ThermalExpansionHelper;
import cofh.lib.util.helpers.*;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.block.BlockStorage;
import cofh.redstonearsenal.core.RAProps;
import cofh.redstonearsenal.item.armor.ItemArmorRF;
import cofh.redstonearsenal.item.tool.*;
import net.minecraft.init.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

public class RAItems {

	public static boolean enableArmor = true;
	public static int armorRFCapacity;
	public static int armorRFTransfer;

	public static boolean[] enableTool = new boolean[9];
	public static int[] toolRFCapacity = new int[9];
	public static int[] toolRFTransfer = new int[9];
	public static int[] toolRFUsed = new int[9];
	public static int[] toolRFCharged = new int[9];

	public static final String[] toolNames = {
			"Wrench", "BattleWrench", "Sword", "Shovel", "Pickaxe", "Axe", "FishingRod", "Sickle", "Bow"
	};

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

	public static BlockStorage blockElectrumFlux;
	public static BlockStorage blockCrystalFlux;

	public static ItemArmorRF itemHelmetFlux;
	public static ItemArmorRF itemPlateFlux;
	public static ItemArmorRF itemLegsFlux;
	public static ItemArmorRF itemBootsFlux;

	public static Item dustElectrumFlux;
	public static Item ingotElectrumFlux;
	public static Item nuggetElectrumFlux;
	public static Item gemCrystalFlux;
	public static Item plateFlux;
	public static Item rodObsidian;
	public static Item rodObsidianFlux;

	public static Item itemWrenchFlux;
	public static Item itemBattleWrenchFlux;
	public static Item itemSwordFlux;
	public static Item itemShovelFlux;
	public static Item itemPickaxeFlux;
	public static Item itemAxeFlux;
	public static Item itemFishingRodFlux;
	public static Item itemSickleFlux;
	public static Item itemBowFlux;

	public static ItemStack materialDustElectrumFlux;
	public static ItemStack materialIngotElectrumFlux;
	public static ItemStack materialNuggetElectrumFlux;
	public static ItemStack materialGemCrystalFlux;
	public static ItemStack materialPlateFlux;
	public static ItemStack materialRodObsidian;
	public static ItemStack materialRodObsidianFlux;

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

	public static final Item.ToolMaterial TOOL_MATERIAL_FLUX = EnumHelper.addToolMaterial("RA:FLUXELECTRUM", 3, 100, 8.0F, 0, 25);
	public static final ItemArmor.ArmorMaterial ARMOR_MATERIAL_FLUX = EnumHelper.addArmorMaterial("RA:FLUXELECTRUM", "flux_armor", 100, new int[] {
			3, 8, 6, 3
	}, 20, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.5F);
	public static final String[] TEXTURE_FLUX = {
			"redstonearsenal:textures/armor/flux_armor_layer_1.png", "redstonearsenal:textures/armor/flux_armor_layer_2.png"
	};

	public static void preInit() {

		RAProps.showArmorCharge = RedstoneArsenal.config.get("Equipment.Flux-Infused.Armor", "ShowChargeBars", RAProps.showArmorCharge);
		RAProps.showToolCharge = RedstoneArsenal.config.get("Equipment.Flux-Infused.Tool", "ShowChargeBars", RAProps.showToolCharge);
		int harvestLevel = MathHelper.clamp(RedstoneArsenal.config.get("Equipment.Flux-Infused.Tool", "HarvestLevel", 4), 1, Integer.MAX_VALUE);

		dustElectrumFlux = new GenericItem("dust_electrum_flux");
		ingotElectrumFlux = new GenericItem("ingot_electrum_flux");
		nuggetElectrumFlux = new GenericItem("nugget_electrum_flux");
		gemCrystalFlux = new GenericItem("gem_crystal_flux");
		plateFlux = new GenericItem("plate_flux");
		rodObsidian = new GenericItem("rod_obsidian");
		rodObsidianFlux = new GenericItem("rod_obsidian_flux");

		materialDustElectrumFlux = new ItemStack(dustElectrumFlux);
		materialIngotElectrumFlux = new ItemStack(ingotElectrumFlux);
		materialNuggetElectrumFlux = new ItemStack(nuggetElectrumFlux);
		materialGemCrystalFlux = new ItemStack(gemCrystalFlux);
		materialPlateFlux = new ItemStack(plateFlux);
		materialRodObsidian = new ItemStack(rodObsidian);
		materialRodObsidianFlux = new ItemStack(rodObsidianFlux);

		itemHelmetFlux = (ItemArmorRF) new ItemArmorRF(ARMOR_MATERIAL_FLUX, EntityEquipmentSlot.HEAD, "flux_helmet").setEnergyParams(armorRFCapacity, armorRFTransfer).setArmorTextures(TEXTURE_FLUX);
		itemHelmetFlux.initModel();
		itemPlateFlux = (ItemArmorRF) new ItemArmorRF(ARMOR_MATERIAL_FLUX, EntityEquipmentSlot.CHEST, "flux_chestplate").setEnergyParams(armorRFCapacity, armorRFTransfer).setArmorTextures(TEXTURE_FLUX);
		itemPlateFlux.initModel();
		itemLegsFlux = (ItemArmorRF) new ItemArmorRF(ARMOR_MATERIAL_FLUX, EntityEquipmentSlot.LEGS, "flux_leggings").setEnergyParams(armorRFCapacity, armorRFTransfer).setArmorTextures(TEXTURE_FLUX);
		itemLegsFlux.initModel();
		itemBootsFlux = (ItemArmorRF) new ItemArmorRF(ARMOR_MATERIAL_FLUX, EntityEquipmentSlot.FEET, "flux_boots").setEnergyParams(armorRFCapacity, armorRFTransfer).setArmorTextures(TEXTURE_FLUX);
		itemBootsFlux.initModel();

		itemWrenchFlux = new ItemWrenchRF(TOOL_MATERIAL_FLUX, "flux_wrench").setEnergyParams(toolRFCapacity[0], toolRFTransfer[0], toolRFUsed[0], toolRFCharged[0]);
		itemBattleWrenchFlux = new ItemWrenchBattleRF(TOOL_MATERIAL_FLUX, "flux_battle_wrench").setEnergyParams(toolRFCapacity[1], toolRFTransfer[1], toolRFUsed[1], toolRFCharged[1]);
		itemSwordFlux = new ItemSwordRF(TOOL_MATERIAL_FLUX, "flux_sword").setEnergyParams(toolRFCapacity[2], toolRFTransfer[2], toolRFUsed[2], toolRFCharged[2]);
		itemShovelFlux = new ItemShovelRF(TOOL_MATERIAL_FLUX, harvestLevel, "flux_shovel").setEnergyParams(toolRFCapacity[3], toolRFTransfer[3], toolRFUsed[3], toolRFCharged[3]);
		itemPickaxeFlux = new ItemPickaxeRF(TOOL_MATERIAL_FLUX, harvestLevel, "flux_pickaxe").setEnergyParams(toolRFCapacity[4], toolRFTransfer[4], toolRFUsed[4], toolRFCharged[4]);
		itemAxeFlux = new ItemAxeRF(TOOL_MATERIAL_FLUX, harvestLevel, "flux_axe").setEnergyParams(toolRFCapacity[5], toolRFTransfer[5], toolRFUsed[5], toolRFCharged[5]);
		itemFishingRodFlux = new ItemFishingRodRF(TOOL_MATERIAL_FLUX, "flux_fishing_rod").setEnergyParams(toolRFCapacity[6], toolRFTransfer[6], toolRFUsed[6], toolRFCharged[6]);
		itemSickleFlux = new ItemSickleRF(TOOL_MATERIAL_FLUX, "flux_sickle").setEnergyParams(toolRFCapacity[7], toolRFTransfer[7], toolRFUsed[7], toolRFCharged[7]);
		itemBowFlux = new ItemBowRF(TOOL_MATERIAL_FLUX, "flux_bow").setEnergyParams(toolRFCapacity[8], toolRFTransfer[8], toolRFUsed[8], toolRFCharged[8]);

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			((GenericItem) dustElectrumFlux).initModel();
			((GenericItem) ingotElectrumFlux).initModel();
			((GenericItem) nuggetElectrumFlux).initModel();
			((GenericItem) gemCrystalFlux).initModel();
			((GenericItem) plateFlux).initModel();
			((GenericItem) rodObsidian).initModel();
			((GenericItem) rodObsidianFlux).initModel();

			((ItemWrenchRF) itemWrenchFlux).initModel();
			((ItemWrenchBattleRF) itemBattleWrenchFlux).initModel();
			((ItemSwordRF) itemSwordFlux).initModel();
			((ItemShovelRF) itemShovelFlux).initModel();
			((ItemPickaxeRF) itemPickaxeFlux).initModel();
			((ItemAxeRF) itemAxeFlux).initModel();
			((ItemFishingRodRF) itemFishingRodFlux).initModel();
			((ItemSickleRF) itemSickleFlux).initModel();
			((ItemBowRF) itemBowFlux).initModel();

		}

		blockElectrumFlux = new BlockStorage("electrum_flux");
		blockElectrumFlux.preInit();
		blockCrystalFlux = new BlockStorage("crystal_flux");
		blockCrystalFlux.preInit();
	}

	public static void initialize() {

		loadItems();
	}

	private static void loadItems() {

		OreDictionary.registerOre("dustElectrumFlux", materialDustElectrumFlux);
		OreDictionary.registerOre("ingotElectrumFlux", materialIngotElectrumFlux);
		OreDictionary.registerOre("nuggetElectrumFlux", materialNuggetElectrumFlux);
		OreDictionary.registerOre("gemCrystalFlux", materialGemCrystalFlux);
		OreDictionary.registerOre("plateFlux", materialPlateFlux);

		OreDictionary.registerOre("rodObsidian", materialRodObsidian);
		OreDictionary.registerOre("rodObsidianFlux", materialRodObsidianFlux);

		/* Armor */
		armorFluxHelmet = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemHelmetFlux), 0);
		armorFluxPlate = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemPlateFlux), 0);
		armorFluxLegs = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemLegsFlux), 0);
		armorFluxBoots = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemBootsFlux), 0);

		ItemStackRegistry.registerCustomItemStack("armorFluxHelmet", armorFluxHelmet);
		ItemStackRegistry.registerCustomItemStack("armorFluxPlate", armorFluxPlate);
		ItemStackRegistry.registerCustomItemStack("armorFluxLegs", armorFluxLegs);
		ItemStackRegistry.registerCustomItemStack("armorFluxBoots", armorFluxBoots);

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

		ItemStackRegistry.registerCustomItemStack("toolFluxWrench", toolFluxWrench);
		ItemStackRegistry.registerCustomItemStack("toolFluxBattleWrench", toolFluxBattleWrench);
		ItemStackRegistry.registerCustomItemStack("toolFluxSword", toolFluxSword);
		ItemStackRegistry.registerCustomItemStack("toolFluxShovel", toolFluxShovel);
		ItemStackRegistry.registerCustomItemStack("toolFluxPickaxe", toolFluxPickaxe);
		ItemStackRegistry.registerCustomItemStack("toolFluxAxe", toolFluxAxe);
		ItemStackRegistry.registerCustomItemStack("toolFluxFishingRod", toolFluxFishingRod);
		ItemStackRegistry.registerCustomItemStack("toolFluxSickle", toolFluxSickle);
		ItemStackRegistry.registerCustomItemStack("toolFluxBow", toolFluxBow);

		if (Loader.isModLoaded("ThermalExpansion")) {
			ThermalExpansionHelper.addTransposerFill(8000, new ItemStack(Items.DIAMOND), materialGemCrystalFlux, new FluidStack(FluidRegistry.getFluid("redstone"), 200), false);
			ThermalExpansionHelper.addTransposerFill(4000, new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ThermalFoundation", "dustElectrum")), 1, 1), materialDustElectrumFlux, new FluidStack(FluidRegistry.getFluid("redstone"), 200), false);
			ThermalExpansionHelper.addSmelterBlastOre("ElectrumFlux");
		}
	}

	public static void postInit() {

		ItemHelper.addStorageRecipe(materialIngotElectrumFlux, "nuggetElectrumFlux");
		ItemHelper.addReverseStorageRecipe(materialNuggetElectrumFlux, "ingotElectrumFlux");

		GameRegistry.addRecipe(ShapedRecipe(rodObsidianFlux, new Object[] {
				"  O", " B ", "O  ", 'B', rodObsidian, 'O', "gemCrystalFlux"
		}));
		GameRegistry.addRecipe(ShapedRecipe(rodObsidian, new Object[] {
				"  O", " B ", "O  ", 'B', Items.BLAZE_POWDER, 'O', "dustObsidian"
		}));
		GameRegistry.addRecipe(ShapedRecipe(plateFlux, new Object[] {
				"NNN", "GIG", "NNN", 'G', "gemCrystalFlux", 'I', "ingotElectrumFlux", 'N', "nuggetElectrumFlux"
		}));

		if (!Loader.isModLoaded("ThermalExpansion")) {
			if (ItemHelper.oreNameExists("dustElectrum")) {
				GameRegistry.addRecipe(ShapelessRecipe(dustElectrumFlux, new Object[] {
						"dustElectrum", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone"
				}));
			}
			else {
				GameRegistry.addRecipe(ShapelessRecipe(dustElectrumFlux, new Object[] {
						"ingotGold", "blockRedstone"
				}));
			}
			GameRegistry.addRecipe(ShapelessRecipe(gemCrystalFlux, new Object[] {
					"gemDiamond", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone"
			}));
			GameRegistry.addSmelting(materialDustElectrumFlux, materialIngotElectrumFlux, 0.0F);
		}
		if (ItemHelper.oreNameExists("dustObsidian")) {
			GameRegistry.addRecipe(ShapedRecipe(rodObsidian, new Object[] {
					"  O", " B ", "O  ", 'B', Items.BLAZE_POWDER, 'O', "dustObsidian"
			}));
		}
		else {
			GameRegistry.addRecipe(ShapedRecipe(rodObsidian, new Object[] {
					"  O", " B ", "O  ", 'B', Items.BLAZE_POWDER, 'O', Blocks.OBSIDIAN
			}));
		}

		/* Armor */
		if (enableArmor) {
			GameRegistry.addRecipe(ShapedRecipe(armorFluxHelmet, new Object[] {
					"III", "I I", 'I', plateFlux
			}));
			GameRegistry.addRecipe(ShapedRecipe(armorFluxPlate, new Object[] {
					"I I", "III", "III", 'I', plateFlux
			}));
			GameRegistry.addRecipe(ShapedRecipe(armorFluxLegs, new Object[] {
					"III", "I I", "I I", 'I', plateFlux
			}));
			GameRegistry.addRecipe(ShapedRecipe(armorFluxBoots, new Object[] {
					"I I", "I I", 'I', plateFlux
			}));
		}

		/* Tools */
		if (enableTool[0]) {
			GameRegistry.addRecipe(ShapedRecipe(toolFluxWrench, new Object[] {
					"I I", " R ", " I ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux
			}));
		}
		if (enableTool[1]) {
			GameRegistry.addRecipe(ShapedRecipe(toolFluxBattleWrench, new Object[] {
					"I I", "III", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux
			}));
		}
		if (enableTool[2]) {
			GameRegistry.addRecipe(ShapedRecipe(toolFluxSword, new Object[] {
					" I ", " I ", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux
			}));
		}
		if (enableTool[3]) {
			GameRegistry.addRecipe(ShapedRecipe(toolFluxShovel, new Object[] {
					" I ", " R ", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux
			}));
		}
		if (enableTool[4]) {
			GameRegistry.addRecipe(ShapedRecipe(toolFluxPickaxe, new Object[] {
					"III", " R ", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux
			}));
		}
		if (enableTool[5]) {
			GameRegistry.addRecipe(ShapedRecipe(toolFluxAxe, new Object[] {
					"II ", "IR ", " R ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux
			}));
		}
		if (enableTool[6]) {
			GameRegistry.addRecipe(ShapedRecipe(toolFluxFishingRod, new Object[] {
					"  I", " IS", "R S", 'I', "ingotElectrumFlux", 'R', rodObsidian, 'S', Items.STRING
			}));
		}
		if (enableTool[7]) {
			GameRegistry.addRecipe(ShapedRecipe(toolFluxSickle, new Object[] {
					" I ", "  I", "RI ", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux
			}));
		}
		if (enableTool[8]) {
			GameRegistry.addRecipe(ShapedRecipe(toolFluxBow, new Object[] {
					" IS", "R S", " IS", 'I', "ingotElectrumFlux", 'R', rodObsidianFlux, 'S', Items.STRING
			}));
		}
		ItemHelper.addStorageRecipe(new ItemStack(blockElectrumFlux), "ingotElectrumFlux");
		ItemHelper.addStorageRecipe(new ItemStack(blockCrystalFlux), "gemCrystalFlux");
		ItemHelper.addReverseStorageRecipe(materialIngotElectrumFlux, new ItemStack(blockElectrumFlux));
		ItemHelper.addReverseStorageRecipe(materialGemCrystalFlux, new ItemStack(blockCrystalFlux));
	}

}
