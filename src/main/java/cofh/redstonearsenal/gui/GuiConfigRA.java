package cofh.redstonearsenal.gui;

import java.util.ArrayList;
import java.util.List;

import cofh.redstonearsenal.RedstoneArsenal;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfigRA extends GuiConfig {

	public GuiConfigRA(GuiScreen parentScreen) {

		super(parentScreen, getConfigElements(parentScreen), RedstoneArsenal.modId, false, false, RedstoneArsenal.modName);
	}

	public static final String[] CATEGORIES = {
			"Equipment", "Storage"
	};

	private static List<IConfigElement> getConfigElements(GuiScreen parent) {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		for (String element : CATEGORIES) {
			list.add(new ConfigElement(RedstoneArsenal.config.getCategory(element)));
		}
		return list;
	}

}
