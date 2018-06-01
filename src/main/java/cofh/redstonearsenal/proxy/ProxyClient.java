package cofh.redstonearsenal.proxy;

import cofh.core.render.IModelRegister;
import cofh.core.render.entity.RenderArrowCore;
import cofh.redstonearsenal.RedstoneArsenal;
import cofh.redstonearsenal.entity.projectile.EntityArrowFlux;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;

public class ProxyClient extends Proxy {

	/* INIT */
	@Override
	public void preInit(FMLPreInitializationEvent event) {

		super.preInit(event);

		for (IModelRegister register : modelList) {
			register.registerModels();
		}
		registerRenderInformation();
	}

	@Override
	public void initialize(FMLInitializationEvent event) {

		super.initialize(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

		super.postInit(event);
	}

	/* REGISTRATION */
	public void registerRenderInformation() {

		RenderingRegistry.registerEntityRenderingHandler(EntityArrowFlux.class, manager -> new RenderArrowCore(manager, new ResourceLocation(RedstoneArsenal.MOD_ID, "textures/entity/arrow_flux.png")));
	}

	/* HELPERS */
	public boolean addIModelRegister(IModelRegister modelRegister) {

		return modelList.add(modelRegister);
	}

	private static ArrayList<IModelRegister> modelList = new ArrayList<>();

}
