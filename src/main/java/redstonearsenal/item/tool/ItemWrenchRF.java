package redstonearsenal.item.tool;

import buildcraft.api.tools.IToolWrench;

import cofh.api.block.IDismantleable;
import cofh.api.energy.IEnergyContainerItem;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ic2.api.tile.IWrenchable;

import java.util.ArrayList;
import java.util.List;

import mods.railcraft.api.core.items.IToolCrowbar;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemWrenchRF extends ItemShears implements IToolCrowbar, IToolWrench, IEnergyContainerItem {

	protected Item.ToolMaterial toolMaterial;

	IIcon drainedIcon;

	public int maxEnergy = 160000;
	public int maxTransfer = 1600;
	public int energyPerUse = 200;

	public ItemWrenchRF(Item.ToolMaterial toolMaterial) {

		super();
		this.toolMaterial = toolMaterial;
		setMaxDamage(toolMaterial.getMaxUses());
		setNoRepair();
	}

	protected int useEnergy(ItemStack stack, boolean simulate) {

		int unbreakingLevel = MathHelper.clampI(EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack), 0, 4);
		return extractEnergy(stack, energyPerUse * (5 - unbreakingLevel) / 5, simulate);
	}

	protected int getEnergyPerUse(ItemStack stack) {

		int unbreakingLevel = MathHelper.clampI(EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack), 0, 4);
		return energyPerUse * (5 - unbreakingLevel) / 5;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return false;
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
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase player) {

		entity.rotationYaw += 90;
		entity.rotationYaw %= 360;
		return true;
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		return true;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		if (stack.getItemDamage() > 0) {
			stack.setItemDamage(0);
		}
		if (!player.capabilities.isCreativeMode && getEnergyStored(stack) < getEnergyPerUse(stack)) {
			return false;
		}
		Block block = world.getBlock(x, y, z);

		if (ServerHelper.isServerWorld(world) && player.isSneaking() && block instanceof IDismantleable
				&& ((IDismantleable) block).canDismantle(player, world, x, y, z)) {
			((IDismantleable) block).dismantleBlock(player, world, x, y, z, false);

			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return true;
		}
		if (BlockHelper.canRotate(block)) {
			int bMeta = world.getBlockMetadata(x, y, z);

			if (player.isSneaking()) {
				world.setBlockMetadataWithNotify(x, y, z, BlockHelper.rotateVanillaBlockAlt(world, block, x, y, z), 3);
				world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getBreakSound(), 1.0F, 0.6F);
			} else {
				world.setBlockMetadataWithNotify(x, y, z, BlockHelper.rotateVanillaBlock(world, block, x, y, z), 3);
				world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getBreakSound(), 1.0F, 0.8F);
			}
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return ServerHelper.isServerWorld(world);
		} else if (!player.isSneaking() && block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(hitSide))) {
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			player.swingItem();
			return ServerHelper.isServerWorld(world);
		}
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile instanceof IWrenchable) {
			IWrenchable wrenchable = (IWrenchable) tile;

			if (player.isSneaking()) {
				hitSide = BlockHelper.SIDE_OPPOSITE[hitSide];
			}
			if (wrenchable.wrenchCanSetFacing(player, hitSide)) {
				if (ServerHelper.isServerWorld(world)) {
					wrenchable.setFacing((short) hitSide);
				}
			} else if (wrenchable.wrenchCanRemove(player)) {
				ItemStack dropBlock = wrenchable.getWrenchDrop(player);

				if (dropBlock != null) {
					world.setBlockToAir(x, y, z);
					if (ServerHelper.isServerWorld(world)) {
						List<ItemStack> drops = block.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);

						if (drops.isEmpty()) {
							drops.add(dropBlock);
						} else {
							drops.set(0, dropBlock);
						}
						for (ItemStack drop : drops) {
							float f = 0.7F;
							double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
							double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
							double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
							EntityItem entity = new EntityItem(world, x + x2, y + y2, z + z2, drop);
							entity.delayBeforeCanPickup = 10;
							world.spawnEntityInWorld(entity);
						}
					}
				}
			}
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return ServerHelper.isServerWorld(world);
		}
		return false;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {

		if (ServerHelper.isClientWorld(entity.worldObj)) {
			entity.rotationYaw += 90;
			entity.rotationYaw %= 360;
			return false;
		}
		if (entity instanceof IShearable) {
			IShearable target = (IShearable) entity;
			if (target.isShearable(stack, entity.worldObj, (int) entity.posX, (int) entity.posY, (int) entity.posZ)) {
				ArrayList<ItemStack> drops = target.onSheared(stack, entity.worldObj, (int) entity.posX, (int) entity.posY, (int) entity.posZ,
						EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));

				for (ItemStack drop : drops) {
					EntityItem ent = entity.entityDropItem(drop, 1.0F);
					ent.motionY += MathHelper.RANDOM.nextFloat() * 0.05F;
					ent.motionX += (MathHelper.RANDOM.nextFloat() - MathHelper.RANDOM.nextFloat()) * 0.1F;
					ent.motionZ += (MathHelper.RANDOM.nextFloat() - MathHelper.RANDOM.nextFloat()) * 0.1F;
				}
				if (!player.capabilities.isCreativeMode) {
					useEnergy(stack, false);
				}
				entity.rotationYaw += 90;
				entity.rotationYaw %= 360;
			}
			return true;
		}
		entity.rotationYaw += 90;
		entity.rotationYaw %= 360;
		return false;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {

		if (ServerHelper.isClientWorld(player.worldObj)) {
			return false;
		}
		Block block = player.worldObj.getBlock(x, y, z);
		if (block instanceof IShearable) {
			IShearable target = (IShearable) block;
			if (target.isShearable(stack, player.worldObj, x, y, z)) {
				ArrayList<ItemStack> drops = target.onSheared(stack, player.worldObj, x, y, z,
						EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));

				for (ItemStack drop : drops) {
					float f = 0.7F;
					double d = MathHelper.RANDOM.nextFloat() * f + (1.0F - f) * 0.5D;
					double d1 = MathHelper.RANDOM.nextFloat() * f + (1.0F - f) * 0.5D;
					double d2 = MathHelper.RANDOM.nextFloat() * f + (1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.worldObj, x + d, y + d1, z + d2, drop);
					entityitem.delayBeforeCanPickup = 10;
					player.worldObj.spawnEntityInWorld(entityitem);
				}
				if (!player.capabilities.isCreativeMode) {
					useEnergy(stack, false);
				}
				player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
			}
		}
		return false;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isCurrentItem) {

		if (stack.getItemDamage() > 0) {
			stack.setItemDamage(0);
		}
	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {

		return true;
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
		list.add(StringHelper.ORANGE + getEnergyPerUse(stack) + " " + StringHelper.localize("info.redstonearsenal.tool.energyPerUse") + StringHelper.END);
		list.add(StringHelper.getFlavorText("info.redstonearsenal.tool.wrench"));
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
	public Multimap getAttributeModifiers(ItemStack stack) {

		Multimap multimap = HashMultimap.create();
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Tool modifier", 1, 0));
		return multimap;
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {

		return getIcon(stack, 0);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {

		return getEnergyStored(stack) <= 0 ? this.drainedIcon : this.itemIcon;
	}

	@Override
	public void registerIcons(IIconRegister ir) {

		this.itemIcon = ir.registerIcon(this.getIconString());
		this.drainedIcon = ir.registerIcon(this.getIconString() + "_Drained");
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

	/* IToolCrowbar */
	@Override
	public boolean canWhack(EntityPlayer player, ItemStack crowbar, int x, int y, int z) {

		return getEnergyStored(crowbar) >= getEnergyPerUse(crowbar) || player.capabilities.isCreativeMode;
	}

	@Override
	public void onWhack(EntityPlayer player, ItemStack crowbar, int x, int y, int z) {

		if (!player.capabilities.isCreativeMode) {
			useEnergy(crowbar, false);
		}
		player.swingItem();
	}

	@Override
	public boolean canLink(EntityPlayer player, ItemStack crowbar, EntityMinecart cart) {

		return player.isSneaking() && getEnergyStored(crowbar) >= getEnergyPerUse(crowbar) || player.capabilities.isCreativeMode;
	}

	@Override
	public void onLink(EntityPlayer player, ItemStack crowbar, EntityMinecart cart) {

		if (!player.capabilities.isCreativeMode) {
			useEnergy(crowbar, false);
		}
		player.swingItem();
	}

	@Override
	public boolean canBoost(EntityPlayer player, ItemStack crowbar, EntityMinecart cart) {

		return !player.isSneaking() && getEnergyStored(crowbar) >= getEnergyPerUse(crowbar);
	}

	@Override
	public void onBoost(EntityPlayer player, ItemStack crowbar, EntityMinecart cart) {

		if (!player.capabilities.isCreativeMode) {
			useEnergy(crowbar, false);
		}
		player.swingItem();
	}

	/* IToolWrench */
	@Override
	public boolean canWrench(EntityPlayer player, int x, int y, int z) {

		ItemStack stack = player.getCurrentEquippedItem();
		return getEnergyStored(stack) >= getEnergyPerUse(stack) || player.capabilities.isCreativeMode;
	}

	@Override
	public void wrenchUsed(EntityPlayer player, int x, int y, int z) {

		if (!player.capabilities.isCreativeMode) {
			useEnergy(player.getCurrentEquippedItem(), false);
		}
	}

}
