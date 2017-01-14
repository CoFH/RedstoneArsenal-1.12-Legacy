package cofh.redstonearsenal.gui;

import cofh.redstonearsenal.RedstoneArsenal;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class GuiConfigRA extends GuiConfig {

	public GuiConfigRA(GuiScreen parentScreen) {

		super(parentScreen, getConfigElements(parentScreen), RedstoneArsenal.MOD_ID, false, false, RedstoneArsenal.MOD_NAME);
	}

	public static final String[] CATEGORIES = { "Equipment", "Storage" };

	private static List<IConfigElement> getConfigElements(GuiScreen parent) {

		List<IConfigElement> list = new ArrayList<IConfigElement>();

		for (String element : CATEGORIES) {
			list.add(new ConfigElement(RedstoneArsenal.CONFIG.getCategory(element)));
		}
		return list;
	}

}
