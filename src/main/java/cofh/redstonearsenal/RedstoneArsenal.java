package cofh.redstonearsenal;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.core.util.ConfigHandler;
import cofh.mod.BaseMod;
import cofh.mod.updater.UpdateManager;
import cofh.redstonearsenal.gui.RACreativeTab;
import cofh.redstonearsenal.item.RAItems;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = RedstoneArsenal.modId, name = RedstoneArsenal.modName, version = RedstoneArsenal.version, dependencies = RedstoneArsenal.dependencies,
		guiFactory = RedstoneArsenal.modGuiFactory, customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class RedstoneArsenal extends BaseMod {

	public static final String modId = "RedstoneArsenal";
	public static final String modName = "Redstone Arsenal";
	public static final String version = "1.7.10R1.1.1B1";
	public static final String dependencies = "required-after:CoFHCore@[" + CoFHCore.version + ",);after:ThermalExpansion";
	public static final String releaseURL = "https://raw.github.com/CoFH/VERSION/master/RedstoneArsenal";
	public static final String modGuiFactory = "cofh.redstonearsenal.gui.GuiConfigRAFactory";

	@Instance("RedstoneArsenal")
	public static RedstoneArsenal instance;

	public static final Logger log = LogManager.getLogger(modId);
	public static final ConfigHandler config = new ConfigHandler(version);
	public static CreativeTabs tab;

	/* INIT SEQUENCE */
	public RedstoneArsenal() {

		super(log);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		UpdateManager.registerUpdater(new UpdateManager(this, releaseURL, CoFHProps.DOWNLOAD_URL));

		config.setConfiguration(new Configuration(new File(CoFHProps.configDir, "/cofh/redstonearsenal/common.cfg"), true));
		tab = new RACreativeTab();

		RAItems.preInit();
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {

		RAItems.initialize();
		RACreativeTab.initialize();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		RAItems.postInit();

		config.cleanUp(false, true);
	}

	void cleanConfig(boolean preInit) {

		if (preInit) {

		}
		String prefix = "config.redstonearsenal.";
		String[] categoryNames = config.getCategoryNames().toArray(new String[config.getCategoryNames().size()]);
		for (int i = 0; i < categoryNames.length; i++) {
			config.getCategory(categoryNames[i]).setLanguageKey(prefix + categoryNames[i]).setRequiresMcRestart(true);
		}
	}

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
