package redstonearsenal.util;

import net.minecraft.entity.player.EntityPlayer;
import cofh.CoFHCore;
import cofh.key.IKeyBinding;

public class KeyBindingEmpower implements IKeyBinding {

	public static KeyBindingEmpower instance = new KeyBindingEmpower();

	@Override
	public String getUUID() {

		return "ra.tool";
	}

	@Override
	public boolean keyPress() {
		EntityPlayer player = CoFHCore.proxy.getClientPlayer();
		return player != null && Utils.isPlayerHoldingEmpowerableItem(player);
	}

	@Override
	public void keyPressServer(EntityPlayer player) {
		if (Utils.isPlayerHoldingEmpowerableItem(player)
				&& Utils.toggleHeldEmpowerableItemState(player)) {
			if (Utils.isPlayerHoldingEmpoweredItem(player)) {
				player.worldObj.playSoundAtEntity(player,
						"ambient.weather.thunder", 0.4F, 1.0F);
			} else {
				player.worldObj.playSoundAtEntity(player, "random.orb", 0.2F,
						0.6F);
			}
		}
	}

	@Override
	public int getKey() {
		return 0x2F;
	}

	@Override
	public boolean suppressRepeating() {
		return true;
	}

	@Override
	public boolean hasServerSide() {
		return true;
	}

}
