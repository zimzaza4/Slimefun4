package io.github.thebusybiscuit.slimefun4.implementation.items.geo;

import io.github.starwishsama.utils.ProtectionChecker;
import io.github.thebusybiscuit.cscorelib2.protection.ProtectableAction;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

public class GEOScanner extends SimpleSlimefunItem<BlockUseHandler> {

    public GEOScanner(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public BlockUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            Player p = e.getPlayer();
            Optional<Block> block = e.getClickedBlock();

            if (block.isPresent()) {
                Block b = block.get();

                if (hasAccess(p, b)) {
                    SlimefunPlugin.getGPSNetwork().getResourceManager().scan(p, b, 0);
                } else {
                    SlimefunPlugin.getLocalization().sendMessage(p, "inventory.no-access", true);
                }
            }
        };
    }

    @ParametersAreNonnullByDefault
    private boolean hasAccess(Player p, Block b) {
        return p.hasPermission("slimefun.gps.bypass") || (SlimefunPlugin.getProtectionManager().hasPermission(p, b, ProtectableAction.INTERACT_BLOCK)) || ProtectionChecker.canInteract(p, b, ProtectableAction.INTERACT_BLOCK);
    }
}