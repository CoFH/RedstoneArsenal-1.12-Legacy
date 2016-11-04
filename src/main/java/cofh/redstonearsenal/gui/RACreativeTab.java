package cofh.redstonearsenal.gui;

import cofh.redstonearsenal.item.RAItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.*;

public class RACreativeTab extends CreativeTabs {

	public RACreativeTab() {
		super("RedstoneArsenal");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		ItemStack iconStack = new ItemStack(RAItems.itemSwordFlux);
		iconStack.setTagCompound(new NBTTagCompound());
		iconStack.getTagCompound().setBoolean("Empowered", true);
		iconStack.getTagCompound().setBoolean("CreativeTab", true);
		return iconStack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {

		return getIconItemStack().getItem();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTabLabel() {

		return "redstonearsenal.creativeTab";
	}

}
