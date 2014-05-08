package redstonearsenal.core;

import redstonearsenal.util.KeyBindingEmpower;
import cofh.key.CoFHKey;
import cpw.mods.fml.common.FMLCommonHandler;

public class ProxyClient extends Proxy {

	@Override
	public void registerKeyBinds() {

		super.registerKeyBinds();
		FMLCommonHandler.instance().bus().register(new CoFHKey());
		CoFHKey.addKeyBind(KeyBindingEmpower.instance);
	}
}
