package cofh.redstonearsenal.item.tool;

import cofh.core.util.CoreUtils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class ItemSickleRF extends ItemToolRF {

	public int radius = 3;

	public ItemSickleRF(Item.ToolMaterial toolMaterial) {

		super(toolMaterial);

		damage = 5;

		effectiveMaterials.add(Material.leaves);
		effectiveMaterials.add(Material.plants);
		effectiveMaterials.add(Material.vine);
		effectiveMaterials.add(Material.web);
		effectiveBlocks.add(Blocks.web);
		effectiveBlocks.add(Blocks.vine);
	}

	public ItemSickleRF setRadius(int radius) {

		this.radius = radius;
		return this;
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack) {

		return super.canHarvestBlock(block, stack);
	}

	@Override
	protected boolean harvestBlock(World world, int x, int y, int z, EntityPlayer player) {

		if (world.isAirBlock(x, y, z))
			return false;
		EntityPlayerMP playerMP = null;
		if (player instanceof EntityPlayerMP) {
			playerMP = (EntityPlayerMP) player;
		}
		// check if the block can be broken, since extra block breaks shouldn't instantly break stuff like obsidian
		// or precious ores you can't harvest while mining stone
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		// only effective materials
		if (!(getToolClasses(player.getCurrentEquippedItem()).contains(block.getHarvestTool(meta)) ||
				canHarvestBlock(block, player.getCurrentEquippedItem())))
			return false;

		if (!ForgeHooks.canHarvestBlock(block, player, meta))
			return false;
		// send the blockbreak event
		BreakEvent event = null;
		if (playerMP != null) {
			event = ForgeHooks.onBlockBreakEvent(world, playerMP.theItemInWorldManager.getGameType(), playerMP, x, y, z);
			if (event.isCanceled())
				return false;
		}

		if (player.capabilities.isCreativeMode) {
			if (!world.isRemote)
				block.onBlockHarvested(world, x, y, z, meta, player);

			if (block.removedByPlayer(world, player, x, y, z, false))
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);
			// send update to client
			if (!world.isRemote) {
				playerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
			} else {
				Minecraft.getMinecraft().getNetHandler()
							.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
			}
			return true;
		}

		if (!world.isRemote) {
			// serverside we reproduce ItemInWorldManager.tryHarvestBlock
			// ItemInWorldManager.removeBlock
			block.onBlockHarvested(world, x, y, z, meta, player);
			if (block.removedByPlayer(world, player, x, y, z, true)) {
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);
				block.harvestBlock(world, player, x, y, z, meta);
				if (block.equals(Blocks.vine)) {
					CoreUtils.dropItemStackIntoWorldWithVelocity(new ItemStack(Blocks.vine), world, x, y, z);
				}
				if (event != null)
					block.dropXpOnBlockBreak(world, x, y, z, event.getExpToDrop());
			}
			// always send block update to client
			playerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
		} else {
			//PlayerControllerMP pcmp = Minecraft.getMinecraft().playerController;
			// clientside we do a "this block has been clicked on long enough to be broken" call. This should not send any new packets
			// the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.
			// following code can be found in PlayerControllerMP.onPlayerDestroyBlock
			if (block.removedByPlayer(world, player, x, y, z, true)) {
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);
			}
			Minecraft.getMinecraft().getNetHandler()
						.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, Minecraft.getMinecraft().objectMouseOver.sideHit));
		}
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {

		World world = player.worldObj;
		Block block = world.getBlock(x, y, z);

		if (!canHarvestBlock(block, stack)) {
			if (!player.capabilities.isCreativeMode) {
				useEnergy(stack, false);
			}
			return false;
		}
		boolean used = false;
		int boost = isEmpowered(stack) ? 2 : 0;

		world.playAuxSFXAtEntity(player, 2001, x, y, z, Block.getIdFromBlock(block) | (world.getBlockMetadata(x, y, z) << 12));

		for (int i = x - (radius + boost); i <= x + (radius + boost); i++) {
			for (int k = z - (radius + boost); k <= z + (radius + boost); k++) {
				for (int j = y - boost; j <= y + boost; j++) {
					used |= harvestBlock(world, i, j, k, player);
				}
			}
		}
		if (used && !player.capabilities.isCreativeMode) {
			useEnergy(stack, false);
		}
		return true;
	}

}
