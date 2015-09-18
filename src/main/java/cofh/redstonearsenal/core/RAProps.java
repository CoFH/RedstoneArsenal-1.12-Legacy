package cofh.redstonearsenal.core;

import cofh.api.item.IEmpowerableItem;
import cofh.core.util.KeyBindingEmpower;
import cofh.lib.util.helpers.StringHelper;

import java.util.List;

import net.minecraft.item.ItemStack;

public class RAProps {

	private RAProps() {

	}

	public static boolean showArmorCharge = true;
	public static boolean showToolCharge = true;

	public static void addEmpoweredTip(IEmpowerableItem item, ItemStack stack, List<String> list) {

		if (item.isEmpowered(stack)) {
			list.add(StringHelper.YELLOW + StringHelper.ITALIC + StringHelper.localize("info.cofh.press") + " "
					+ StringHelper.getKeyName(KeyBindingEmpower.instance.getKey()) + " " + StringHelper.localize("info.redstonearsenal.tool.chargeOff")
					+ StringHelper.END);
		} else {
			list.add(StringHelper.BRIGHT_BLUE + StringHelper.ITALIC + StringHelper.localize("info.cofh.press") + " "
					+ StringHelper.getKeyName(KeyBindingEmpower.instance.getKey()) + " " + StringHelper.localize("info.redstonearsenal.tool.chargeOn")
					+ StringHelper.END);
		}
	}

}
