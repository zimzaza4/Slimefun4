package io.github.thebusybiscuit.slimefun4.implementation.items.seasonal;

import io.github.thebusybiscuit.cscorelib2.inventory.ItemUtils;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.FireworkUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The {@link ChristmasPresent} is a seasonal {@link SlimefunItem} that drops a random
 * gift when being placed down.
 *
 * @author TheBusyBiscuit
 * @see EasterEgg
 */
public class ChristmasPresent extends SimpleSlimefunItem<BlockUseHandler> implements NotPlaceable {

    private final ItemStack[] gifts;

    public ChristmasPresent(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack... gifts) {
        super(category, item, recipeType, recipe);

        this.gifts = gifts;
    }

    @Override
    public BlockUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();

            if (p.getGameMode() != GameMode.CREATIVE) {
                ItemUtils.consumeItem(e.getItem(), false);
            }

            FireworkUtils.launchRandom(p, 3);

            ItemStack gift = gifts[ThreadLocalRandom.current().nextInt(gifts.length)].clone();
            p.getWorld().dropItemNaturally(p.getLocation(), gift);
        };
    }

}