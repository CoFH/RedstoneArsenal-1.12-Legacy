package cofh.redstonearsenal.block;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Created by brandon3055 on 5/11/2016.
 */
public class ItemBlockFlux extends ItemBlock {
    public ItemBlockFlux(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + (BlockStorage.Type.byMetadata(stack.getMetadata()).getUnlocalizedName());
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }
}
