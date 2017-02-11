package cofh.redstonearsenal.init;

import cofh.api.core.IModelRegister;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.item.ItemMaterial;
import cofh.redstonearsenal.item.armor.ItemArmorRF;
import cofh.redstonearsenal.item.tool.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;
import static cofh.lib.util.helpers.ItemHelper.addRecipe;

public class RAEquipment {

	private RAEquipment() {

	}

	public static boolean preInit() {

		for (ArmorSet e : ArmorSet.values()) {
			e.preInit();
			RedstoneArsenal.proxy.addIModelRegister(e);
		}
		for (ToolSet e : ToolSet.values()) {
			e.preInit();
			RedstoneArsenal.proxy.addIModelRegister(e);
		}
		return true;
	}

	public static boolean initialize() {

		return true;
	}

	public static boolean postInit() {

		for (ArmorSet e : ArmorSet.values()) {
			e.postInit();
		}
		for (ToolSet e : ToolSet.values()) {
			e.postInit();
		}
		return true;
	}

	/* MATERIALS */
	public static final ArmorMaterial ARMOR_MATERIAL_FLUX = EnumHelper.addArmorMaterial("RA:FLUXELECTRUM", "flux_armor", 100, new int[] { 3, 6, 8, 3 }, 20, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2F);
	public static final ToolMaterial TOOL_MATERIAL_FLUX = EnumHelper.addToolMaterial("RA:FLUXELECTRUM", 4, 100, 8.0F, 0, 25);

	/* ARMOR */
	public enum ArmorSet implements IModelRegister {

		FLUX("flux", ARMOR_MATERIAL_FLUX);

		private final String name;
		private final ArmorMaterial ARMOR_MATERIAL;

		public ItemArmorRF itemHelmet;
		public ItemArmorRF itemPlate;
		public ItemArmorRF itemLegs;
		public ItemArmorRF itemBoots;

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

			itemHelmet = new ItemArmorRF(ARMOR_MATERIAL, EntityEquipmentSlot.HEAD);
			itemPlate = new ItemArmorRF(ARMOR_MATERIAL, EntityEquipmentSlot.CHEST);
			itemLegs = new ItemArmorRF(ARMOR_MATERIAL, EntityEquipmentSlot.LEGS);
			itemBoots = new ItemArmorRF(ARMOR_MATERIAL, EntityEquipmentSlot.FEET);
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
			itemHelmet.setArmorTextures(TEXTURE).setUnlocalizedName(ARMOR + "Helmet").setCreativeTab(RedstoneArsenal.tabCommon);
			itemHelmet.setShowInCreative(enable[0]);
			itemHelmet.setRegistryName("armor.helmet_" + name);
			GameRegistry.register(itemHelmet);

			/* PLATE */
			itemPlate.setArmorTextures(TEXTURE).setUnlocalizedName(ARMOR + "Plate").setCreativeTab(RedstoneArsenal.tabCommon);
			itemPlate.setShowInCreative(enable[1]);
			itemPlate.setRegistryName("armor.plate_" + name);
			GameRegistry.register(itemPlate);

			/* LEGS */
			itemLegs.setArmorTextures(TEXTURE).setUnlocalizedName(ARMOR + "Legs").setCreativeTab(RedstoneArsenal.tabCommon);
			itemLegs.setShowInCreative(enable[2]);
			itemLegs.setRegistryName("armor.legs_" + name);
			GameRegistry.register(itemLegs);

			/* BOOTS */
			itemBoots.setArmorTextures(TEXTURE).setUnlocalizedName(ARMOR + "Boots").setCreativeTab(RedstoneArsenal.tabCommon);
			itemBoots.setShowInCreative(enable[3]);
			itemBoots.setRegistryName("armor.boots_" + name);
			GameRegistry.register(itemBoots);

