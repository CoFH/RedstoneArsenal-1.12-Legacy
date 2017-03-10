package cofh.redstonearsenal.init;

import cofh.core.util.core.IInitializer;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.block.BlockStorage;

import java.util.ArrayList;

public class RABlocks {

	private RABlocks() {

	}

	public static void preInit() {

		blockStorage = new BlockStorage();

		initList.add(blockStorage);

		RedstoneArsenal.proxy.addIModelRegister(blockStorage);

		for (int i = 0; i < initList.size(); i++) {
			initList.get(i).preInit();
		}
	}

	public static void initialize() {

		for (int i = 0; i < initList.size(); i++) {
			initList.get(i).initialize();
		}
	}

	public static void postInit() {

		for (int i = 0; i < initList.size(); i++) {
			initList.get(i).postInit();
		}
		initList.clear();
	}

	static ArrayList<IInitializer> initList = new ArrayList<>();

	/* REFERENCES */
	public static BlockStorage blockStorage;
}
