package io.github.thebusybiscuit.slimefun4.implementation.items.cargo;

import io.github.thebusybiscuit.cscorelib2.chat.ChatColors;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.cargo.CargoNet;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The {@link CargoConnectorNode} connects a {@link CargoNode} with a {@link CargoNet}.
 * It has no further functionality.
 *
 * @author TheBusyBiscuit
 * @see CargoNode
 * @see CargoNet
 */
public class CargoConnectorNode extends SimpleSlimefunItem<BlockUseHandler> {

    @ParametersAreNonnullByDefault
    public CargoConnectorNode(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(category, item, recipeType, recipe, recipeOutput);
    }

    @Nonnull
    @Override
    public BlockUseHandler getItemHandler() {
        return e -> {
            if (!e.getClickedBlock().isPresent()) return;

            Player p = e.getPlayer();
            Block b = e.getClickedBlock().get();

            if (CargoNet.getNetworkFromLocation(b.getLocation()) != null) {
                p.sendMessage(ChatColors.color("&7连接状态: " + "&2\u2714"));
            } else {
                p.sendMessage(ChatColors.color("&7连接状态: " + "&4\u2718"));
            }
        };
    }
}
