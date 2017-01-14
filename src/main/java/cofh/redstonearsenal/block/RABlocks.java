package cofh.redstonearsenal.block;

import cofh.api.core.IInitializer;
import cofh.redstonearsenal.RedstoneArsenal;

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
	}

	static ArrayList<IInitializer> initList = new ArrayList<IInitializer>();

	/* REFERENCES */
	public static BlockStorage blockStorage;
}
