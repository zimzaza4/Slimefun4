package me.mrCookieSlime.Slimefun.api.inventory;

import org.bukkit.inventory.ItemStack;

/**
 * @author TheBusyBiscuit
 * @deprecated Please use {@link BlockMenuPreset#onItemStackChange(DirtyChestMenu, int, ItemStack, ItemStack)} instead.
 */
@Deprecated
@FunctionalInterface
public interface ItemManipulationEvent {

    ItemStack onEvent(int slot, ItemStack previous, ItemStack next);

}
