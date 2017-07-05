package cofh.redstonearsenal.init;

import cofh.core.util.core.IInitializer;
import cofh.redstonearsenal.block.BlockStorage;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class RABlocks {

	public static final RABlocks INSTANCE = new RABlocks();

	private RABlocks() {

	}

	public static void preInit() {

		blockStorage = new BlockStorage();

		initList.add(blockStorage);

		for (int i = 0; i < initList.size(); i++) {
			initList.get(i).initialize();
		}
		MinecraftForge.EVENT_BUS.register(INSTANCE);
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
	public static BlockStorage blockStorage;
}
