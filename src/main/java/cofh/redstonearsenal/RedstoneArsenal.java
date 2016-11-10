package cofh.redstonearsenal;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.core.util.ConfigHandler;
import cofh.mod.BaseMod;
import cofh.mod.updater.UpdateManager;
import cofh.redstonearsenal.core.Proxy;
import cofh.redstonearsenal.gui.RACreativeTab;
import cofh.redstonearsenal.item.RAItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.CustomProperty;
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

@Mod(modid = RedstoneArsenal.modId, name = RedstoneArsenal.modName, version = RedstoneArsenal.version, dependencies = RedstoneArsenal.dependencies, guiFactory = RedstoneArsenal.modGuiFactory, customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class RedstoneArsenal extends BaseMod {

	public static final String modId = "RedstoneArsenal";
	public static final String modName = "Redstone Arsenal";
	public static final String version = "1.10.2R1.0.0";
	public static final String version_max = "1.7.10R1.1.0";
	public static final String dependencies = CoFHCore.version_group + ";after:ThermalExpansion";
	public static final String modGuiFactory = "cofh.redstonearsenal.gui.GuiConfigRAFactory";

	public static final String version_group = "required-after:" + modId + "@[" + version + "," + version_max + ");";
	public static final String releaseURL = "https://raw.github.com/CoFH/VERSION/master/" + modId;

	@Instance("RedstoneArsenal")
	public static RedstoneArsenal instance;

	@SidedProxy(clientSide = "cofh.redstonearsenal.core.ProxyClient", serverSide = "cofh.redstonearsenal.core.Proxy")
	public static Proxy proxy;

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
		cleanConfig(true);
		tab = new RACreativeTab();
		RAItems.preInit();
		proxy.preInit();
	}

	@EventHandler
	public void initialize(FMLInitializationEvent event) {
		RAItems.initialize();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		RAItems.postInit();
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event) {
		config.cleanUp(false, true);
		log.info("Redstone Arsenal: Load Complete.");
	}

	void cleanConfig(boolean preInit) {
		if (preInit) {
		}
		String prefix = "config.redstonearsenal.";
		String[] categoryNames = config.getCategoryNames().toArray(new String[config.getCategoryNames().size()]);
		for (String categoryName : categoryNames) {
			config.getCategory(categoryName).setLanguageKey(prefix + categoryName).setRequiresMcRestart(true);
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
