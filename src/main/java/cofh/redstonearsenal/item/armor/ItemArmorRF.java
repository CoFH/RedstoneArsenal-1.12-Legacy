package cofh.redstonearsenal.item.armor;

import java.util.List;

import cofh.api.energy.IEnergyContainerItem;
import cofh.core.item.ItemArmorAdv;
import cofh.lib.util.helpers.*;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.core.RAProps;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.*;

public class ItemArmorRF extends ItemArmorAdv implements ISpecialArmor, IEnergyContainerItem {

	public static final ArmorProperties FLUX = new ArmorProperties(0, 0.20D, Integer.MAX_VALUE);

	public int maxEnergy = 400000;
	public int maxTransfer = 2000;

	public double absorbRatio = 0.9D;
	public int energyPerDamage = 160;

	public String[] textures = new String[2];

	private static String name;

	public ItemArmorRF(ArmorMaterial material, EntityEquipmentSlot type, String nameIn) {
		super(material, type);
		name = nameIn;
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setCreativeTab(RedstoneArsenal.tab);
	}

	@SideOnly(Side.CLIENT)
	public void initModel(String name) {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(RedstoneArsenal.modId + ":" + name, "inventory"));
	}

	public ItemArmorRF setEnergyParams(int maxEnergy, int maxTransfer) {
		this.maxEnergy = maxEnergy;
		this.maxTransfer = maxTransfer;

		return this;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {
		return false;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check) {
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (stack.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		list.add(StringHelper.localize("info.cofh.charge") + ": " + stack.getTagCompound().getInteger("Energy") + " / " + maxEnergy + " RF");
	}

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged)
                && !(!slotChanged && oldStack.isItemEqual(newStack) && getEnergyStored(oldStack) < getEnergyStored(newStack));
    }

    @Override
	public double getDurabilityForDisplay(ItemStack stack) {
		if (stack.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		return 1D - (double) stack.getTagCompound().getInteger("Energy") / (double) maxEnergy;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return 0;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return !RAProps.showArmorCharge ? false : stack.getTagCompound() == null || !stack.getTagCompound().getBoolean("CreativeTab");
	}

	@Override
	public boolean isDamaged(ItemStack stack) {
		return true;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), 0));
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), maxEnergy));
	}

	protected int getBaseAbsorption() {
		return 20;
	}

	protected int getEnergyPerDamage(ItemStack stack) {
		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 4);
		return energyPerDamage * (5 - unbreakingLevel) / 5;
	}

	/* ISpecialArmor */
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {

		if (source.damageType.equals("flux")) {
			return FLUX;
		}
		else if (source.isUnblockable()) {
			int absorbMax = getEnergyPerDamage(armor) > 0 ? 25 * getEnergyStored(armor) / getEnergyPerDamage(armor) : 0;
			return new ArmorProperties(0, absorbRatio * getArmorMaterial().getDamageReductionAmount(armorType) * 0.025, absorbMax);
		}
		int absorbMax = getEnergyPerDamage(armor) > 0 ? 25 * getEnergyStored(armor) / getEnergyPerDamage(armor) : 0;
		return new ArmorProperties(0, absorbRatio * getArmorMaterial().getDamageReductionAmount(armorType) * 0.05, absorbMax);
		// 0.05 = 1 / 20 (max armor)
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		if (getEnergyStored(armor) >= getEnergyPerDamage(armor)) {
			return getArmorMaterial().getDamageReductionAmount(armorType);
		}
		return 0;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack armor, DamageSource source, int damage, int slot) {
		if (source.damageType.equals("flux")) {
			boolean p = source.getEntity() == null;
			receiveEnergy(armor, damage * (p ? energyPerDamage / 2 : getEnergyPerDamage(armor)), false);
		}
		else {
			extractEnergy(armor, damage * getEnergyPerDamage(armor), false);
		}
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = container.getTagCompound().getInteger("Energy");
		int receive = Math.min(maxReceive, Math.min(maxEnergy - stored, maxTransfer));

		if (!simulate) {
			stored += receive;
			container.getTagCompound().setInteger("Energy", stored);
		}
		return receive;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = container.getTagCompound().getInteger("Energy");
		int extract = Math.min(maxExtract, stored);

		if (!simulate) {
			stored -= extract;
			container.getTagCompound().setInteger("Energy", stored);
		}
		return extract;
	}

	@Override
	public int getEnergyStored(ItemStack container) {
		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		return container.getTagCompound().getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {
		return maxEnergy;
	}

}
