package cofh.redstonearsenal;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.core.util.ConfigHandler;
import cofh.redstonearsenal.init.RABlocks;
import cofh.redstonearsenal.proxy.Proxy;
import cofh.redstonearsenal.gui.CreativeTabRA;
import cofh.redstonearsenal.init.RAEquipment;
import cofh.redstonearsenal.init.RAItems;
import cofh.thermalfoundation.ThermalFoundation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod (modid = RedstoneArsenal.MOD_ID, name = RedstoneArsenal.MOD_NAME, version = RedstoneArsenal.VERSION, dependencies = RedstoneArsenal.DEPENDENCIES, guiFactory = RedstoneArsenal.MOD_GUI_FACTORY)
public class RedstoneArsenal {

	public static final String MOD_ID = "redstonearsenal";
	public static final String MOD_NAME = "Redstone Arsenal";
	public static final String VERSION = "1.2.0";
	public static final String VERSION_MAX = "1.3.0";
	public static final String DEPENDENCIES = CoFHCore.VERSION_GROUP + ThermalFoundation.VERSION_GROUP + "after:ThermalExpansion";
	public static final String MOD_GUI_FACTORY = "cofh.redstonearsenal.gui.GuiConfigRAFactory";

	public static final String VERSION_GROUP = "required-after:" + MOD_ID + "@[" + VERSION + "," + VERSION_MAX + ");";

	@Instance (MOD_ID)
	public static RedstoneArsenal instance;

	@SidedProxy (clientSide = "cofh.redstonearsenal.proxy.ProxyClient", serverSide = "cofh.redstonearsenal.proxy.Proxy")
	public static Proxy proxy;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);
	public static final ConfigHandler CONFIG = new ConfigHandler(VERSION);
	public static final ConfigHandler CONFIG_CLIENT = new ConfigHandler(VERSION);

	public static CreativeTabs tab = new CreativeTabRA();

	/* INIT SEQUENCE */
	public RedstoneArsenal() {

		super();
	}

	/* INIT SEQUENCE */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		CONFIG.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/" + MOD_ID + "/common.cfg"), true));
		CONFIG_CLIENT.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/" + MOD_ID + "/client.cfg"), true));

		RABlocks.preInit();
		RAItems.preInit();
		RAEquipment.preInit();

		proxy.preInit(event);
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		RABlocks.initialize();
		RAItems.initialize();
		RAEquipment.initialize();

		proxy.initialize(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		RABlocks.postInit();
		RAItems.postInit();
		RAEquipment.postInit();

		proxy.postInit(event);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {

		CONFIG.cleanUp(false, true);
		CONFIG_CLIENT.cleanUp(false, true);

		LOG.info(MOD_NAME + ": Load Complete.");
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {

	}

	@EventHandler
	public void handleIMC(IMCEvent theIMC) {

	}

}
