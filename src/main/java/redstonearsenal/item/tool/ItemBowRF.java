package redstonearsenal.item.tool;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import org.lwjgl.input.Keyboard;

import redstonearsenal.util.KeyBindingEmpower;
import cofh.api.energy.IEnergyContainerItem;
import cofh.util.EnergyHelper;
import cofh.util.MathHelper;
import cofh.util.StringHelper;

public class ItemBowRF extends ItemBow implements IEmpowerableItem, IEnergyContainerItem {

	protected Item.ToolMaterial toolMaterial;

	IIcon normalIcons[] = new IIcon[4];
	IIcon activeIcons[] = new IIcon[4];
	IIcon drainedIcon;

	public int maxEnergy = 160000;
	public int maxTransfer = 1600;
	public int energyPerUse = 200;
	public int energyPerUseCharged = 800;

	public ItemBowRF(Item.ToolMaterial toolMaterial) {

		super();
		this.toolMaterial = toolMaterial;
		setMaxDamage(toolMaterial.getMaxUses());
		setNoRepair();
	}

	protected void useEnergy(ItemStack stack) {

		int unbreakingLevel = MathHelper.clampI(EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack), 0, 4);
		extractEnergy(stack, isEmpowered(stack) ? energyPerUseCharged * (5 - unbreakingLevel) / 5 : energyPerUse * (5 - unbreakingLevel) / 5, false);
	}

	protected int getEnergyPerUse(ItemStack stack) {

		int unbreakingLevel = MathHelper.clampI(EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack), 0, 4);
		return (isEmpowered(stack) ? energyPerUseCharged : energyPerUse) * (5 - unbreakingLevel) / 5;
	}

	@Override
	public int getItemEnchantability() {

		return toolMaterial.getEnchantability();
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		return EnumRarity.uncommon;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), 0));
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, 0), maxEnergy));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		if (!player.capabilities.isCreativeMode && getEnergyStored(stack) < getEnergyPerUse(stack)) {
			return stack;
		}
		ArrowNockEvent event = new ArrowNockEvent(player, stack);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return event.result;
		}
		if (player.capabilities.isCreativeMode || player.inventory.hasItem(Items.arrow)) {
			player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		}
		return stack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int itemUse) {

		int draw = this.getMaxItemUseDuration(stack) - itemUse;

		ArrowLooseEvent event = new ArrowLooseEvent(player, stack, draw);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return;
		}
		draw = event.charge;

		boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

		if (flag || player.inventory.hasItem(Items.arrow)) {
			float f = draw / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if (f < 0.1D) {
				return;
			}
			if (f > 1.0F) {
				f = 1.0F;
			}
			EntityArrow entityarrow = new EntityArrow(world, player, f * 2.0F);

			if (f == 1.0F) {
				entityarrow.setIsCritical(true);
			}
			int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

			if (k > 0) {
				entityarrow.setDamage(entityarrow.getDamage() + k * 0.5D + 0.5D);
			}
			int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

			if (l > 0) {
				entityarrow.setKnockbackStrength(l);
			}
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) {
				entityarrow.setFire(100);
			}
			stack.damageItem(1, player);
			world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

			if (flag) {
				entityarrow.canBePickedUp = 2;
			} else {
				player.inventory.consumeInventoryItem(Items.arrow);
			}
			if (!world.isRemote) {
				world.spawnEntityInWorld(entityarrow);
			}
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForInfo);
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (stack.stackTagCompound == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		list.add(StringHelper.localize("info.cofh.charge") + ": " + stack.stackTagCompound.getInteger("Energy") + " / " + maxEnergy + " RF");

		list.add(StringHelper.ORANGE + getEnergyPerUse(stack) + " " + StringHelper.localize("info.redstonearsenal.tool.energyPerUse") + StringHelper.END);
		if (isEmpowered(stack)) {
			list.add(StringHelper.YELLOW + StringHelper.ITALIC + StringHelper.localize("info.cofh.press") + " "
					+ Keyboard.getKeyName(KeyBindingEmpower.instance.getKey()) + " " + StringHelper.localize("info.redstonearsenal.tool.chargeOff")
					+ StringHelper.END);
		} else {
			list.add(StringHelper.BRIGHT_BLUE + StringHelper.ITALIC + StringHelper.localize("info.cofh.press") + " "
					+ Keyboard.getKeyName(KeyBindingEmpower.instance.getKey()) + " " + StringHelper.localize("info.redstonearsenal.tool.chargeOn")
					+ StringHelper.END);
		}
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

	@Override
	public IIcon getIconIndex(ItemStack stack) {

		return getIcon(stack, 0);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {

		return isEmpowered(stack) ? this.activeIcons[0] : getEnergyStored(stack) <= 0 ? this.drainedIcon : this.normalIcons[0];
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {

		if (getEnergyStored(stack) <= 0) {
			return this.drainedIcon;
		}
		if (useRemaining > 0) {
			int draw = stack.getMaxItemUseDuration() - useRemaining;

			if (draw > 17) {
				return isEmpowered(stack) ? this.activeIcons[3] : this.normalIcons[3];
			} else if (draw > 13) {
				return isEmpowered(stack) ? this.activeIcons[2] : this.normalIcons[2];
			} else if (draw > 0) {
				return isEmpowered(stack) ? this.activeIcons[1] : this.normalIcons[1];
			}
		}
		return isEmpowered(stack) ? this.activeIcons[0] : this.normalIcons[0];
	}

	@Override
	public void registerIcons(IIconRegister ir) {

		this.drainedIcon = ir.registerIcon(this.getIconString() + "_Drained");
		this.normalIcons[0] = ir.registerIcon(this.getIconString());
		this.activeIcons[0] = ir.registerIcon(this.getIconString() + "_Active");

		for (int i = 1; i < 4; i++) {
			this.normalIcons[i] = ir.registerIcon(this.getIconString() + "_" + (i - 1));
			this.activeIcons[i] = ir.registerIcon(this.getIconString() + "_" + (i - 1) + "_Active");
		}
	}

	/* IEmpowerableItem */
	@Override
	public boolean isEmpowered(ItemStack stack) {

		return stack.stackTagCompound.getBoolean("Empowered");
	}

	@Override
	public boolean setEmpoweredState(ItemStack stack, boolean state) {

		if (getEnergyStored(stack) > 0) {
			stack.stackTagCompound.setBoolean("Empowered", state);
			return true;
		}
		stack.stackTagCompound.setBoolean("Empowered", false);
		return false;
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

			if (stored == 0) {
				setEmpoweredState(container, false);
			}
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
