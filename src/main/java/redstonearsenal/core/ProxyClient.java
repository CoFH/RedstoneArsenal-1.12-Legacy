package redstonearsenal.core;

import redstonearsenal.util.KeyBindingEmpower;
import cofh.key.CoFHKey;

public class ProxyClient extends Proxy {

	@Override
	public void registerKeyBinds() {

		super.registerKeyBinds();
		CoFHKey.addKeyBind(KeyBindingEmpower.instance);
	}
}
