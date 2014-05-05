package redstonearsenal.core;

import redstonearsenal.util.KeyBindingEmpower;
import cofh.hud.CoFHServerKeyHandler;

public class Proxy {

	public void registerKeyBinds() {

		CoFHServerKeyHandler.addServerKeyBind(KeyBindingEmpower.instance, RAProps.empowerKeyName);
	}

}
