package cofh.redstonearsenal.item.tool;

import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.tool.ItemShearsCore;
import cofh.core.util.helpers.*;
import cofh.redstonearsenal.init.RAProps;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.util.EnergyContainerItemWrapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import javax.annotation.Nullable;
import java.util.List;

//TODO FIXME @Optional
//@Implementable ({ "buildcraft.api.tools.IToolWrench", "mods.railcraft.api.core.items.IToolCrowbar" })
public class ItemWrenchFlux extends ItemShearsCore implements IEnergyContainerItem, IEnchantableItem, IToolHammer {

	protected int maxEnergy = 320000;
	protected int maxTransfer = 4000;

	protected int energyPerUse = 200;

	protected boolean showInCreative = true;

	public ItemWrenchFlux(ToolMaterial toolMaterial) {

		super(toolMaterial);
		setMaxDamage(toolMaterial.getMaxUses());
		setNoRepair();

		setHarvestLevel("wrench", 1);

		addPropertyOverride(new ResourceLocation("active"), (stack, world, entity) -> ItemWrenchFlux.this.getEnergyStored(stack) > 0 ? 1F : 0F);
	}

	public ItemWrenchFlux setEnergyParams(int maxEnergy, int maxTransfer, int energyPerUse) {

		this.maxEnergy = maxEnergy;
		this.maxTransfer = maxTransfer;
		this.energyPerUse = energyPerUse;

		return this;
	}

	public ItemWrenchFlux setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	protected int getEnergyPerUse(ItemStack stack) {

		return energyPerUse;
	}

	protected int useEnergy(ItemStack stack, boolean simulate) {

		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 10);
		if (MathHelper.RANDOM.nextInt(2 + unbreakingLevel) >= 2) {
			return 0;
		}
		return extractEnergy(stack, energyPerUse, simulate);
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
		tooltip.add(StringHelper.getNoticeText("info.redstonearsenal.tool.wrench"));
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
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {

		return true;
	}

	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {

		return false;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase player) {

		entity.rotationYaw += 90;
		entity.rotationYaw %= 360;
		return true;
	}

	@Override
	public boolean isDamageable() {

		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return true;
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {

		if (ServerHelper.isClientWorld(entity.world)) {
			entity.rotationYaw += 90;
			entity.rotationYaw %= 360;
			return false;
		}
		if (entity instanceof IShearable) {
			IShearable target = (IShearable) entity;
			BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
			if (target.isShearable(stack, entity.world, pos)) {
				List<ItemStack> drops = target.onSheared(stack, entity.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));

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
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		if (ServerHelper.isClientWorld(player.world)) {
			return false;
		}
		Block block = player.world.getBlockState(pos).getBlock();
		if (block instanceof IShearable) {
			IShearable target = (IShearable) block;
			if (target.isShearable(stack, player.world, pos)) {
				List<ItemStack> drops = target.onSheared(stack, player.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));

				for (ItemStack drop : drops) {
					float f = 0.7F;
					double d = MathHelper.RANDOM.nextFloat() * f + (1.0F - f) * 0.5D;
					double d1 = MathHelper.RANDOM.nextFloat() * f + (1.0F - f) * 0.5D;
					double d2 = MathHelper.RANDOM.nextFloat() * f + (1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.world, pos.getX() + d, pos.getY() + d1, pos.getZ() + d2, drop);
					entityitem.setPickupDelay(10);
					player.world.spawnEntity(entityitem);
				}
				if (!player.capabilities.isCreativeMode) {
					useEnergy(stack, false);
				}
				player.addStat(StatList.getBlockStats(block), 1);
			}
		}
		return false;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || getEnergyStored(oldStack) > 0 != getEnergyStored(newStack) > 0);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {

		return RAProps.showToolCharge && getEnergyStored(stack) > 0;
	}

	@Override
	public int getItemEnchantability(ItemStack stack) {

		return toolMaterial.getEnchantability();
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
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		return ServerHelper.isClientWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItemDamage() > 0) {
			stack.setItemDamage(0);
		}
		if (!player.capabilities.isCreativeMode && getEnergyStored(stack) < getEnergyPerUse(stack)) {
			return EnumActionResult.PASS;
		}
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (world.isAirBlock(pos)) {
			return EnumActionResult.PASS;
		}
		PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(player, hand, pos, side, new Vec3d(hitX, hitY, hitZ));
		if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Result.DENY || event.getUseItem() == Result.DENY || event.getUseBlock() == Result.DENY) {
			return EnumActionResult.PASS;
		}
		if (ServerHelper.isServerWorld(world) && player.isSneaking() && block instanceof IDismantleable && ((IDismantleable) block).canDismantle(world, pos, state, player)) {
			((IDismantleable) block).dismantleBlock(world, pos, state, player, false);
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return EnumActionResult.SUCCESS;
		}
		if (BlockHelper.canRotate(block)) {
			world.setBlockState(pos, BlockHelper.rotateVanillaBlock(world, state, pos), 3);
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			player.swingArm(hand);
			return ServerHelper.isServerWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
		} else if (!player.isSneaking() && block.rotateBlock(world, pos, side)) {
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			player.swingArm(hand);
			return ServerHelper.isServerWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
		}
		return EnumActionResult.PASS;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {

		Multimap<String, AttributeModifier> multimap = HashMultimap.create();
		if (slot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Tool modifier", 1, 0));
		}
		return multimap;
	}

