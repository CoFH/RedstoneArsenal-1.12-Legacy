package redstonearsenal.core;

import cofh.key.CoFHKey;
import cpw.mods.fml.common.FMLCommonHandler;

import redstonearsenal.util.KeyBindingEmpower;

public class ProxyClient extends Proxy {

	@Override
	public void registerKeyBinds() {

		super.registerKeyBinds();
		FMLCommonHandler.instance().bus().register(new CoFHKey());
		CoFHKey.addKeyBind(KeyBindingEmpower.instance);
	}
}
