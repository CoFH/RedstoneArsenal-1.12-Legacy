package redstonearsenal.core;

import cofh.key.CoFHKey;

import redstonearsenal.util.KeyBindingEmpower;

public class Proxy {

	public void registerKeyBinds() {

		CoFHKey.addServerKeyBind(KeyBindingEmpower.instance);
	}

}
