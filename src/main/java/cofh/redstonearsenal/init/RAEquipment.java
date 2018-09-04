package cofh.redstonearsenal.init;

import cofh.core.render.IModelRegister;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.item.ItemMaterial;
import cofh.redstonearsenal.item.armor.ItemArmorFlux;
import cofh.redstonearsenal.item.tool.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;

public class RAEquipment {

	public static final RAEquipment INSTANCE = new RAEquipment();

	private RAEquipment() {

	}

	public static void preInit() {

		for (ArmorSet e : ArmorSet.values()) {
			e.preInit();
			RedstoneArsenal.proxy.addIModelRegister(e);
		}
		for (ToolSet e : ToolSet.values()) {
			e.preInit();
			RedstoneArsenal.proxy.addIModelRegister(e);
		}
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {

		for (ArmorSet e : ArmorSet.values()) {
			e.initialize();
		}
		for (ToolSet e : ToolSet.values()) {
			e.initialize();
		}
	}

	/* MATERIALS */
	public static ArmorMaterial armorMaterialFlux;
	public static ToolMaterial toolMaterialFlux;

	static {
		String category;
		String comment;

		int helm = 3;
		int legs = 6;
		int chest = 8;
		int boots = 3;

		int harvestLevel = 4;
		float efficiency = 8.0F;

		category = "Equipment.Armor";
		comment = "Adjust this value to set the default protection provided by the Flux-Infused Boots.";
		boots = RedstoneArsenal.CONFIG.getConfiguration().getInt("BootsProtection", category, boots, 1, 10, comment);

		comment = "Adjust this value to set the default protection provided by the Flux-Infused Leggings.";
		legs = RedstoneArsenal.CONFIG.getConfiguration().getInt("LegsProtection", category, legs, 1, 10, comment);

		comment = "Adjust this value to set the default protection provided by the Flux-Infused Chestplate.";
		chest = RedstoneArsenal.CONFIG.getConfiguration().getInt("ChestProtection", category, chest, 1, 10, comment);

		comment = "Adjust this value to set the default protection provided by the Flux-Infused Helmet.";
		helm = RedstoneArsenal.CONFIG.getConfiguration().getInt("HelmProtection", category, helm, 1, 10, comment);

		category = "Equipment.Tools";
		comment = "Adjust this value to set the default harvest level of Flux-Infused Tools.";
		harvestLevel = RedstoneArsenal.CONFIG.getConfiguration().getInt("HarvestLevel", category, harvestLevel, 0, 10, comment);

		comment = "Adjust this value to set the default efficiency (mining speed) of Flux-Infused Tools";
		efficiency = RedstoneArsenal.CONFIG.getConfiguration().getFloat("Efficiency", category, efficiency, 2.0F, 32.0F, comment);

		armorMaterialFlux = EnumHelper.addArmorMaterial("RA:FLUXELECTRUM", "flux_armor", 100, new int[] { boots, legs, chest, helm }, 20, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0F);
		toolMaterialFlux = EnumHelper.addToolMaterial("RA:FLUXELECTRUM", harvestLevel, 100, efficiency, 0, 25);
	}

	/* ARMOR */
	public enum ArmorSet implements IModelRegister {

		FLUX("flux", armorMaterialFlux);

		private final String name;
		private final ArmorMaterial ARMOR_MATERIAL;

		public ItemArmorFlux itemHelmet;
		public ItemArmorFlux itemPlate;
		public ItemArmorFlux itemLegs;
		public ItemArmorFlux itemBoots;

		public ItemStack armorHelmet;
		public ItemStack armorPlate;
		public ItemStack armorLegs;
		public ItemStack armorBoots;

		public boolean[] enable = new boolean[4];

		ArmorSet(String name, ArmorMaterial material) {

			this.name = name.toLowerCase(Locale.US);
			ARMOR_MATERIAL = material;
		}

		protected void create() {

			itemHelmet = new ItemArmorFlux(ARMOR_MATERIAL, EntityEquipmentSlot.HEAD);
			itemPlate = new ItemArmorFlux(ARMOR_MATERIAL, EntityEquipmentSlot.CHEST);
			itemLegs = new ItemArmorFlux(ARMOR_MATERIAL, EntityEquipmentSlot.LEGS);
			itemBoots = new ItemArmorFlux(ARMOR_MATERIAL, EntityEquipmentSlot.FEET);
		}

		protected void preInit() {

			final String ARMOR = "redstonearsenal.armor." + name;
			final String PATH_ARMOR = "redstonearsenal:textures/armor/";
			final String[] TEXTURE = { PATH_ARMOR + name + "_1.png", PATH_ARMOR + name + "_2.png" };

			String category = "Equipment.Armor." + StringHelper.titleCase(name);

			enable[0] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Helmet", true).getBoolean(true);
			enable[1] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Chestplate", true).getBoolean(true);
			enable[2] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Leggings", true).getBoolean(true);
			enable[3] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Boots", true).getBoolean(true);

			create();

			/* HELMET */
			itemHelmet.setArmorTextures(TEXTURE).setUnlocalizedName(ARMOR + "Helmet").setCreativeTab(RedstoneArsenal.tabBasicArmor);
			itemHelmet.setShowInCreative(enable[0]);
			itemHelmet.setRegistryName("armor.helmet_" + name);
			ForgeRegistries.ITEMS.register(itemHelmet);

			/* PLATE */
			itemPlate.setArmorTextures(TEXTURE).setUnlocalizedName(ARMOR + "Plate").setCreativeTab(RedstoneArsenal.tabBasicArmor);
			itemPlate.setShowInCreative(enable[1]);
			itemPlate.setRegistryName("armor.plate_" + name);
			ForgeRegistries.ITEMS.register(itemPlate);

			/* LEGS */
			itemLegs.setArmorTextures(TEXTURE).setUnlocalizedName(ARMOR + "Legs").setCreativeTab(RedstoneArsenal.tabBasicArmor);
			itemLegs.setShowInCreative(enable[2]);
			itemLegs.setRegistryName("armor.legs_" + name);
			ForgeRegistries.ITEMS.register(itemLegs);

			/* BOOTS */
			itemBoots.setArmorTextures(TEXTURE).setUnlocalizedName(ARMOR + "Boots").setCreativeTab(RedstoneArsenal.tabBasicArmor);
			itemBoots.setShowInCreative(enable[3]);
			itemBoots.setRegistryName("armor.boots_" + name);
			ForgeRegistries.ITEMS.register(itemBoots);

			/* REFERENCES */
			armorHelmet = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemHelmet), 0);
			armorPlate = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemPlate), 0);
			armorLegs = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemLegs), 0);
			armorBoots = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemBoots), 0);
		}

		protected void initialize() {

			if (enable[0]) {
				addShapedRecipe(armorHelmet, "III", "I I", 'I', ItemMaterial.plateArmorFlux);
			}
			if (enable[1]) {
				addShapedRecipe(armorPlate, "I I", "III", "III", 'I', ItemMaterial.plateArmorFlux);
			}
			if (enable[2]) {
				addShapedRecipe(armorLegs, "III", "I I", "I I", 'I', ItemMaterial.plateArmorFlux);
			}
			if (enable[3]) {
				addShapedRecipe(armorBoots, "I I", "I I", 'I', ItemMaterial.plateArmorFlux);
			}
		}

		/* HELPERS */
		@SideOnly (Side.CLIENT)
		public void registerModel(Item item, String stackName) {

			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(RedstoneArsenal.MOD_ID + ":armor", "type=" + stackName));
		}

		/* IModelRegister */
		@Override
		@SideOnly (Side.CLIENT)
		public void registerModels() {

			registerModel(itemHelmet, "helmet_" + name);
			registerModel(itemPlate, "chestplate_" + name);
			registerModel(itemLegs, "leggings_" + name);
			registerModel(itemBoots, "boots_" + name);
		}
	}

	/* TOOLS */
	public enum ToolSet implements IModelRegister {

		FLUX("flux", toolMaterialFlux);

		private final String name;
		private final ToolMaterial TOOL_MATERIAL;

		/* BOW */
		private float arrowDamage = 1.5F;
		private float arrowSpeed = 0.5F;
		private float zoomMultiplier = 0.35F;

		/* FISHING ROD */
		private int luckModifier = 1;
		private int speedModifier = 1;

		/* TOOLS */
		public ItemWrenchFlux itemWrench;
		public ItemBattleWrenchFlux itemBattleWrench;
		public ItemSwordFlux itemSword;
		public ItemShovelFlux itemShovel;
		public ItemPickaxeFlux itemPickaxe;
		public ItemAxeFlux itemAxe;
		public ItemBowFlux itemBow;
		public ItemFishingRodFlux itemFishingRod;
		public ItemSickleFlux itemSickle;
		public ItemHammerFlux itemHammer;
		public ItemExcavatorFlux itemExcavator;
		public ItemShieldFlux itemShield;

		public ItemStack toolWrench;
		public ItemStack toolBattleWrench;
		public ItemStack toolSword;
		public ItemStack toolShovel;
		public ItemStack toolPickaxe;
		public ItemStack toolAxe;
		public ItemStack toolBow;
		public ItemStack toolFishingRod;
		public ItemStack toolSickle;
		public ItemStack toolHammer;
		public ItemStack toolExcavator;
		public ItemStack toolShield;

		public boolean[] enable = new boolean[12];

		ToolSet(String name, ToolMaterial material) {

			this.name = name.toLowerCase(Locale.US);
			TOOL_MATERIAL = material;
		}

		protected void create() {

			itemWrench = new ItemWrenchFlux(TOOL_MATERIAL);
			itemBattleWrench = new ItemBattleWrenchFlux(TOOL_MATERIAL);
			itemSword = new ItemSwordFlux(TOOL_MATERIAL);
			itemShovel = new ItemShovelFlux(TOOL_MATERIAL);
			itemPickaxe = new ItemPickaxeFlux(TOOL_MATERIAL);
			itemAxe = new ItemAxeFlux(TOOL_MATERIAL);
			itemBow = new ItemBowFlux(TOOL_MATERIAL);
			itemFishingRod = new ItemFishingRodFlux(TOOL_MATERIAL);
			itemSickle = new ItemSickleFlux(TOOL_MATERIAL);
			itemHammer = new ItemHammerFlux(TOOL_MATERIAL);
			itemExcavator = new ItemExcavatorFlux(TOOL_MATERIAL);
			itemShield = new ItemShieldFlux(TOOL_MATERIAL);
		}

		protected void preInit() {

			final String TOOL = "redstonearsenal.tool." + name;

			String category = "Equipment.Tools." + StringHelper.titleCase(name);

			enable[0] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Wrench", true).getBoolean(true);
			enable[1] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "BattleWrench", true).getBoolean(true);
			enable[2] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Sword", true).getBoolean(true);
			enable[3] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Shovel", true).getBoolean(true);
			enable[4] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Pickaxe", true).getBoolean(true);
			enable[5] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Axe", true).getBoolean(true);
			enable[6] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Bow", true).getBoolean(true);
			enable[7] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "FishingRod", true).getBoolean(true);
			enable[8] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Sickle", true).getBoolean(true);
			enable[9] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Hammer", true).getBoolean(true);
			enable[10] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Excavator", true).getBoolean(true);
			enable[11] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Shield", true).getBoolean(true);

			create();

			/* WRENCH */
			itemWrench.setUnlocalizedName(TOOL + "Wrench").setCreativeTab(RedstoneArsenal.tabBasicTools);
			itemWrench.setShowInCreative(enable[0]);
			itemWrench.setRegistryName("tool.wrench_" + name);
			ForgeRegistries.ITEMS.register(itemWrench);

			/* BATTLEWRENCH */
			itemBattleWrench.setUnlocalizedName(TOOL + "BattleWrench").setCreativeTab(RedstoneArsenal.tabBasicCombat);
			itemBattleWrench.setShowInCreative(enable[1]);
			itemBattleWrench.setRegistryName("tool.battlewrench_" + name);
			ForgeRegistries.ITEMS.register(itemBattleWrench);

			/* SWORD */
			itemSword.setUnlocalizedName(TOOL + "Sword").setCreativeTab(RedstoneArsenal.tabBasicCombat);
			itemSword.setShowInCreative(enable[2]);
			itemSword.setRegistryName("tool.sword_" + name);
			ForgeRegistries.ITEMS.register(itemSword);

			/* SHOVEL */
			itemShovel.setUnlocalizedName(TOOL + "Shovel").setCreativeTab(RedstoneArsenal.tabBasicTools);
			itemShovel.setShowInCreative(enable[3]);
			itemShovel.setRegistryName("tool.shovel_" + name);
			ForgeRegistries.ITEMS.register(itemShovel);

			/* PICKAXE */
			itemPickaxe.setUnlocalizedName(TOOL + "Pickaxe").setCreativeTab(RedstoneArsenal.tabBasicTools);
			itemPickaxe.setShowInCreative(enable[4]);
			itemPickaxe.setRegistryName("tool.pickaxe_" + name);
			ForgeRegistries.ITEMS.register(itemPickaxe);

			/* AXE */
			itemAxe.setUnlocalizedName(TOOL + "Axe").setCreativeTab(RedstoneArsenal.tabBasicTools);
			itemAxe.setShowInCreative(enable[5]);
			itemAxe.setRegistryName("tool.axe_" + name);
			ForgeRegistries.ITEMS.register(itemAxe);

			/* BOW */
			itemBow.setUnlocalizedName(TOOL + "Bow").setCreativeTab(RedstoneArsenal.tabBasicCombat);
			itemBow.setArrowDamage(arrowDamage).setArrowSpeed(arrowSpeed).setZoomMultiplier(zoomMultiplier);
			itemBow.setShowInCreative(enable[6]);
			itemBow.setRegistryName("tool.bow_" + name);
			ForgeRegistries.ITEMS.register(itemBow);

			/* FISHING ROD */
			itemFishingRod.setUnlocalizedName(TOOL + "FishingRod").setCreativeTab(RedstoneArsenal.tabBasicTools);
			itemFishingRod.setLuckModifier(luckModifier).setSpeedModifier(speedModifier);
			itemFishingRod.setShowInCreative(enable[7]);
			itemFishingRod.setRegistryName("tool.fishing_rod_" + name);
			ForgeRegistries.ITEMS.register(itemFishingRod);

			/* SICKLE */
			itemSickle.setUnlocalizedName(TOOL + "Sickle").setCreativeTab(RedstoneArsenal.tabBasicTools);
			itemSickle.setShowInCreative(enable[8]);
			itemSickle.setRegistryName("tool.sickle_" + name);
			ForgeRegistries.ITEMS.register(itemSickle);

			/* HAMMER */
			itemHammer.setUnlocalizedName(TOOL + "Hammer").setCreativeTab(RedstoneArsenal.tabBasicTools);
			itemHammer.setShowInCreative(enable[9]);
			itemHammer.setRegistryName("tool.hammer_" + name);
			ForgeRegistries.ITEMS.register(itemHammer);

			/* EXCAVATOR */
			itemExcavator.setUnlocalizedName(TOOL + "Excavator").setCreativeTab(RedstoneArsenal.tabBasicTools);
			itemExcavator.setShowInCreative(enable[10]);
			itemExcavator.setRegistryName("tool.excavator_" + name);
			ForgeRegistries.ITEMS.register(itemExcavator);

			/* SHIELD */
			itemShield.setUnlocalizedName(TOOL + "Shield").setCreativeTab(RedstoneArsenal.tabBasicCombat);
			itemShield.setShowInCreative(enable[11]);
			itemShield.setRegistryName("tool.shield_" + name);
			ForgeRegistries.ITEMS.register(itemShield);


			/* REFERENCES */
			toolWrench = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemWrench), 0);
			toolBattleWrench = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemBattleWrench), 0);
			toolSword = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemSword), 0);
			toolShovel = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemShovel), 0);
			toolPickaxe = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemPickaxe), 0);
			toolAxe = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemAxe), 0);
			toolBow = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemBow), 0);
			toolFishingRod = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemFishingRod), 0);
			toolSickle = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemSickle), 0);
			toolHammer = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemHammer), 0);
			toolExcavator = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemExcavator), 0);
			toolShield = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemShield), 0);
		}

		protected void initialize() {

			if (enable[0]) {
				addShapedRecipe(toolWrench, "I I", " R ", " I ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux);
			}
			if (enable[1]) {
				addShapedRecipe(toolBattleWrench, "I I", " G ", " R ", 'I', "ingotElectrumFlux", 'G', "gearElectrumFlux", 'R', ItemMaterial.rodObsidianFlux);
			}
			if (enable[2]) {
				addShapedRecipe(toolSword, " I ", " I ", " R ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux);
			}
			if (enable[3]) {
				addShapedRecipe(toolShovel, " I ", " R ", " R ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux);
			}
			if (enable[4]) {
				addShapedRecipe(toolPickaxe, "III", " R ", " R ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux);
			}
			if (enable[5]) {
				addShapedRecipe(toolAxe, "II ", "IR ", " R ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux);
			}
			if (enable[6]) {
				addShapedRecipe(toolBow, " IS", "R S", " IS", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux, 'S', "string");
			}
			if (enable[7]) {
				addShapedRecipe(toolFishingRod, "  I", " IS", "R S", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux, 'S', "string");
			}
			if (enable[8]) {
				addShapedRecipe(toolSickle, " I ", "  I", "RI ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux);
			}
			if (enable[9]) {
				addShapedRecipe(toolHammer, "III", "IRI", " R ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux);
			}
			if (enable[10]) {
				addShapedRecipe(toolHammer, " I ", "IRI", " R ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux);
			}
			if (enable[11]) {
				addShapedRecipe(toolShield, "IRI", "III", " I ", 'I', "ingotElectrumFlux", 'R', "gemCrystalFlux");
			}
		}

		/* HELPERS */
		@SideOnly (Side.CLIENT)
		public void registerModel(Item item, String stackName) {

			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(RedstoneArsenal.MOD_ID + ":tool/" + stackName, "inventory"));
		}

		/* IModelRegister */
		@Override
		@SideOnly (Side.CLIENT)
		public void registerModels() {

			registerModel(itemWrench, "wrench_" + name);
			registerModel(itemBattleWrench, "battle_wrench_" + name);
			registerModel(itemSword, "sword_" + name);
			registerModel(itemShovel, "shovel_" + name);
			registerModel(itemPickaxe, "pickaxe_" + name);
			registerModel(itemAxe, "axe_" + name);
			registerModel(itemBow, "bow_" + name);
			registerModel(itemFishingRod, "fishing_rod_" + name);
			registerModel(itemSickle, "sickle_" + name);
			registerModel(itemHammer, "hammer_" + name);
			registerModel(itemExcavator, "excavator_" + name);
			registerModel(itemShield, "shield_" + name);
		}
	}

}
