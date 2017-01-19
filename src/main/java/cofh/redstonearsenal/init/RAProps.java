package cofh.redstonearsenal.init;

import cofh.api.item.IMultiModeItem;
import cofh.core.util.KeyBindingMultiMode;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.item.ItemStack;

import java.util.List;

public class RAProps {

	private RAProps() {

	}

	public static void preInit() {

	}

	public static void loadComplete() {

	}

	public static boolean showArmorCharge = true;
	public static boolean showToolCharge = true;

	public static void addEmpoweredTip(IMultiModeItem item, ItemStack stack, List<String> list) {

		if (item.getMode(stack) == 1) {
			list.add(StringHelper.YELLOW + StringHelper.ITALIC + StringHelper.localize("info.cofh.press") + " " + StringHelper.getKeyName(KeyBindingMultiMode.instance.getKey()) + " " + StringHelper.localize("info.redstonearsenal.tool.chargeOff") + StringHelper.END);
		} else {
			list.add(StringHelper.BRIGHT_BLUE + StringHelper.ITALIC + StringHelper.localize("info.cofh.press") + " " + StringHelper.getKeyName(KeyBindingMultiMode.instance.getKey()) + " " + StringHelper.localize("info.redstonearsenal.tool.chargeOn") + StringHelper.END);
		}
	}

}
