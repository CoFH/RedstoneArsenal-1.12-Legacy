package cofh.redstonearsenal.gui;

import cpw.mods.fml.client.IModGuiFactory;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiConfigRAFactory implements IModGuiFactory {

	/* IModGuiFactory */
	@Override
	public void initialize(Minecraft minecraftInstance) {

	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {

		return GuiConfigRA.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {

		return null;
	}

}
