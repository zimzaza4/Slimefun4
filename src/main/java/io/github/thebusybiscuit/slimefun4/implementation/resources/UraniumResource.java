package io.github.thebusybiscuit.slimefun4.implementation.resources;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

class UraniumResource extends SlimefunResource {

    UraniumResource() {
        super("uranium", "小块铀", SlimefunItems.SMALL_URANIUM, 2, true);
    }

    @Override
    public int getDefaultSupply(Environment envionment, Biome biome) {
        if (envionment == Environment.NORMAL) {
            return 5;
        }

        return 0;
    }

}