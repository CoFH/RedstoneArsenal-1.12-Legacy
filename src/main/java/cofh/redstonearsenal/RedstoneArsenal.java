package cofh.redstonearsenal;

import cofh.CoFHCore;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.util.ConfigHandler;
import cofh.redstonearsenal.init.RABlocks;
import cofh.redstonearsenal.init.RAEquipment;
import cofh.redstonearsenal.init.RAItems;
import cofh.redstonearsenal.init.RAProps;
import cofh.redstonearsenal.proxy.Proxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod (modid = RedstoneArsenal.MOD_ID, name = RedstoneArsenal.MOD_NAME, version = RedstoneArsenal.VERSION, dependencies = RedstoneArsenal.DEPENDENCIES, updateJSON = RedstoneArsenal.UPDATE_URL, certificateFingerprint = "8a6abf2cb9e141b866580d369ba6548732eff25f")
public class RedstoneArsenal {

	public static final String MOD_ID = "redstonearsenal";
	public static final String MOD_NAME = "Redstone Arsenal";

	public static final String VERSION = "2.4.1";
	public static final String VERSION_MAX = "2.5.0";
	public static final String VERSION_GROUP = "required-after:" + MOD_ID + "@[" + VERSION + "," + VERSION_MAX + ");";
	public static final String UPDATE_URL = "https://raw.github.com/cofh/version/master/" + MOD_ID + "_update.json";

	public static final String DEPENDENCIES = CoFHCore.VERSION_GROUP + "after:thermalexpansion;";
	public static final String MOD_GUI_FACTORY = "cofh.redstonearsenal.gui.GuiConfigRAFactory";

	@Instance (MOD_ID)
	public static RedstoneArsenal instance;

	@SidedProxy (clientSide = "cofh.redstonearsenal.proxy.ProxyClient", serverSide = "cofh.redstonearsenal.proxy.Proxy")
	public static Proxy proxy;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);
	public static final ConfigHandler CONFIG = new ConfigHandler(VERSION);
	public static final ConfigHandler CONFIG_CLIENT = new ConfigHandler(VERSION);

	public static CreativeTabs tabCommon;       // Blocks and general stuff.
	public static CreativeTabs tabItems;        // Non-usable items.

	public static CreativeTabs tabBasicTools;   // Basic Tools, Non-Combat
	public static CreativeTabs tabBasicCombat;  // Basic Tools, Combat
	public static CreativeTabs tabBasicArmor;   // Basic Armor

	public RedstoneArsenal() {

		super();
	}

	/* INIT */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		CONFIG.setConfiguration(new Configuration(new File(CoreProps.configDir, "/cofh/" + MOD_ID + "/common.cfg"), true));
		CONFIG_CLIENT.setConfiguration(new Configuration(new File(CoreProps.configDir, "/cofh/" + MOD_ID + "/client.cfg"), true));

		RAProps.preInit();

		RABlocks.preInit();
		RAItems.preInit();
		RAEquipment.preInit();

		CoreEnchantments.register();

		proxy.preInit(event);
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		proxy.initialize(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		proxy.postInit(event);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {

		CONFIG.cleanUp(false, true);
		CONFIG_CLIENT.cleanUp(false, true);

		LOG.info(MOD_NAME + ": Load Complete.");
	}

}
