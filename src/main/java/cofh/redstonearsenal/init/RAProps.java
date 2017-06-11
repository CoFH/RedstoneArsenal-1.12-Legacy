package cofh.redstonearsenal.init;

import cofh.api.item.IMultiModeItem;
import cofh.core.key.KeyBindingItemMultiMode;
import cofh.lib.util.helpers.StringHelper;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.gui.CreativeTabRA;
import net.minecraft.item.ItemStack;

import java.util.List;

public class RAProps {

	private RAProps() {

	}

	public static void preInit() {

		configCommon();
		configClient();
	}

	public static void loadComplete() {

	}

	/* HELPERS */
	private static void configCommon() {

	}

	private static void configClient() {

		/* CREATIVE TABS */
		RedstoneArsenal.tabCommon = new CreativeTabRA();
	}

	public static void addEmpoweredTip(IMultiModeItem item, ItemStack stack, List<String> tooltip) {

		if (item.getMode(stack) == 1) {
			tooltip.add(StringHelper.localizeFormat("info.redstonearsenal.tool.chargeOff", StringHelper.getKeyName(KeyBindingItemMultiMode.instance.getKey())));
		} else {
			tooltip.add(StringHelper.localizeFormat("info.redstonearsenal.tool.chargeOn", StringHelper.getKeyName(KeyBindingItemMultiMode.instance.getKey())));
		}
	}

	/* INTERFACE */
	public static boolean showArmorCharge = true;
	public static boolean showToolCharge = true;

}
