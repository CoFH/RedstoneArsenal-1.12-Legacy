package cofh.redstonearsenal.item.tool;

import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.asm.relauncher.Implementable;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.eventhandler.Event.Result;

import ic2.api.tile.IWrenchable;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

@Implementable({ "buildcraft.api.tools.IToolWrench", "mods.railcraft.api.core.items.IToolCrowbar" })
public class ItemWrenchBattleRF extends ItemSwordRF implements IToolHammer {

	public ItemWrenchBattleRF(Item.ToolMaterial toolMaterial) {

		super(toolMaterial);
		damage = 6;
		damageCharged = 3;
		setHarvestLevel("wrench", 1);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase player) {

		entity.rotationYaw += 90;
		entity.rotationYaw %= 360;
		return super.hitEntity(stack, entity, player);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		return ServerHelper.isClientWorld(world);
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

		if (block == null) {
			return false;
		}
		PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, x, y, z, hitSide, world);
		if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Result.DENY || event.useBlock == Result.DENY || event.useItem == Result.DENY) {
			return false;
		}
		if (ServerHelper.isServerWorld(world) && player.isSneaking() && block instanceof IDismantleable
				&& ((IDismantleable) block).canDismantle(player, world, x, y, z)) {
			((IDismantleable) block).dismantleBlock(player, world, x, y, z, false);

			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return true;
		}
		if (BlockHelper.canRotate(block)) {
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
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isCurrentItem) {

		if (stack.getItemDamage() > 0) {
			stack.setItemDamage(0);
		}
		super.onUpdate(stack, world, entity, slot, isCurrentItem);
	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {

		return true;
	}

	/* IToolCrowbar */
	public boolean canWhack(EntityPlayer player, ItemStack crowbar, int x, int y, int z) {

		return getEnergyStored(crowbar) >= getEnergyPerUse(crowbar) || player.capabilities.isCreativeMode;
	}

	public void onWhack(EntityPlayer player, ItemStack crowbar, int x, int y, int z) {

		if (!player.capabilities.isCreativeMode) {
			useEnergy(crowbar, false);
		}
		player.swingItem();
	}

	public boolean canLink(EntityPlayer player, ItemStack crowbar, EntityMinecart cart) {

		return player.isSneaking() && getEnergyStored(crowbar) >= getEnergyPerUse(crowbar) || player.capabilities.isCreativeMode;
	}

	public void onLink(EntityPlayer player, ItemStack crowbar, EntityMinecart cart) {

		if (!player.capabilities.isCreativeMode) {
			useEnergy(crowbar, false);
		}
		player.swingItem();
	}

	public boolean canBoost(EntityPlayer player, ItemStack crowbar, EntityMinecart cart) {

		return !player.isSneaking() && getEnergyStored(crowbar) >= getEnergyPerUse(crowbar);
	}

	public void onBoost(EntityPlayer player, ItemStack crowbar, EntityMinecart cart) {

		if (!player.capabilities.isCreativeMode) {
			useEnergy(crowbar, false);
		}
		player.swingItem();
	}

	/* IToolHammer */
	@Override
	public boolean isUsable(ItemStack item, EntityLivingBase user, int x, int y, int z) {

		if (user instanceof EntityPlayer) {
			if (((EntityPlayer) user).capabilities.isCreativeMode) {
				return true;
			}
		}
		return getEnergyStored(item) >= getEnergyPerUse(item);
	}

	@Override
	public void toolUsed(ItemStack item, EntityLivingBase user, int x, int y, int z) {

		if (user instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) user;

			if (!player.capabilities.isCreativeMode) {
				useEnergy(player.getCurrentEquippedItem(), false);
			}
		}
	}

	/* IToolWrench */
	public boolean canWrench(EntityPlayer player, int x, int y, int z) {

		ItemStack stack = player.getCurrentEquippedItem();
		return getEnergyStored(stack) >= getEnergyPerUse(stack) || player.capabilities.isCreativeMode;
	}

	public void wrenchUsed(EntityPlayer player, int x, int y, int z) {

		if (!player.capabilities.isCreativeMode) {
			useEnergy(player.getCurrentEquippedItem(), false);
		}
	}

}
