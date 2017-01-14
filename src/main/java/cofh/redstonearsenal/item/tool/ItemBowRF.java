package cofh.redstonearsenal.item.tool;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.item.IMultiModeItem;
import cofh.core.item.IEqualityOverrideItem;
import cofh.core.item.tool.ItemBowAdv;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.core.RAProps;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBowRF extends ItemBowAdv implements IMultiModeItem, IEnergyContainerItem, IEqualityOverrideItem {

	protected int maxEnergy = 160000;
	protected int maxTransfer = 1600;

	protected int energyPerUse = 200;
	protected int energyPerUseCharged = 800;

	public ItemBowRF(Item.ToolMaterial toolMaterial) {

		super(toolMaterial);

		this.toolMaterial = toolMaterial;
		setMaxDamage(toolMaterial.getMaxUses());

		setNoRepair();

		setArrowDamage(1.5F);
		setArrowSpeed(3.0F);

		addPropertyOverride(new ResourceLocation("flux_bow_empowered"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {

				return ItemBowRF.this.getEnergyStored(stack) > 0 && ItemBowRF.this.isEmpowered(stack) && !ItemBowRF.this.isPulling(stack, entity) ? 1F : 0F;
			}
		});
		addPropertyOverride(new ResourceLocation("flux_bow_empowered_0"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {

				return ItemBowRF.this.getEnergyStored(stack) > 0 && ItemBowRF.this.isEmpowered(stack) && ItemBowRF.this.isPulling(stack, entity) && ItemBowRF.this.getPullStage(stack, entity) == 0 ? 1F : 0F;
			}
		});
		addPropertyOverride(new ResourceLocation("flux_bow_empowered_1"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {

				return ItemBowRF.this.getEnergyStored(stack) > 0 && ItemBowRF.this.isEmpowered(stack) && ItemBowRF.this.isPulling(stack, entity) && ItemBowRF.this.getPullStage(stack, entity) == 1 ? 1F : 0F;
			}
		});
		addPropertyOverride(new ResourceLocation("flux_bow_empowered_2"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {

				return ItemBowRF.this.getEnergyStored(stack) > 0 && ItemBowRF.this.isEmpowered(stack) && ItemBowRF.this.isPulling(stack, entity) && ItemBowRF.this.getPullStage(stack, entity) == 2 ? 1F : 0F;
			}
		});
		addPropertyOverride(new ResourceLocation("flux_bow_active"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {

				return ItemBowRF.this.getEnergyStored(stack) > 0 && !ItemBowRF.this.isEmpowered(stack) && !ItemBowRF.this.isPulling(stack, entity) ? 1F : 0F;
			}
		});
		addPropertyOverride(new ResourceLocation("flux_bow_active_0"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {

				return ItemBowRF.this.getEnergyStored(stack) > 0 && !ItemBowRF.this.isEmpowered(stack) && ItemBowRF.this.isPulling(stack, entity) && ItemBowRF.this.getPullStage(stack, entity) == 0 ? 1F : 0F;
			}
		});
		addPropertyOverride(new ResourceLocation("flux_bow_active_1"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {

				return ItemBowRF.this.getEnergyStored(stack) > 0 && !ItemBowRF.this.isEmpowered(stack) && ItemBowRF.this.isPulling(stack, entity) && ItemBowRF.this.getPullStage(stack, entity) == 1 ? 1F : 0F;
			}
		});
		addPropertyOverride(new ResourceLocation("flux_bow_active_2"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {

				return ItemBowRF.this.getEnergyStored(stack) > 0 && !ItemBowRF.this.isEmpowered(stack) && ItemBowRF.this.isPulling(stack, entity) && ItemBowRF.this.getPullStage(stack, entity) == 2 ? 1F : 0F;
			}
		});
	}

	public ItemBowRF setEnergyParams(int maxEnergy, int maxTransfer, int energyPerUse, int energyPerUseCharged) {

		this.maxEnergy = maxEnergy;
		this.maxTransfer = maxTransfer;
		this.energyPerUse = energyPerUse;
		this.energyPerUseCharged = energyPerUseCharged;

		return this;
	}

	protected boolean isEmpowered(ItemStack stack) {

		return getMode(stack) == 1;
	}

	protected boolean isPulling(ItemStack stack, EntityLivingBase entity) {

		return entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack;
	}

	protected int getEnergyPerUse(ItemStack stack) {

		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 4);
		return (isEmpowered(stack) ? energyPerUseCharged : energyPerUse) * (5 - unbreakingLevel) / 5;
	}

	protected int getPullStage(ItemStack stack, EntityLivingBase entityIn) {

		float useTime = (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F;
		if (useTime < 0.65F) {
			return 0;
		} else if (useTime >= 0.65F && useTime < 0.9F) {
			return 1;
		} else {
			return 2;
		}
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

		list.add(StringHelper.ORANGE + getEnergyPerUse(stack) + " " + StringHelper.localize("info.redstonearsenal.tool.energyPerUse") + StringHelper.END);
		RAProps.addEmpoweredTip(this, stack, list);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {

		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), 0));
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), maxEnergy));
	}

	@Override
	public void onBowFired(EntityPlayer player, ItemStack stack) {

		if (player.capabilities.isCreativeMode) {
			return;
		}
		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 4);
		extractEnergy(stack, isEmpowered(stack) ? energyPerUseCharged * (5 - unbreakingLevel) / 5 : energyPerUse * (5 - unbreakingLevel) / 5, false);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isCurrentItem) {

		if (stack.getItemDamage() > 0) {
			stack.setItemDamage(0);
		}
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {

		super.setDamage(stack, 0);
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

		return !RAProps.showToolCharge ? false : stack.getTagCompound() == null || !stack.getTagCompound().getBoolean("CreativeTab");
	}

	@Override
	public int getMaxDamage(ItemStack stack) {

		return 0;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		return 1D - (double) stack.getTagCompound().getInteger("Energy") / (double) maxEnergy;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		return isEmpowered(stack) ? EnumRarity.RARE : EnumRarity.UNCOMMON;
	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {

		if (!player.capabilities.isCreativeMode && getEnergyStored(stack) < getEnergyPerUse(stack)) {
			return new ActionResult(EnumActionResult.FAIL, stack);
		}
		return super.onItemRightClick(stack, world, player, hand);
	}

	@SideOnly (Side.CLIENT)
	public void registerModel(String name) {

		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(RedstoneArsenal.MOD_ID + ":" + name, "inventory"));
	}

	/* IMultiModeItem */
	@Override
	public int getMode(ItemStack stack) {

		return !stack.hasTagCompound() ? 0 : stack.getTagCompound().getInteger("Mode");
	}

	@Override
	public boolean setMode(ItemStack stack, int mode) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger("Mode", mode);
		return false;
	}

	@Override
	public boolean incrMode(ItemStack stack) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode++;
		if (curMode >= getNumModes(stack)) {
			curMode = 0;
		}
		stack.getTagCompound().setInteger("Mode", curMode);
		return true;
	}

	@Override
	public boolean decrMode(ItemStack stack) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode--;
		if (curMode <= 0) {
			curMode = getNumModes(stack) - 1;
		}
		stack.getTagCompound().setInteger("Mode", curMode);
		return true;
	}

	@Override
	public int getNumModes(ItemStack stack) {

		return 2;
	}

	@Override
	public void onModeChange(EntityPlayer player, ItemStack stack) {

		if (isEmpowered(stack)) {
			player.worldObj.playSound(null, player.getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.4F, 1.0F);
		} else {
			player.worldObj.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, SoundCategory.PLAYERS, 0.2F, 0.6F);
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
		if (container.getTagCompound().hasKey("Unbreakable")) {
			container.getTagCompound().removeTag("Unbreakable");
		}
		int stored = container.getTagCompound().getInteger("Energy");
		int extract = Math.min(maxExtract, stored);

		if (!simulate) {
			stored -= extract;
			container.getTagCompound().setInteger("Energy", stored);

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
		return container.getTagCompound().getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		return maxEnergy;
	}

	/* IEqualityOverrideItem */
	@Override
	public boolean isLastHeldItemEqual(ItemStack current, ItemStack previous) {

		NBTTagCompound a = current.getTagCompound(), b = previous.getTagCompound();
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		a = a.copy();
		b = b.copy();
		a.removeTag("Energy");
		b.removeTag("Energy");
		return a.equals(b);
	}

}
