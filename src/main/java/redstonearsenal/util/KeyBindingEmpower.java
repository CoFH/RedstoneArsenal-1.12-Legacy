package redstonearsenal.util;

import net.minecraft.entity.player.EntityPlayer;
import cofh.CoFHCore;
import cofh.hud.IKeyBinding;

public class KeyBindingEmpower implements IKeyBinding {

	public static KeyBindingEmpower instance = new KeyBindingEmpower();

	@Override
	public boolean keyDown(String key, boolean tickEnd, boolean isRepeat) {

		EntityPlayer player = CoFHCore.proxy.getClientPlayer();
		return player != null && Utils.isPlayerHoldingEmpowerableItem(player);
	}

	@Override
	public boolean keyUp(String key, boolean tickEnd) {

		return false;
	}

	@Override
	public void keyDownServer(String key, boolean tickEnd, boolean isRepeat, EntityPlayer player) {

		if (Utils.isPlayerHoldingEmpowerableItem(player) && Utils.toggleHeldEmpowerableItemState(player)) {
			if (Utils.isPlayerHoldingEmpoweredItem(player)) {
				player.worldObj.playSoundAtEntity(player, "ambient.weather.thunder", 0.4F, 1.0F);
			} else {
				player.worldObj.playSoundAtEntity(player, "random.orb", 0.2F, 0.6F);
			}
		}
	}

	@Override
	public void keyUpServer(String key, boolean tickEnd, EntityPlayer thePlayer) {

	}

	@Override
	public String getUUID() {

		return "ra.tool";
	}

}
