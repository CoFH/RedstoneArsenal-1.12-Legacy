package redstonearsenal.core;

import redstonearsenal.util.KeyBindingEmpower;
import cofh.key.CoFHKey;

public class Proxy {

	public void registerKeyBinds() {

		CoFHKey.addServerKeyBind(KeyBindingEmpower.instance);
	}

}
