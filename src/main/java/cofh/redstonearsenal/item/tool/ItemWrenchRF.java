package cofh.redstonearsenal.item.tool;

import cofh.api.block.IDismantleable;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.item.IToolHammer;
import cofh.asm.relauncher.Implementable;
import cofh.asm.relauncher.Substitutable;
import cofh.lib.util.capabilities.EnergyContainerItemWrapper;
import cofh.lib.util.helpers.*;
import cofh.redstonearsenal.init.RAProps;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
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
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

@Implementable ({ "buildcraft.api.tools.IToolWrench", "mods.railcraft.api.core.items.IToolCrowbar" })
public class ItemWrenchRF extends ItemShears implements IEnergyContainerItem, IToolHammer {

	protected int maxEnergy = 320000;
	protected int maxTransfer = 4000;

	protected int energyPerUse = 200;

	protected ToolMaterial toolMaterial;

	protected boolean showInCreative = true;

	public ItemWrenchRF(ToolMaterial toolMaterial) {

		super();

		this.toolMaterial = toolMaterial;
		setMaxDamage(toolMaterial.getMaxUses());
		setNoRepair();

		setHarvestLevel("wrench", 1);

		addPropertyOverride(new ResourceLocation("active"), (stack, world, entity) -> ItemWrenchRF.this.getEnergyStored(stack) > 0 ? 1F : 0F);
	}

	public ItemWrenchRF setEnergyParams(int maxEnergy, int maxTransfer, int energyPerUse) {

		this.maxEnergy = maxEnergy;
		this.maxTransfer = maxTransfer;
		this.energyPerUse = energyPerUse;

		return this;
	}

	public ItemWrenchRF setShowInCreative(boolean showInCreative) {

		this.showInCreative = showInCreative;
		return this;
	}

	protected int getEnergyPerUse(ItemStack stack) {

		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 4);
		return energyPerUse * (5 - unbreakingLevel) / 5;
	}

	protected int useEnergy(ItemStack stack, boolean simulate) {

		int unbreakingLevel = MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), 0, 4);
		return extractEnergy(stack, energyPerUse * (5 - unbreakingLevel) / 5, simulate);
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
		list.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.formatNumber(stack.getTagCompound().getInteger("Energy")) + " / " + StringHelper.formatNumber(maxEnergy) + " RF");
		list.add(StringHelper.ORANGE + getEnergyPerUse(stack) + " " + StringHelper.localize("info.redstonearsenal.tool.energyPerUse") + StringHelper.END);
		list.add(StringHelper.getFlavorText("info.redstonearsenal.tool.wrench"));
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubItems(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		if (showInCreative) {
			list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item), 0));
			list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item), maxEnergy));
		}
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
	public boolean isDamaged(ItemStack stack) {

		return true;
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {

		if (ServerHelper.isClientWorld(entity.worldObj)) {
			entity.rotationYaw += 90;
			entity.rotationYaw %= 360;
			return false;
		}
		if (entity instanceof IShearable) {
			IShearable target = (IShearable) entity;
			BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
			if (target.isShearable(stack, entity.worldObj, pos)) {
				List<ItemStack> drops = target.onSheared(stack, entity.worldObj, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));

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

		if (ServerHelper.isClientWorld(player.worldObj)) {
			return false;
		}
		Block block = player.worldObj.getBlockState(pos).getBlock();
		if (block instanceof IShearable) {
			IShearable target = (IShearable) block;
			if (target.isShearable(stack, player.worldObj, pos)) {
				List<ItemStack> drops = target.onSheared(stack, player.worldObj, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));

				for (ItemStack drop : drops) {
					float f = 0.7F;
					double d = MathHelper.RANDOM.nextFloat() * f + (1.0F - f) * 0.5D;
					double d1 = MathHelper.RANDOM.nextFloat() * f + (1.0F - f) * 0.5D;
					double d2 = MathHelper.RANDOM.nextFloat() * f + (1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.worldObj, pos.getX() + d, pos.getY() + d1, pos.getZ() + d2, drop);
					entityitem.setPickupDelay(10);
					player.worldObj.spawnEntityInWorld(entityitem);
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

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !(!slotChanged && oldStack.isItemEqual(newStack) && getEnergyStored(oldStack) < getEnergyStored(newStack));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {

		return RAProps.showToolCharge && stack.getTagCompound() != null && !stack.getTagCompound().getBoolean("CreativeTab");
	}

	@Override
	public int getItemEnchantability() {

		return toolMaterial.getEnchantability();
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

		return EnumRarity.UNCOMMON;
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		return ServerHelper.isClientWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

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
		PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(player, hand, stack, pos, side, new Vec3d(hitX, hitY, hitZ));
		if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Result.DENY || event.getUseItem() == Result.DENY || event.getUseBlock() == Result.DENY) {
			return EnumActionResult.PASS;
		}
		if (ServerHelper.isServerWorld(world) && player.isSneaking() && block instanceof IDismantleable && ((IDismantleable) block).canDismantle(world, pos, state, player)) {
			((IDismantleable) block).dismantleBlock(world, pos, state, player, false);
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return EnumActionResult.SUCCESS;
		} else if (handleIC2Tile(this, stack, player, world, pos, side.ordinal())) {
			return ServerHelper.isServerWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
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
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Tool modifier", 1, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.4D, 0));
		}
		return multimap;
	}

	/* TRICKERY */
	@Substitutable (method = "returnFalse", value = "ic2.api.tile.IWrenchable")
	static boolean handleIC2Tile(IToolHammer tool, ItemStack stack, EntityPlayer player, World world, BlockPos pos, int hitSide) {

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (!block.hasTileEntity(state)) {
			return false;
		}
		boolean ret = false;
		TileEntity tile = world.getTileEntity(pos);

		if (block instanceof IWrenchable) {
			IWrenchable wrenchable = (IWrenchable) block;

			if (player.isSneaking()) {
				hitSide = BlockHelper.SIDE_OPPOSITE[hitSide];
			}
			if (wrenchable.setFacing(world, pos, EnumFacing.VALUES[hitSide], player)) {
				ret = true;
			} else if (wrenchable.wrenchCanRemove(world, pos, player)) {
				int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
				List<ItemStack> drops = wrenchable.getWrenchDrops(world, pos, state, tile, player, fortune);

				if (!drops.isEmpty()) {
					world.setBlockToAir(pos);
					if (ServerHelper.isServerWorld(world)) {
						for (ItemStack drop : drops) {
							float f = 0.7F;
							double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
							double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
							double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
							EntityItem entity = new EntityItem(world, pos.getX() + x2, pos.getY() + y2, pos.getZ() + z2, drop);
							entity.setPickupDelay(10);
							;
							world.spawnEntityInWorld(entity);
						}
					}
					ret = true;
				}
			}
		}
		if (ret) {
			tool.toolUsed(stack, player, pos);
		}
		return ret;
	}

	static boolean returnFalse(IToolHammer tool, ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide) {

		return false;
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
