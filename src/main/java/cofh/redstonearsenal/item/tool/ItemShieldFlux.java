package cofh.redstonearsenal.item.tool;

import cofh.api.item.IMultiModeItem;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.tool.ItemShieldCore;
import cofh.core.util.helpers.DamageHelper;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.redstonearsenal.init.RAProps;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.util.EnergyContainerItemWrapper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class ItemShieldFlux extends ItemShieldCore implements IMultiModeItem, IEnergyContainerItem, IEnchantableItem {

	protected int maxEnergy = 320000;
	protected int maxTransfer = 4000;

	protected int energyPerUse = 200;
	protected int energyPerUseCharged = 800;

	protected int damageCharged = 6;

	public ItemShieldFlux(ToolMaterial toolMaterial) {

		super(toolMaterial);
		setNoRepair();

		addPropertyOverride(new ResourceLocation("active"), (stack, world, entity) -> ItemShieldFlux.this.getEnergyStored(stack) > 0 && !ItemShieldFlux.this.isEmpowered(stack) ? 1F : 0F);
		addPropertyOverride(new ResourceLocation("empowered"), (stack, world, entity) -> ItemShieldFlux.this.isEmpowered(stack) ? 1F : 0F);
	}

	public ItemShieldFlux setEnergyParams(int maxEnergy, int maxTransfer, int energyPerUse) {

		this.maxEnergy = maxEnergy;
		this.maxTransfer = maxTransfer;
		this.energyPerUse = energyPerUse;

		return this;
	}

	public ItemShieldFlux setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	protected boolean isEmpowered(ItemStack stack) {

		return getMode(stack) == 1 && getEnergyStored(stack) >= energyPerUseCharged;
	}

	protected int getEnergyPerUse(ItemStack stack) {

		return isEmpowered(stack) ? energyPerUseCharged : energyPerUse;
	}

	protected int useEnergy(ItemStack stack, boolean simulate) {

		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 10);
		if (MathHelper.RANDOM.nextInt(2 + unbreakingLevel) >= 2) {
			return 0;
		}
		return extractEnergy(stack, isEmpowered(stack) ? energyPerUseCharged : energyPerUse, simulate);
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
		tooltip.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(getEnergyStored(stack)) + " / " + StringHelper.getScaledNumber(getMaxEnergyStored(stack)) + " RF");
		tooltip.add(StringHelper.ORANGE + getEnergyPerUse(stack) + " " + StringHelper.localize("info.redstonearsenal.tool.energyPerUse") + StringHelper.END);
		RAProps.addEmpoweredTip(this, stack, tooltip);

		if (getEnergyStored(stack) >= getEnergyPerUse(stack)) {
			tooltip.add("");
			tooltip.add(StringHelper.BRIGHT_GREEN + (isEmpowered(stack) ? damageCharged : 1) + " " + StringHelper.localize("info.cofh.damageFlux") + StringHelper.END);
		}
	}

	@Override
	public void onHit(ItemStack stack, EntityPlayer player, Entity source) {

		if (source != null) {
			float potionDamage = 1.0F;
			if (player.isPotionActive(MobEffects.STRENGTH)) {
				potionDamage += player.getActivePotionEffect(MobEffects.STRENGTH).getAmplifier() * 1.3F;
			}
			source.attackEntityFrom(DamageHelper.causePlayerFluxDamage(player), (isEmpowered(stack) ? damageCharged : 1) * potionDamage);
		}
		if (!player.capabilities.isCreativeMode) {
			useEnergy(stack, false);
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab) && showInCreative) {
			items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, 0), 0));
			items.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, 0), maxEnergy));
		}
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {

		super.setDamage(stack, 0);
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return false;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase player) {

		if (stack.getItemDamage() > 0) {
			stack.setItemDamage(0);
		}
		EntityPlayer thePlayer = (EntityPlayer) player;

		if (thePlayer.capabilities.isCreativeMode || getEnergyStored(stack) >= getEnergyPerUse(stack)) {
			int fluxDamage = isEmpowered(stack) ? damageCharged : 1;

			float potionDamage = 1.0F;
			if (player.isPotionActive(MobEffects.STRENGTH)) {
				potionDamage += player.getActivePotionEffect(MobEffects.STRENGTH).getAmplifier() * 1.3F;
			}
			entity.attackEntityFrom(DamageHelper.causePlayerFluxDamage(thePlayer), fluxDamage * potionDamage);
			useEnergy(stack, thePlayer.capabilities.isCreativeMode);
		}
		return true;
	}

	@Override
	public boolean isDamageable() {

		return false;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {

		return RAProps.showToolCharge && getEnergyStored(stack) > 0;
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
		return 1D - (double) stack.getTagCompound().getInteger(CoreProps.ENERGY) / (double) getMaxEnergyStored(stack);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		return EnumRarity.UNCOMMON;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);
		if (!player.capabilities.isCreativeMode && getEnergyStored(stack) < getEnergyPerUse(stack)) {
			new ActionResult<>(EnumActionResult.FAIL, stack);
		}
		player.setActiveHand(hand);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	/* IMultiModeItem */
	@Override
	public void onModeChange(EntityPlayer player, ItemStack stack) {

		if (isEmpowered(stack)) {
			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.4F, 1.0F);
		} else {
			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.2F, 0.6F);
		}
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = Math.min(container.getTagCompound().getInteger(CoreProps.ENERGY), getMaxEnergyStored(container));
		int receive = Math.min(maxReceive, Math.min(getMaxEnergyStored(container) - stored, maxTransfer));

		if (!simulate) {
			stored += receive;
			container.getTagCompound().setInteger(CoreProps.ENERGY, stored);
		}
		return receive;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = Math.min(container.getTagCompound().getInteger(CoreProps.ENERGY), getMaxEnergyStored(container));
		int extract = Math.min(maxExtract, stored);

		if (!simulate) {
			stored -= extract;
			container.getTagCompound().setInteger(CoreProps.ENERGY, stored);

			if (stored == 0) {
				setMode(container, 0);
			}
		}
		return extract;
	}

	@Override
	public int getEnergyStored(ItemStack container) {

		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		return Math.min(container.getTagCompound().getInteger(CoreProps.ENERGY), getMaxEnergyStored(container));
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
