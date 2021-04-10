package com.xzavier0722.mc.plugin.slimefun4.autocrafter;

import io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters.AbstractAutoCrafter;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

/**
 *
 * The interface is for item interacting between inventory and crafter.
 *
 * @author Xzavier0722
 *
 */
public interface CrafterInteractable {

    /**
     * Check if the item can be output to inventory.
     * @param item: the item will check.
     * @return true if there are enough space, else false.
     */
    boolean canOutput(ItemStack item);

    /**
     * Check if the items in inventory match all recipe items.
     * This usually use with #matchesAny() in {@link AbstractAutoCrafter}
     * @param crafter: the crafter that invoked this method.
     * @param recipe: the collection of recipe ingredients' predicate to test the item.
     * @param itemQuantities: the item's slot and amount after consume.
     * @return true if all matched, else false.
     */
    boolean matchRecipe(AbstractAutoCrafter crafter, Collection<Predicate<ItemStack>> recipe, Map<Integer, Integer> itemQuantities);

    ItemStack getItem(int slot);

    boolean addItem(ItemStack item);

    /**
     * This is for updating the count of different ingredient in recipe.
     * This will be invoked when the recipe is changed.
     * Usually use for controlling the item insertion of cargo.
     * @param b: block to update.
     * @param count: amount of different item.
     */
    default void setIngredientCount(Block b, int count) {}


}
