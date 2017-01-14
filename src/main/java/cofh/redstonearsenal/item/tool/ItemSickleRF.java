package cofh.redstonearsenal.item.tool;

import cofh.core.util.CoreUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;

public class ItemSickleRF extends ItemToolRF {

	public int radius = 3;

	public ItemSickleRF(ToolMaterial toolMaterial) {

		super(-2.2F, toolMaterial);
		addToolClass("sickle");
		damage = 5;

		effectiveBlocks.add(Blocks.WEB);
		effectiveBlocks.add(Blocks.VINE);
		effectiveBlocks.add(Blocks.LEAVES);
		effectiveBlocks.add(Blocks.LEAVES2);

		effectiveMaterials.add(Material.LEAVES);
		effectiveMaterials.add(Material.PLANTS);
		effectiveMaterials.add(Material.VINE);
		effectiveMaterials.add(Material.WEB);

		addPropertyOverride(new ResourceLocation("flux_sickle_empowered"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {

				return ItemSickleRF.this.getEnergyStored(stack) > 0 && ItemSickleRF.this.isEmpowered(stack) ? 1F : 0F;
			}
		});
		addPropertyOverride(new ResourceLocation("flux_sickle_active"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {

				return ItemSickleRF.this.getEnergyStored(stack) > 0 && !ItemSickleRF.this.isEmpowered(stack) ? 1F : 0F;
			}
		});
	}

	public ItemSickleRF setRadius(int radius) {

		this.radius = radius;
		return this;
	}

	@Override
	protected boolean harvestBlock(World world, BlockPos pos, EntityPlayer player) {

		if (world.isAirBlock(pos)) {
			return false;
		}
		EntityPlayerMP playerMP = null;
		if (player instanceof EntityPlayerMP) {
			playerMP = (EntityPlayerMP) player;
		}
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		// only effective materials
		if (!(getToolClasses(player.getHeldItemMainhand()).contains(block.getHarvestTool(state)) || canHarvestBlock(state, player.getHeldItemMainhand()))) {
			return false;
		}
		if (!ForgeHooks.canHarvestBlock(block, player, world, pos)) {
			return false;
		}
		// send the blockbreak event
		int xpToDrop = 0;
		if (playerMP != null) {
			xpToDrop = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, pos);
			if (xpToDrop == -1) {
				return false;
			}
		}
		if (player.capabilities.isCreativeMode) {
			if (!world.isRemote) {
				block.onBlockHarvested(world, pos, state, player);
			}
			if (block.removedByPlayer(state, world, pos, player, false)) {
				block.onBlockDestroyedByPlayer(world, pos, state);
			}
			// send update to client
			if (!world.isRemote) {
				playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
			} else {
				Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
			}
			return true;
		}
		if (!world.isRemote) {
			block.onBlockHarvested(world, pos, state, player);
			if (block.removedByPlayer(state, world, pos, player, true)) {
				block.onBlockDestroyedByPlayer(world, pos, state);
				block.harvestBlock(world, player, pos, state, null, null);
				if (block.equals(Blocks.VINE)) {
					CoreUtils.dropItemStackIntoWorldWithVelocity(new ItemStack(Blocks.VINE), world, pos);
				} else if (block.equals(Blocks.LEAVES)) {
					CoreUtils.dropItemStackIntoWorldWithVelocity(new ItemStack(Blocks.LEAVES), world, pos);
				} else if (block.equals(Blocks.LEAVES2)) {
					CoreUtils.dropItemStackIntoWorldWithVelocity(new ItemStack(Blocks.LEAVES2), world, pos);
				}
				if (xpToDrop > 0) {
					block.dropXpOnBlockBreak(world, pos, xpToDrop);
				}
			}
			// always send block update to client
			playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
		} else {
			if (block.removedByPlayer(state, world, pos, player, true)) {
				block.onBlockDestroyedByPlayer(world, pos, state);
			}
			Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
		}
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

		World world = player.worldObj;
		IBlockState state = world.getBlockState(pos);

		if (!canHarvestBlock(state, stack)) {
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return false;
		}
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		boolean used = false;
		int boost = isEmpowered(stack) ? 2 : 0;

		world.playEvent(2001, pos, Block.getStateId(state));

		for (int i = x - (radius + boost); i <= x + (radius + boost); i++) {
			for (int k = z - (radius + boost); k <= z + (radius + boost); k++) {
				for (int j = y - boost; j <= y + boost; j++) {
					used |= harvestBlock(world, new BlockPos(i, j, k), player);
				}
			}
		}
		if (used && !player.capabilities.isCreativeMode) {
			useEnergy(stack, false);
		}
		return true;
	}

}
