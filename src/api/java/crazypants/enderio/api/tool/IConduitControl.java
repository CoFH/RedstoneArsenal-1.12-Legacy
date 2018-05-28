package crazypants.enderio.api.tool;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Interface for items which support the overlay and conduit view modes
 */
public interface IConduitControl extends IHideFacades {

  /**
   * Controls whether the overlay is shown and the player can change the display mode.
   * 
   * @param stack
   *          The itemstack
   * @param player
   *          The player holding the itemstack
   * @return True if the overlay should be rendered and the player should be able to change modes. False otherwise.
   */
  boolean showOverlay(@Nonnull ItemStack stack, @Nonnull EntityPlayer player);
}
