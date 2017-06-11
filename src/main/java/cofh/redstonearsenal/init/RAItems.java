package cofh.redstonearsenal.init;

import cofh.core.util.core.IInitializer;
import cofh.redstonearsenal.item.ItemMaterial;

import java.util.ArrayList;

public class RAItems {

	private RAItems() {

	}

	public static void preInit() {

		itemMaterial = new ItemMaterial();

		initList.add(itemMaterial);

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
	public static ItemMaterial itemMaterial;

}
