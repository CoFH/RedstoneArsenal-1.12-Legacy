package cofh.redstonearsenal.gui;

import cofh.redstonearsenal.RedstoneArsenal;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;

public class GuiConfigRA extends GuiConfig {

	public GuiConfigRA(GuiScreen parentScreen) {

		super(parentScreen, getConfigElements(parentScreen), RedstoneArsenal.modId, false, false, RedstoneArsenal.modName);
	}

	public static final String[] CATEGORIES = { "Equipment", "Storage" };

	@SuppressWarnings("rawtypes")
	private static List<IConfigElement> getConfigElements(GuiScreen parent) {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		for (int i = 0; i < CATEGORIES.length; i++) {
			list.add(new ConfigElement<ConfigCategory>(RedstoneArsenal.config.getCategory(CATEGORIES[i])));
		}
		return list;
	}

}
