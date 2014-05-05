package redstonearsenal;

import java.io.File;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redstonearsenal.core.Proxy;
import redstonearsenal.core.RAProps;
import redstonearsenal.gui.RACreativeTab;
import redstonearsenal.item.RAItems;
import cofh.core.CoFHProps;
import cofh.mod.BaseMod;
import cofh.util.ConfigHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = RedstoneArsenal.modId, name = RedstoneArsenal.modName, version = RedstoneArsenal.version, dependencies = "required-after:Forge@["
		+ CoFHProps.FORGE_REQ + ",);required-after:CoFHCore@[" + CoFHProps.VERSION + ",);after:ThermalExpansion")
public class RedstoneArsenal extends BaseMod {

	public static final String modId = "RedstoneArsenal";
	public static final String version = RAProps.VERSION;
	public static final String modName = RAProps.NAME;

	@Instance("RedstoneArsenal")
	public static RedstoneArsenal instance;

	@SidedProxy(clientSide = "redstonearsenal.core.ProxyClient", serverSide = "redstonearsenal.core.Proxy")
	public static Proxy proxy;

	/* INIT SEQUENCE */
	public RedstoneArsenal() {

		super(log);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		config.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/RedstoneArsenal.cfg")));

		RAItems.preInit();
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		RAItems.initialize();
		RACreativeTab.initialize();

		proxy.registerKeyBinds();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		RAItems.postInit();

		config.cleanUp(false, true);
	}

	public static Logger log = LogManager.getLogger(modId);
	public static ConfigHandler config = new ConfigHandler(RAProps.VERSION);
	public static final CreativeTabs tab = new RACreativeTab();

	/* BaseMod */
	@Override
	public String getModId() {

		return modId;
	}

	@Override
	public String getModName() {

		return modName;
	}

	@Override
	public String getModVersion() {

		return version;
	}

}