			/* REFERENCES */
			armorHelmet = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemHelmet), 0);
			armorPlate = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemPlate), 0);
			armorLegs = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemLegs), 0);
			armorBoots = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemBoots), 0);
		}

		protected void postInit() {

			if (enable[0]) {
				addRecipe(ShapedRecipe(armorHelmet, "III", "I I", 'I', ItemMaterial.plateFlux));
			}
			if (enable[1]) {
				addRecipe(ShapedRecipe(armorPlate, "I I", "III", "III", 'I', ItemMaterial.plateFlux));
			}
			if (enable[2]) {
				addRecipe(ShapedRecipe(armorLegs, "III", "I I", "I I", 'I', ItemMaterial.plateFlux));
			}
			if (enable[3]) {
				addRecipe(ShapedRecipe(armorBoots, "I I", "I I", 'I', ItemMaterial.plateFlux));
			}
		}

		/* HELPERS */
		@SideOnly (Side.CLIENT)
		public void registerModel(Item item, String stackName) {

			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(RedstoneArsenal.MOD_ID + ":armor/" + name + "/" + stackName, "inventory"));
		}

		/* IModelRegister */
		@Override
		@SideOnly (Side.CLIENT)
		public void registerModels() {

			registerModel(itemHelmet, name + "_helmet");
			registerModel(itemPlate, name + "_chestplate");
			registerModel(itemLegs, name + "_leggings");
			registerModel(itemBoots, name + "_boots");
		}
	}

	/* TOOLS */
	public enum ToolSet implements IModelRegister {

		FLUX("flux", TOOL_MATERIAL_FLUX);

		private final String name;
		private final ToolMaterial TOOL_MATERIAL;

		/* BOW */
		private float arrowDamage = 1.5F;
		private float arrowSpeed = 3.0F;

		/* FISHING ROD */
		private int luckModifier = 1;
		private int speedModifier = 1;

		/* TOOLS */
		public ItemWrenchRF itemWrench;
		public ItemBattleWrenchRF itemBattleWrench;
		public ItemSwordRF itemSword;
		public ItemShovelRF itemShovel;
		public ItemPickaxeRF itemPickaxe;
		public ItemAxeRF itemAxe;
		public ItemBowRF itemBow;
		public ItemFishingRodRF itemFishingRod;
		public ItemSickleRF itemSickle;
		public ItemHammerRF itemHammer;
		public ItemShieldRF itemShield;

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
		public ItemStack toolShield;

		public boolean[] enable = new boolean[11];

		ToolSet(String name, ToolMaterial material) {

			this.name = name.toLowerCase(Locale.US);
			TOOL_MATERIAL = material;
		}

		protected void create() {

			itemWrench = new ItemWrenchRF(TOOL_MATERIAL);
			itemBattleWrench = new ItemBattleWrenchRF(TOOL_MATERIAL);
			itemSword = new ItemSwordRF(TOOL_MATERIAL);
			itemShovel = new ItemShovelRF(TOOL_MATERIAL);
			itemPickaxe = new ItemPickaxeRF(TOOL_MATERIAL);
			itemAxe = new ItemAxeRF(TOOL_MATERIAL);
			itemBow = new ItemBowRF(TOOL_MATERIAL);
			itemFishingRod = new ItemFishingRodRF(TOOL_MATERIAL);
			itemSickle = new ItemSickleRF(TOOL_MATERIAL);
			itemHammer = new ItemHammerRF(TOOL_MATERIAL);
			itemShield = new ItemShieldRF(TOOL_MATERIAL);
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

			// TODO: Add Hammer/Shield
			// enable[9] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Hammer", true).getBoolean(true);
			// enable[10] = RedstoneArsenal.CONFIG.getConfiguration().get(category, "Shield", true).getBoolean(true);

			enable[9] = false;
			enable[10] = false;

			create();

			/* WRENCH */
			itemWrench.setUnlocalizedName(TOOL + "Wrench").setCreativeTab(RedstoneArsenal.tabCommon);
			itemWrench.setShowInCreative(enable[0]);
			itemWrench.setRegistryName("tool.wrench_" + name);
			GameRegistry.register(itemWrench);

			/* BATTLEWRENCH */
			itemBattleWrench.setUnlocalizedName(TOOL + "BattleWrench").setCreativeTab(RedstoneArsenal.tabCommon);
			itemBattleWrench.setShowInCreative(enable[1]);
			itemBattleWrench.setRegistryName("tool.battlewrench_" + name);
			GameRegistry.register(itemBattleWrench);

			/* SWORD */
			itemSword.setUnlocalizedName(TOOL + "Sword").setCreativeTab(RedstoneArsenal.tabCommon);
			itemSword.setShowInCreative(enable[2]);
			itemSword.setRegistryName("tool.sword_" + name);
			GameRegistry.register(itemSword);

			/* SHOVEL */
			itemShovel.setUnlocalizedName(TOOL + "Shovel").setCreativeTab(RedstoneArsenal.tabCommon);
			itemShovel.setShowInCreative(enable[3]);
			itemShovel.setRegistryName("tool.shovel_" + name);
			GameRegistry.register(itemShovel);

			/* PICKAXE */
			itemPickaxe.setUnlocalizedName(TOOL + "Pickaxe").setCreativeTab(RedstoneArsenal.tabCommon);
			itemPickaxe.setShowInCreative(enable[4]);
			itemPickaxe.setRegistryName("tool.pickaxe_" + name);
			GameRegistry.register(itemPickaxe);

			/* AXE */
			itemAxe.setUnlocalizedName(TOOL + "Axe").setCreativeTab(RedstoneArsenal.tabCommon);
			itemAxe.setShowInCreative(enable[5]);
			itemAxe.setRegistryName("tool.axe_" + name);
			GameRegistry.register(itemAxe);

			/* BOW */
			itemBow.setUnlocalizedName(TOOL + "Bow").setCreativeTab(RedstoneArsenal.tabCommon);
			itemBow.setArrowDamage(arrowDamage).setArrowSpeed(arrowSpeed);
			itemBow.setShowInCreative(enable[6]);
			itemBow.setRegistryName("tool.bow_" + name);
			GameRegistry.register(itemBow);

			/* FISHING ROD */
			itemFishingRod.setUnlocalizedName(TOOL + "FishingRod").setCreativeTab(RedstoneArsenal.tabCommon);
			itemFishingRod.setLuckModifier(luckModifier).setSpeedModifier(speedModifier);
			itemFishingRod.setShowInCreative(enable[7]);
			itemFishingRod.setRegistryName("tool.fishing_rod_" + name);
			GameRegistry.register(itemFishingRod);

			/* SICKLE */
			itemSickle.setUnlocalizedName(TOOL + "Sickle").setCreativeTab(RedstoneArsenal.tabCommon);
			itemSickle.setShowInCreative(enable[8]);
			itemSickle.setRegistryName("tool.sickle_" + name);
			GameRegistry.register(itemSickle);

			/* HAMMER */
			itemHammer.setUnlocalizedName(TOOL + "Hammer").setCreativeTab(RedstoneArsenal.tabCommon);
			itemHammer.setShowInCreative(enable[9]);
			itemHammer.setRegistryName("tool.hammer_" + name);
			GameRegistry.register(itemHammer);

			/* SHIELD */
			itemShield.setUnlocalizedName(TOOL + "Shield").setCreativeTab(RedstoneArsenal.tabCommon);
			itemShield.setShowInCreative(enable[10]);
			itemShield.setRegistryName("tool.shield_" + name);
			GameRegistry.register(itemShield);


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
			toolShield = EnergyHelper.setDefaultEnergyTag(new ItemStack(itemShield), 0);
		}

		protected void postInit() {

			if (enable[0]) {
				addRecipe(ShapedRecipe(toolWrench, "I I", " R ", " I ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux));
			}
			if (enable[1]) {
				addRecipe(ShapedRecipe(toolBattleWrench, "I I", " G ", " R ", 'I', "ingotElectrumFlux", 'G', "gearElectrumFlux", 'R', ItemMaterial.rodObsidianFlux));
			}
			if (enable[2]) {
				addRecipe(ShapedRecipe(toolSword, " I ", " I ", " R ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux));
			}
			if (enable[3]) {
				addRecipe(ShapedRecipe(toolShovel, " I ", " R ", " R ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux));
			}
			if (enable[4]) {
				addRecipe(ShapedRecipe(toolPickaxe, "III", " R ", " R ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux));
			}
			if (enable[5]) {
				addRecipe(ShapedRecipe(toolAxe, "II ", "IR ", " R ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux));
			}
			if (enable[6]) {
				addRecipe(ShapedRecipe(toolBow, " IS", "R S", " IS", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux, 'S', Items.STRING));
			}
			if (enable[7]) {
				addRecipe(ShapedRecipe(toolFishingRod, "  I", " IS", "R S", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux, 'S', Items.STRING));
			}
			if (enable[8]) {
				addRecipe(ShapedRecipe(toolSickle, " I ", "  I", "RI ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux));
			}
			if (enable[9]) {
				addRecipe(ShapedRecipe(toolHammer, "III", "IRI", " R ", 'I', "ingotElectrumFlux", 'R', ItemMaterial.rodObsidianFlux));
			}
			if (enable[10]) {
				addRecipe(ShapedRecipe(toolShield, "IRI", "III", " I ", 'I', "ingotElectrumFlux", 'R', "gemCrystalFlux"));
			}
		}

		/* HELPERS */
		@SideOnly (Side.CLIENT)
		public void registerModel(Item item, String stackName) {

			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(RedstoneArsenal.MOD_ID + ":tool/" + name + "/" + stackName, "inventory"));
		}

		/* IModelRegister */
		@Override
		@SideOnly (Side.CLIENT)
		public void registerModels() {

			registerModel(itemWrench, name + "_wrench");
			registerModel(itemBattleWrench, name + "_battle_wrench");
			registerModel(itemSword, name + "_sword");
			registerModel(itemShovel, name + "_shovel");
			registerModel(itemPickaxe, name + "_pickaxe");
			registerModel(itemAxe, name + "_axe");
			registerModel(itemBow, name + "_bow");
			registerModel(itemFishingRod, name + "_fishing_rod");
			registerModel(itemSickle, name + "_sickle");
			registerModel(itemHammer, name + "_hammer");
			registerModel(itemShield, name + "_shield");
		}
	}

}
