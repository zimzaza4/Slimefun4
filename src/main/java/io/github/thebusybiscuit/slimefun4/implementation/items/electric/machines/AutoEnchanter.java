package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This has been moved.
 *
 * @deprecated Moved to
 *             {@link io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter}
 *
 */
@Deprecated
public class AutoEnchanter extends io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter {

    @ParametersAreNonnullByDefault
    public AutoEnchanter(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

}