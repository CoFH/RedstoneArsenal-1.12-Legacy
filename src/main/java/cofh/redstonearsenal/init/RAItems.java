package cofh.redstonearsenal.init;

import cofh.core.util.core.IInitializer;
import cofh.redstonearsenal.item.ItemMaterial;
import cofh.redstonearsenal.item.ItemQuiverRF;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class RAItems {

	public static final RAItems INSTANCE = new RAItems();

	private RAItems() {

	}

	public static void preInit() {

		itemMaterial = new ItemMaterial();
		itemQuiver = new ItemQuiverRF();

		initList.add(itemMaterial);
		initList.add(itemQuiver);

		for (IInitializer init : initList) {
			init.initialize();
		}
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {

		for (IInitializer init : initList) {
			init.register();
		}
	}

	static ArrayList<IInitializer> initList = new ArrayList<>();

	/* REFERENCES */
	public static ItemMaterial itemMaterial;
	public static ItemQuiverRF itemQuiver;

}
