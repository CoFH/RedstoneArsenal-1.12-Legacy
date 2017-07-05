package cofh.redstonearsenal.init;

import cofh.core.util.core.IInitializer;
import cofh.redstonearsenal.item.ItemMaterial;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class RAItems {

	public static final RAItems INSTANCE = new RAItems();

	private RAItems() {

	}

	public static void preInit() {

		itemMaterial = new ItemMaterial();

		initList.add(itemMaterial);

		for (int i = 0; i < initList.size(); i++) {
			initList.get(i).initialize();
		}
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {

		for (int i = 0; i < initList.size(); i++) {
			initList.get(i).register();
		}
	}

	static ArrayList<IInitializer> initList = new ArrayList<>();

	/* REFERENCES */
	public static ItemMaterial itemMaterial;

}
