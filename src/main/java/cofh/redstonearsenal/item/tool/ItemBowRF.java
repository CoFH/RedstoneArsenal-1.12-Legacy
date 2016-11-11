package cofh.redstonearsenal.item.tool;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.item.IEmpowerableItem;
import cofh.core.item.IEqualityOverrideItem;
import cofh.core.item.tool.ItemBowAdv;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.core.RAProps;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBowRF extends ItemBowAdv implements IEmpowerableItem, IEnergyContainerItem, IEqualityOverrideItem {

	public int maxEnergy = 160000;
	public int maxTransfer = 1600;
	public int energyPerUse = 200;
	public int energyPerUseCharged = 800;

	private static String name;

	public ItemBowRF(Item.ToolMaterial toolMaterial, String nameIn) {
		super(toolMaterial);
		name = nameIn;
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setArrowSpeed(3.0F);
		setArrowDamage(1.5F);
		setCreativeTab(RedstoneArsenal.tab);
		setNoRepair();
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

	@SideOnly(Side.CLIENT)
	public void initModel(String name) {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(RedstoneArsenal.modId + ":" + name, "inventory"));
	}

	private int getPullStage(ItemStack stack, EntityLivingBase entityIn) {
		float useTime = (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F;
		if (useTime < 0.65F) {
			return 0;
		}
		else if (useTime >= 0.65F && useTime < 0.9F) {
			return 1;
		}
		else {
			return 2;
		}
	}

	private boolean isPulling(ItemStack stack, EntityLivingBase entityIn) {
		return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack;
	}

	@Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) &&
                !(!slotChanged && oldStack.isItemEqual(newStack) && getEnergyStored(oldStack) < getEnergyStored(newStack));
    }

    protected int getEnergyPerUse(ItemStack stack) {
		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 4);
		return (isEmpowered(stack) ? energyPerUseCharged : energyPerUse) * (5 - unbreakingLevel) / 5;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), 0));
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), maxEnergy));
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (!player.capabilities.isCreativeMode && getEnergyStored(stack) < getEnergyPerUse(stack)) {
			return new ActionResult(EnumActionResult.FAIL, stack);
		}
		return super.onItemRightClick(stack, world, player, hand);
	}

	@Override
	public void onBowFired(EntityPlayer player, ItemStack stack) {
		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 4);
		extractEnergy(stack, isEmpowered(stack) ? energyPerUseCharged * (5 - unbreakingLevel) / 5 : energyPerUse * (5 - unbreakingLevel) / 5, false);
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
		return !RAProps.showToolCharge ? false : stack.getTagCompound() == null || !stack.getTagCompound().getBoolean("CreativeTab");
	}

	@Override
	public boolean isDamaged(ItemStack stack) {
		return true;
	}

	/* IEmpowerableItem */
	@Override
	public boolean isEmpowered(ItemStack stack) {
		return stack.getTagCompound() == null ? false : stack.getTagCompound().getBoolean("Empowered");
	}

	@Override
	public boolean setEmpoweredState(ItemStack stack, boolean state) {
		if (getEnergyStored(stack) > 0) {
			stack.getTagCompound().setBoolean("Empowered", state);
			return true;
		}
		stack.getTagCompound().setBoolean("Empowered", false);
		return false;
	}

	@Override
	public void onStateChange(EntityPlayer player, ItemStack stack) {
        if (isEmpowered(stack)) {
            player.worldObj.playSound(null, player.getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.4F, 1.0F);
        }
        else {
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
				setEmpoweredState(container, false);
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