	/* IToolHammer */
	@Override
	public boolean isUsable(ItemStack item, EntityLivingBase user, BlockPos pos) {

		if (user instanceof EntityPlayer) {
			if (((EntityPlayer) user).capabilities.isCreativeMode) {
				return true;
			}
		}
		return getEnergyStored(item) >= getEnergyPerUse(item);
	}

	@Override
	public boolean isUsable(ItemStack item, EntityLivingBase user, Entity entity) {

		if (user instanceof EntityPlayer) {
			if (((EntityPlayer) user).capabilities.isCreativeMode) {
				return true;
			}
		}
		return getEnergyStored(item) >= getEnergyPerUse(item);
	}

	@Override
	public void toolUsed(ItemStack item, EntityLivingBase user, BlockPos pos) {

		if (user instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) user;
			if (!player.capabilities.isCreativeMode) {
				useEnergy(player.getHeldItemMainhand(), false);
			}
		}
	}

	@Override
	public void toolUsed(ItemStack item, EntityLivingBase user, Entity entity) {

		if (user instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) user;
			if (!player.capabilities.isCreativeMode) {
				useEnergy(player.getHeldItemMainhand(), false);
			}
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

	/* IMPLEMENTABLES */

	/* IToolCrowbar */
	public boolean canWhack(EntityPlayer player, EnumHand hand, ItemStack crowbar, BlockPos pos) {

		return getEnergyStored(crowbar) >= getEnergyPerUse(crowbar) || player.capabilities.isCreativeMode;
	}

	public void onWhack(EntityPlayer player, EnumHand hand, ItemStack crowbar, BlockPos pos) {

		if (!player.capabilities.isCreativeMode) {
			useEnergy(crowbar, false);
		}
		player.swingArm(EnumHand.MAIN_HAND);
	}

	public boolean canLink(EntityPlayer player, EnumHand hand, ItemStack crowbar, EntityMinecart cart) {

		return player.isSneaking() && getEnergyStored(crowbar) >= getEnergyPerUse(crowbar) || player.capabilities.isCreativeMode;
	}

	public void onLink(EntityPlayer player, EnumHand hand, ItemStack crowbar, EntityMinecart cart) {

		if (!player.capabilities.isCreativeMode) {
			useEnergy(crowbar, false);
		}
		player.swingArm(EnumHand.MAIN_HAND);
	}

	public boolean canBoost(EntityPlayer player, EnumHand hand, ItemStack crowbar, EntityMinecart cart) {

		return !player.isSneaking() && getEnergyStored(crowbar) >= getEnergyPerUse(crowbar);
	}

	public void onBoost(EntityPlayer player, EnumHand hand, ItemStack crowbar, EntityMinecart cart) {

		if (!player.capabilities.isCreativeMode) {
			useEnergy(crowbar, false);
		}
		player.swingArm(EnumHand.MAIN_HAND);
	}

	/* IToolWrench */
	public boolean canWrench(EntityPlayer player, EnumHand hand, ItemStack wrench, RayTraceResult rayTrace) {

		ItemStack stack = player.getHeldItemMainhand();
		return getEnergyStored(stack) >= getEnergyPerUse(stack) || player.capabilities.isCreativeMode;
	}

	public void wrenchUsed(EntityPlayer player, EnumHand hand, ItemStack wrench, RayTraceResult rayTrace) {

		if (!player.capabilities.isCreativeMode) {
			useEnergy(player.getHeldItemMainhand(), false);
		}
	}

	/* CAPABILITIES */
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {

		return new EnergyContainerItemWrapper(stack, this);
	}

}
