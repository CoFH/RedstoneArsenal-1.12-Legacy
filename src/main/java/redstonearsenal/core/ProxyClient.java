package redstonearsenal.core;

import redstonearsenal.util.KeyBindingEmpower;
import cofh.hud.CoFHHUD;
import cofh.hud.CoFHKeyBinding;
import cofh.util.StringHelper;

public class ProxyClient extends Proxy {

	public static CoFHKeyBinding empower = new CoFHKeyBinding(StringHelper.localize("keybind.redstonearsenal.empower"), 0x2F, RAProps.empowerKeyName);

	@Override
	public void registerKeyBinds() {

		super.registerKeyBinds();
		CoFHHUD.addKeybind(KeyBindingEmpower.instance, empower, false);
	}
}
