package redstonearsenal.item.armor;

import cofh.api.energy.IEnergyContainerItem;
import cofh.item.ItemArmorAdv;
import cofh.util.EnergyHelper;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

public class ItemArmorRF extends ItemArmorAdv implements ISpecialArmor, IEnergyContainerItem {

	public static final ArmorProperties UNBLOCKABLE = new ArmorProperties(0, 0.0D, 0);
	public static final ArmorProperties FLUX = new ArmorProperties(0, 0.5D, Integer.MAX_VALUE);

	public int maxEnergy = 400000;
	public int maxTransfer = 2000;

	public double absorbRatio = 0.8D;
	public int energyPerDamage = 80;

	public String[] textures = new String[2];

	public ItemArmorRF(ArmorMaterial material, int type) {

		super(material, type);
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return false;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		return EnumRarity.uncommon;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (stack.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		list.add(StringHelper.localize("info.cofh.charge") + ": " + stack.stackTagCompound.getInteger("Energy") + " / " + maxEnergy + " RF");
	}

	@Override
	public int getDisplayDamage(ItemStack stack) {

		if (stack.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		return 1 + maxEnergy - stack.stackTagCompound.getInteger("Energy");
	}

	@Override
	public int getMaxDamage(ItemStack stack) {

		return 1 + maxEnergy;
	}

	@Override
	public boolean isDamaged(ItemStack stack) {

		return stack.getItemDamage() != Short.MAX_VALUE;
	}

	protected int getBaseAbsorption() {

		return 20;
	}

	/**
	 * Returns a % that each piece absorbs, set sums to 100.
	 */
	protected int getAbsorptionRatio() {

		switch (armorType) {
		case 0:
			return 15;
		case 1:
			return 40;
		case 2:
			return 30;
		case 3:
			return 15;
		}
		return 0;
	}

	/* ISpecialArmor */
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {

		if (source.damageType.equals("flux")) {
			return FLUX;
		} else if (source.isUnblockable()) {
			return UNBLOCKABLE;
		}
		int absorbMax = energyPerDamage > 0 ? 25 * getEnergyStored(armor) / energyPerDamage : 0;
		return new ArmorProperties(0, absorbRatio, absorbMax);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {

		if (getEnergyStored(armor) >= energyPerDamage) {
			return Math.min(getBaseAbsorption(), 20) * getAbsorptionRatio() / 100;
		}
		return 0;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {

		if (source.damageType.equals("flux")) {
			receiveEnergy(stack, damage * energyPerDamage, false);
		} else {
			extractEnergy(stack, damage * energyPerDamage, false);
		}
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = container.stackTagCompound.getInteger("Energy");
		int receive = Math.min(maxReceive, Math.min(maxEnergy - stored, maxTransfer));

		if (!simulate) {
			stored += receive;
			container.stackTagCompound.setInteger("Energy", stored);
		}
		return receive;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = container.stackTagCompound.getInteger("Energy");
		int extract = Math.min(maxExtract, stored);

		if (!simulate) {
			stored -= extract;
			container.stackTagCompound.setInteger("Energy", stored);
		}
		return extract;
	}

	@Override
	public int getEnergyStored(ItemStack container) {

		if (container.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		return container.stackTagCompound.getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		return maxEnergy;
	}

}
