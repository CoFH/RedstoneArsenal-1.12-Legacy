package cofh.redstonearsenal.item.tool;

import java.util.List;

import com.google.common.collect.*;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.item.IEmpowerableItem;
import cofh.core.item.IEqualityOverrideItem;
import cofh.lib.util.helpers.*;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.core.RAProps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.*;

import javax.annotation.Nullable;

public class ItemSwordRF extends ItemSword implements IEmpowerableItem, IEnergyContainerItem, IEqualityOverrideItem {

	// IIcon activeIcon;
	// IIcon drainedIcon;

	public int maxEnergy = 160000;
	public int maxTransfer = 1600;
	public int energyPerUse = 200;
	public int energyPerUseCharged = 800;

	public int damage = 8;
	public int damageCharged = 4;

	private static String name;

	public ItemSwordRF(Item.ToolMaterial toolMaterial, String nameIn) {

		super(toolMaterial);
		name = nameIn;
		setNoRepair();
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setCreativeTab(RedstoneArsenal.tab);
		addPropertyOverride(new ResourceLocation("flux_sword_empowered"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return (ItemSwordRF.this.getEnergyStored(stack) > 0 && ItemSwordRF.this.isEmpowered(stack)) || ItemSwordRF.this.isCreativeTab(stack) ? 1F : 0F;
            }
        });
		addPropertyOverride(new ResourceLocation("flux_sword_active"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return ItemSwordRF.this.getEnergyStored(stack) > 0 && !ItemSwordRF.this.isEmpowered(stack) ? 1F : 0F;
            }
        });
	}

	public ItemSwordRF setEnergyParams(int maxEnergy, int maxTransfer, int energyPerUse, int energyPerUseCharged) {

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

	protected int useEnergy(ItemStack stack, boolean simulate) {

		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 4);
		return extractEnergy(stack, isEmpowered(stack) ? energyPerUseCharged * (5 - unbreakingLevel) / 5 : energyPerUse * (5 - unbreakingLevel) / 5, simulate);
	}

	protected int getEnergyPerUse(ItemStack stack) {

		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 4);
		return (isEmpowered(stack) ? energyPerUseCharged : energyPerUse) * (5 - unbreakingLevel) / 5;
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
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {

		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), 0));
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), maxEnergy));
	}

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {

        Multimap<String, AttributeModifier> multimap = HashMultimap.create();

        if (slot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.2D, 0));

            if (useEnergy(stack, true) == getEnergyPerUse(stack)) {
                int fluxDamage = isEmpowered(stack) ? damageCharged : 1;
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

        if (thePlayer.capabilities.isCreativeMode || useEnergy(stack, false) == getEnergyPerUse(stack)) {
            int fluxDamage = isEmpowered(stack) ? damageCharged : 1;

            float potionDamage = 1.0f;
            if (player.isPotionActive(MobEffects.STRENGTH)) {
                potionDamage += player.getActivePotionEffect(MobEffects.STRENGTH).getAmplifier() * 1.3f;
            }

            entity.attackEntityFrom(DamageHelper.causePlayerFluxDamage(thePlayer), fluxDamage * potionDamage);
        }

        return true;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLivingy) {

		if (state.getBlockHardness(world, pos) != 0.0D) {
			extractEnergy(stack, energyPerUse, false);
		}
		return true;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isCurrentItem) {
		if (stack.getItemDamage() > 0) {
			stack.setItemDamage(0);
		}
	}

	/*
	 * Commented out for now since sword blocking isn't a thing in 1.10, I would
	 * recommend a check here for if a shield is in the offhand and actively
	 * blocking
	 *
	 * @Override public void onUpdate(ItemStack stack, World world, Entity
	 * entity, int slot, boolean isCurrentItem) {
	 *
	 * if (!isEmpowered(stack) || !isCurrentItem) { return; } if (entity
	 * instanceof EntityPlayer) { if (((EntityPlayer) entity).isBlocking()) {
	 *
	 * AxisAlignedBB axisalignedbb = entity.boundingBox.expand(2.0D, 1.0D,
	 * 2.0D); List<EntityMob> list =
	 * entity.worldObj.getEntitiesWithinAABB(EntityMob.class, axisalignedbb);
	 *
	 * for (Entity mob : list) { pushEntityAway(mob, entity); } } } }
	 */
	protected void pushEntityAway(Entity entity, Entity player) {

		double d0 = player.posX - entity.posX;
		double d1 = player.posZ - entity.posZ;
		double d2 = MathHelper.maxAbs(d0, d1);

		if (d2 >= 0.01D) {
			d2 = Math.sqrt(d2);
			d0 /= d2;
			d1 /= d2;
			double d3 = 1.0D / d2;

			if (d3 > 1.0D) {
				d3 = 1.0D;
			}
			d0 *= d3;
			d1 *= d3;
			d0 *= 0.2D;
			d1 *= 0.2D;
			d0 *= 1.0F - entity.entityCollisionReduction;
			d1 *= 1.0F - entity.entityCollisionReduction;
			entity.addVelocity(-d0, 0.0D, -d1);
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
		if (getEnergyStored(stack) >= getEnergyPerUse(stack)) {
            int adjustedDamage = (int) (damage + player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue());
			list.add("");
			list.add(StringHelper.LIGHT_BLUE + "+" + adjustedDamage + " " + StringHelper.localize("info.cofh.damageAttack") + StringHelper.END);
			list.add(StringHelper.BRIGHT_GREEN + "+" + (isEmpowered(stack) ? damageCharged : 1) + " " + StringHelper.localize("info.cofh.damageFlux") + StringHelper.END);
		}
	}

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged)
                && !(!slotChanged && oldStack.isItemEqual(newStack) && getEnergyStored(oldStack) < getEnergyStored(newStack));
    }

    @Override
	public void setDamage(ItemStack stack, int damage) {

		super.setDamage(stack, 0);
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
			player.worldObj.playSound(null, player.getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 2F));
		}
		else {
			player.worldObj.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 2F));
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

	public boolean isCreativeTab(ItemStack stack) {
		return stack.getTagCompound() == null ? false : stack.getTagCompound().getBoolean("CreativeTab");
	}

}
