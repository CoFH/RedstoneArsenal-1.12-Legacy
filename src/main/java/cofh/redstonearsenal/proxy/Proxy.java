package cofh.redstonearsenal.proxy;

import cofh.core.render.IModelRegister;
import cofh.redstonearsenal.entity.projectile.EntityFluxArrow;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Proxy {

	/* INIT */
	public void preInit(FMLPreInitializationEvent event) {

		registerEntities();
	}

	public void initialize(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	/* REGISTRATION */
	public void registerEntities() {

		EntityFluxArrow.initialize(0);
	}

	/* HELPERS */
	public boolean addIModelRegister(IModelRegister modelRegister) {

		return false;
	}

}
