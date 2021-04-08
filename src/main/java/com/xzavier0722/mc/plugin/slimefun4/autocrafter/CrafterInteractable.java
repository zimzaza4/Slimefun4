package com.xzavier0722.mc.plugin.slimefun4.autocrafter;

import io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters.AbstractAutoCrafter;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

/**
 *
 * The interface that provide item interaction between inventory and crafter.
 *
 * @author Xzavier0722
 *
 */
public interface CrafterInteractable {

    boolean canOutput(ItemStack item);

    boolean matchRecipe(AbstractAutoCrafter crafter, Collection<Predicate<ItemStack>> recipe, Map<Integer, Integer> itemQuantities);

    ItemStack getItem(int slot);

    boolean addItem(ItemStack item);

    default void setIngredientCount(Block b, int count){}


}
