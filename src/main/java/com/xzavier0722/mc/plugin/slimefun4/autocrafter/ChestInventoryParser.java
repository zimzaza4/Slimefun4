package com.xzavier0722.mc.plugin.slimefun4.autocrafter;

import io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters.AbstractAutoCrafter;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

/**
 *
 * This is the implementation of {@link CrafterInteractable} with vanilla inventory.
 *
 * @author Xzavier0722
 *
 */
public class ChestInventoryParser implements CrafterInteractable {
    private final Inventory inv;

    public ChestInventoryParser(Inventory inv) {
        this.inv = inv;
    }

    @Override
    public boolean canOutput(ItemStack item) {
        return inv.firstEmpty() > -1 || isFit(item);
    }

    @Override
    public boolean matchRecipe(AbstractAutoCrafter crafter, Collection<Predicate<ItemStack>> recipe, Map<Integer, Integer> itemQuantities) {
        for (Predicate<ItemStack> predicate : recipe) {
            // Check if any Item matches the Predicate
            if (!crafter.matchesAny(inv, itemQuantities, predicate)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return inv.getItem(slot);
    }

    @Override
    public boolean addItem(ItemStack item) {
        return inv.addItem(item).isEmpty();
    }

    private boolean isFit(ItemStack item) {
        ItemStack[] contents = inv.getContents();

        ItemStackWrapper wrapper = ItemStackWrapper.wrap(item);
        int amount = wrapper.getAmount();

        for (ItemStack each : contents) {
            int eachAmount = each.getAmount();
            int maxAmount = each.getMaxStackSize();
            if (SlimefunUtils.isItemSimilar(each, wrapper, true, false) && eachAmount < maxAmount) {
                int restAmount = maxAmount - eachAmount;
                if (amount <= restAmount) {
                    return true;
                }
                amount -= restAmount;
            }
        }
        return false;
    }

}
