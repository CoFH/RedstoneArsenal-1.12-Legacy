package cofh.redstonearsenal.gui;

import cofh.redstonearsenal.item.RAItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class RACreativeTab extends CreativeTabs {

	static ItemStack iconStack;

	public static void initialize() {

		iconStack = new ItemStack(RAItems.itemSwordFlux, 1, Short.MAX_VALUE);
		iconStack.setTagCompound(new NBTTagCompound());
		iconStack.stackTagCompound.setBoolean("Empowered", true);
		iconStack.stackTagCompound.setBoolean("CreativeTab", true);
	}

	public RACreativeTab() {

		super("RedstoneArsenal");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {

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
