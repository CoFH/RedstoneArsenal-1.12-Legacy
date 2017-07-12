package cofh.redstonearsenal.item.armor;

import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.ItemArmorCore;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.redstonearsenal.init.RAProps;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.util.EnergyContainerItemWrapper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class ItemArmorRF extends ItemArmorCore implements ISpecialArmor, IEnergyContainerItem, IEnchantableItem {

	private static final ArmorProperties FLUX = new ArmorProperties(0, 0.20D, Integer.MAX_VALUE);
	private static final ArmorProperties FALL = new ArmorProperties(0, 0.00D, 0);

	protected int maxEnergy = 800000;
	protected int maxTransfer = 4000;

	protected double absorbRatio = 0.9D;
	protected int energyPerDamage = 160;

	public ItemArmorRF(ArmorMaterial material, EntityEquipmentSlot type) {

		super(material, type);
	}

	public ItemArmorRF setEnergyParams(int maxEnergy, int maxTransfer) {

		this.maxEnergy = maxEnergy;
		this.maxTransfer = maxTransfer;

		return this;
	}

	protected int getEnergyPerDamage(ItemStack stack) {

		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 4);
		return energyPerDamage * (5 - unbreakingLevel) / 5;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (stack.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		tooltip.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.formatNumber(stack.getTagCompound().getInteger("Energy")) + " / " + StringHelper.formatNumber(getMaxEnergyStored(stack)) + " RF");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab) && showInCreative) {
			items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, 0), 0));
			items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, 0), maxEnergy));
		}
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return false;
	}

	@Override
	public boolean isDamaged(ItemStack stack) {

		return true;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || !ItemHelper.areItemStacksEqualIgnoreTags(oldStack, newStack, "Energy"));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {

		return !RAProps.showArmorCharge ? false : stack.getTagCompound() == null || !stack.getTagCompound().getBoolean("CreativeTab");
	}

	@Override
	public int getMaxDamage(ItemStack stack) {

		return 0;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {

		return CoreProps.RGB_DURABILITY_FLUX;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		return 1D - (double) stack.getTagCompound().getInteger("Energy") / (double) getMaxEnergyStored(stack);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		return EnumRarity.UNCOMMON;
	}

	/* ISpecialArmor */
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {

		if (source.damageType.equals("flux")) {
			return FLUX;
		} else if (source.damageType.equals("fall")) {
			if (slot == 0) {
				int absorbMax = getEnergyPerDamage(armor) > 0 ? 25 * getEnergyStored(armor) / getEnergyPerDamage(armor) : 0;
				return new ArmorProperties(0, absorbRatio * getArmorMaterial().getDamageReductionAmount(armorType) * 0.25, absorbMax);
			}
			return FALL;
		} else if (source.isUnblockable()) {
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
			boolean p = source.getTrueSource() == null;
			receiveEnergy(armor, damage * (p ? energyPerDamage / 2 : getEnergyPerDamage(armor)), false);
		} else {
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
		int receive = Math.min(maxReceive, Math.min(getMaxEnergyStored(container) - stored, maxTransfer));

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

		int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, container);
		return maxEnergy + maxEnergy * enchant / 2;
	}

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		return enchantment == CoreEnchantments.holding;
	}

	/* CAPABILITIES */
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {

		return new EnergyContainerItemWrapper(stack, this);
	}

}
