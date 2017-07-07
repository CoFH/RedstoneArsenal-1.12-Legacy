package cofh.redstonearsenal.block;

import cofh.core.block.ItemBlockCore;
import cofh.core.util.helpers.ItemHelper;
import cofh.redstonearsenal.block.BlockStorage.Type;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class ItemBlockStorage extends ItemBlockCore {

	public ItemBlockStorage(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.redstonearsenal.storage." + Type.byMetadata(ItemHelper.getItemDamage(stack)).getNameRaw() + ".name";
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		return Type.byMetadata(ItemHelper.getItemDamage(stack)).getRarity();
	}

}
