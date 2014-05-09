package redstonearsenal.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

import redstonearsenal.item.tool.IEmpowerableItem;

public class Utils {

	private Utils() {

	}

	public static boolean isPlayerHoldingEmpowerableItem(EntityPlayer player) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		return equipped instanceof IEmpowerableItem;
	}

	public static boolean isPlayerHoldingEmpoweredItem(EntityPlayer player) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		return equipped instanceof IEmpowerableItem && ((IEmpowerableItem) equipped).isEmpowered(player.getCurrentEquippedItem());
	}

	public static boolean toggleHeldEmpowerableItemState(EntityPlayer player) {

		ItemStack equipped = player.getCurrentEquippedItem();
		IEmpowerableItem empowerableItem = (IEmpowerableItem) equipped.getItem();

		return empowerableItem.setEmpoweredState(equipped, !empowerableItem.isEmpowered(equipped));
	}

	public static class DamageSourceFlux extends DamageSource {

		protected DamageSourceFlux() {

			super("flux");
			this.setDamageBypassesArmor();
		}
	}

	public static class EntityDamageSourceFlux extends EntityDamageSource {

		public EntityDamageSourceFlux(String type, Entity entity) {

			super(type, entity);
			this.setDamageBypassesArmor();
		}
	}

	public static DamageSource causePlayerFluxDamage(EntityPlayer entityPlayer) {

		return new EntityDamageSourceFlux("player", entityPlayer);
	}

	public static final DamageSourceFlux flux = new DamageSourceFlux();

}
