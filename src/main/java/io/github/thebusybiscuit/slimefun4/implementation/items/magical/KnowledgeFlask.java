package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

public class KnowledgeFlask extends SimpleSlimefunItem<ItemUseHandler> {

    @ParametersAreNonnullByDefault
    public KnowledgeFlask(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(category, item, recipeType, recipe, recipeOutput);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();
            if (p.getLevel() >= 1 && (!e.getClickedBlock().isPresent() || !(e.getClickedBlock().get().getType().isInteractable()))) {
                p.setLevel(p.getLevel() - 1);

                ItemStack item = SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE.clone();

                if (!p.getInventory().addItem(item).isEmpty()) {
                    // The Item could not be added, let's drop it to the ground (fixes #2728)
                    p.getWorld().dropItemNaturally(p.getLocation(), item);
                }

                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 0.5F);

                item.setAmount(item.getAmount() - 1);
                e.cancel();
            }
        };
    }

}
