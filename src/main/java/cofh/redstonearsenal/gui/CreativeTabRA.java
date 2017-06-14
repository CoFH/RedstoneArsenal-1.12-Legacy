package cofh.redstonearsenal.gui;

import cofh.redstonearsenal.init.RAEquipment;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabRA extends CreativeTabs {

	public CreativeTabRA() {

		super("RedstoneArsenal");
	}

	@Override
	@SideOnly (Side.CLIENT)
	public ItemStack getIconItemStack() {

		ItemStack iconStack = new ItemStack(RAEquipment.ToolSet.FLUX.itemSword);
		iconStack.setTagCompound(new NBTTagCompound());
		iconStack.getTagCompound().setBoolean("CreativeTab", true);
		iconStack.getTagCompound().setInteger("Energy", 32000);
		iconStack.getTagCompound().setInteger("Mode", 1);

		return iconStack;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public ItemStack getTabIconItem() {

		return getIconItemStack();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public String getTabLabel() {

		return "redstonearsenal.creativeTab";
	}

}
