package cofh.redstonearsenal.item.tool;

import java.util.List;

import com.google.common.collect.*;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.item.IEmpowerableItem;
import cofh.core.item.IEqualityOverrideItem;
import cofh.core.item.tool.ItemToolAdv;
import cofh.lib.util.helpers.*;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.core.RAProps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.*;

public abstract class ItemToolRF extends ItemToolAdv implements IEmpowerableItem, IEnergyContainerItem, IEqualityOverrideItem {

	// IIcon activeIcon;
	// IIcon drainedIcon;

	public int maxEnergy = 160000;
	public int maxTransfer = 1600;
	public int energyPerUse = 200;
	public int energyPerUseCharged = 800;

	int damage = 0;

	private static String name;

	public ItemToolRF(Item.ToolMaterial toolMaterial, String nameIn, float attackSpeed) {
        super(0, attackSpeed, toolMaterial);
        name = nameIn;
        setNoRepair();
        setMaxStackSize(1);
    }

	public ItemToolRF(Item.ToolMaterial toolMaterial, String nameIn) {
	    this(toolMaterial, nameIn, -2.9F);
	}

	public ItemToolRF(Item.ToolMaterial toolMaterial, int harvestLevel, String nameIn) {
		super(0, toolMaterial, harvestLevel);
		name = nameIn;
		setNoRepair();
		setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	public void initModel(String name) {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(RedstoneArsenal.modId + ":" + name, "inventory"));
	}

	public ItemToolRF setEnergyParams(int maxEnergy, int maxTransfer, int energyPerUse, int energyPerUseCharged) {
		this.maxEnergy = maxEnergy;
		this.maxTransfer = maxTransfer;
		this.energyPerUse = energyPerUse;
		this.energyPerUseCharged = energyPerUseCharged;

		return this;
	}

	@Override
	protected float getEfficiency(ItemStack stack) {
		if (isEmpowered(stack) && getEnergyStored(stack) >= energyPerUseCharged) {
			return efficiencyOnProperMaterial * 1.5F;
		}
		return efficiencyOnProperMaterial;
	}

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        if (getEnergyStored(stack) < energyPerUse) {
            return 1.0F;
        }
        return super.getStrVsBlock(stack, state);
    }

    protected int useEnergy(ItemStack stack, boolean simulate) {
		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 4);
		return extractEnergy(stack, isEmpowered(stack) ? energyPerUseCharged * (5 - unbreakingLevel) / 5 : energyPerUse * (5 - unbreakingLevel) / 5, simulate);
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
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item), 0));
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item), maxEnergy));
	}

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {

        Multimap<String, AttributeModifier> multimap = HashMultimap.create();

        if (slot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)this.attackSpeed, 0));

            if (extractEnergy(stack, energyPerUse, true) == energyPerUse) {
                int fluxDamage = isEmpowered(stack) ? 2 : 1;
                multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", fluxDamage + damage, 0));
            }
            else {
                multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 1, 0));
            }
        }

        return multimap;
    }

    @Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase player) {

        if (stack.getItemDamage() > 0) {
            stack.setItemDamage(0);
        }

        EntityPlayer thePlayer = (EntityPlayer) player;

        if (thePlayer.capabilities.isCreativeMode || extractEnergy(stack, energyPerUse, false) == energyPerUse) {
            int fluxDamage = isEmpowered(stack) ? 2 : 1;

            float potionDamage = 1.0f;
            if (player.isPotionActive(MobEffects.STRENGTH)) {
                potionDamage += player.getActivePotionEffect(MobEffects.STRENGTH).getAmplifier() * 1.3f;
            }

            entity.attackEntityFrom(DamageHelper.causePlayerFluxDamage(thePlayer), fluxDamage * potionDamage);
        }

        return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> list, boolean advanced) {
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
		if (getEnergyStored(stack) >= getEnergyPerUse(stack)) {
            int adjustedDamage = (int) (damage + playerIn.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue());
			list.add("");
			list.add(StringHelper.LIGHT_BLUE + "+" + adjustedDamage + " " + StringHelper.localize("info.cofh.damageAttack") + StringHelper.END);
			list.add(StringHelper.BRIGHT_GREEN + "+" + (isEmpowered(stack) ? 2 : 1) + " " + StringHelper.localize("info.cofh.damageFlux") + StringHelper.END);
		}
	}

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged)
                && (slotChanged || !ItemHelper.areItemStacksEqualIgnoreTags(oldStack, newStack, "Energy"));
    }

    @Override
	public double getDurabilityForDisplay(ItemStack stack) {
		if (stack.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		return 1D - (double) stack.getTagCompound().getInteger("Energy") / (double) maxEnergy;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {

		return !RAProps.showToolCharge ? false : stack.getTagCompound() == null || !stack.getTagCompound().getBoolean("CreativeTab");
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

	@Override
	public int getMaxDamage(ItemStack stack) {
		return 0;
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
