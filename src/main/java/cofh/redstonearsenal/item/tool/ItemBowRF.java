package cofh.redstonearsenal.item.tool;

import java.util.List;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.item.IEmpowerableItem;
import cofh.core.enchantment.CoFHEnchantment;
import cofh.core.item.IEqualityOverrideItem;
import cofh.core.item.tool.ItemBowAdv;
import cofh.lib.util.helpers.*;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.core.RAProps;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.*;

import javax.annotation.Nullable;

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
		addPropertyOverride(new ResourceLocation(name + "_empowered"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return ItemBowRF.this.getEnergyStored(stack) > 0 && ItemBowRF.this.isEmpowered(stack) && !ItemBowRF.this.isPulling(stack, entity) ? 1F : 0F;
            }
        });
		addPropertyOverride(new ResourceLocation(name + "_empowered_0"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return ItemBowRF.this.getEnergyStored(stack) > 0 && ItemBowRF.this.isEmpowered(stack) && ItemBowRF.this.isPulling(stack, entity) && ItemBowRF.this.getPullStage(stack, entity) == 0 ? 1F : 0F;
            }
        });
		addPropertyOverride(new ResourceLocation(name + "_empowered_1"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return ItemBowRF.this.getEnergyStored(stack) > 0 && ItemBowRF.this.isEmpowered(stack) && ItemBowRF.this.isPulling(stack, entity) && ItemBowRF.this.getPullStage(stack, entity) == 1 ? 1F : 0F;
            }
        });
		addPropertyOverride(new ResourceLocation(name + "_empowered_2"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return ItemBowRF.this.getEnergyStored(stack) > 0 && ItemBowRF.this.isEmpowered(stack) && ItemBowRF.this.isPulling(stack, entity) && ItemBowRF.this.getPullStage(stack, entity) == 2 ? 1F : 0F;
            }
        });
		addPropertyOverride(new ResourceLocation(name + "_active"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return ItemBowRF.this.getEnergyStored(stack) > 0 && !ItemBowRF.this.isEmpowered(stack) && !ItemBowRF.this.isPulling(stack, entity) ? 1F : 0F;
            }
        });
		addPropertyOverride(new ResourceLocation(name + "_active_0"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return ItemBowRF.this.getEnergyStored(stack) > 0 && !ItemBowRF.this.isEmpowered(stack) && ItemBowRF.this.isPulling(stack, entity) && ItemBowRF.this.getPullStage(stack, entity) == 0 ? 1F : 0F;
            }
        });
		addPropertyOverride(new ResourceLocation(name + "_active_1"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return ItemBowRF.this.getEnergyStored(stack) > 0 && !ItemBowRF.this.isEmpowered(stack) && ItemBowRF.this.isPulling(stack, entity) && ItemBowRF.this.getPullStage(stack, entity) == 1 ? 1F : 0F;
            }
        });
		addPropertyOverride(new ResourceLocation(name + "_active_2"), new IItemPropertyGetter() {
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
	public void initModel() {
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
		return entityIn.isHandActive() && entityIn.getActiveItemStack() == stack;
	}

	protected void useEnergy(ItemStack stack) {
		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 4);
		extractEnergy(stack, isEmpowered(stack) ? energyPerUseCharged * (5 - unbreakingLevel) / 5 : energyPerUse * (5 - unbreakingLevel) / 5, false);
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
		ArrowNockEvent event = new ArrowNockEvent(player, stack, hand, world, true);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return event.getResult() == Result.ALLOW ? new ActionResult(EnumActionResult.SUCCESS, hand) : new ActionResult(EnumActionResult.FAIL, stack);
		}
		if (player.capabilities.isCreativeMode || player.inventory.hasItemStack(new ItemStack(Items.ARROW)) || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0) {
			player.setHeldItem(hand, stack);
		}
		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int itemUse) {
		if (!(entityLiving instanceof EntityPlayer)) {
			return;
		}
		EntityPlayer player = (EntityPlayer) entityLiving;
		int draw = getMaxItemUseDuration(stack) - itemUse;

		ArrowLooseEvent event = new ArrowLooseEvent(player, stack, world, draw, true);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return;
		}
		draw = event.getCharge();

		boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;

		if (flag || player.inventory.hasItemStack(new ItemStack(Items.ARROW))) {
			boolean empowered = isEmpowered(stack);

			float drawStrength = draw / 20.0F;
			drawStrength = (drawStrength * drawStrength + drawStrength * 2.0F) / 3.0F;

			if (drawStrength > 1.0F) {
				drawStrength = 1.0F;
			}
			else if (drawStrength < 0.1F) {
				return;
			}
			int enchantPower = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
			int enchantKnockback = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
			int enchantFire = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack);
			int enchantMultishot = EnchantmentHelper.getEnchantmentLevel(CoFHEnchantment.multishot, stack);

			EntityArrow arrow = new ItemArrow().createArrow(world, stack, player);
			float f = ItemBow.getArrowVelocity(draw);
			arrow.setAim(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);
			double damage = arrow.getDamage() * arrowDamageMultiplier * (empowered ? 1.5F : 1.2F);
			arrow.setDamage(damage);

			if (drawStrength == 1.0F) {
				arrow.setIsCritical(true);
			}
			if (enchantPower > 0) {
				arrow.setDamage(damage + enchantPower * 0.5D + 0.5D);
			}
			if (enchantKnockback > 0) {
				arrow.setKnockbackStrength(enchantKnockback);
			}
			if (enchantFire > 0) {
				arrow.setFire(100);
			}
			if (flag) {
				arrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
			}
			else {
				int slot = player.inventory.getSlotFor(new ItemStack(Items.ARROW));
				player.inventory.decrStackSize(slot, 1);
			}
			world.playSound((EntityPlayer) null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.1F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + drawStrength * 0.5F);

			if (ServerHelper.isServerWorld(world)) {
				world.spawnEntityInWorld(arrow);
			}
			for (int i = 0; i < enchantMultishot; i++) {
				arrow = new ItemArrow().createArrow(world, stack, player);
				// arrow.setThrowableHeading(arrow.motionX, arrow.motionY,
				// arrow.motionZ, 1.5f * drawStrength * arrowSpeedMultiplier *
				// 10f, 3.0F);
				// arrow.setThrowableHeading(player.motionX, arrow.motionY *
				// 0.20000000298023224D, player.motionZ, 1.6F, 3.0F);
				arrow.setAim(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);

				arrow.setDamage(damage);

				if (drawStrength == 1.0F) {
					arrow.setIsCritical(true);
				}
				if (enchantPower > 0) {
					arrow.setDamage(damage + enchantPower * 0.5D + 0.5D);
				}
				if (enchantKnockback > 0) {
					arrow.setKnockbackStrength(enchantKnockback);
				}
				if (enchantFire > 0) {
					arrow.setFire(100);
				}
				arrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
				world.playSound((EntityPlayer) null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.1F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + drawStrength * 0.5F);

				if (ServerHelper.isServerWorld(world)) {
					world.spawnEntityInWorld(arrow);
				}
			}
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack);
			}
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
			player.worldObj.playSound((EntityPlayer) null, player.getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 2F));
		}
		else {
			player.worldObj.playSound((EntityPlayer) null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 2F));
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
