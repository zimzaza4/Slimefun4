package io.github.thebusybiscuit.slimefun4.implementation.resources;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

class NetherIceResource extends SlimefunResource {

    NetherIceResource() {
        super("nether_ice", "下界冰", SlimefunItems.NETHER_ICE, 6, true);
    }

    @Override
    public int getDefaultSupply(Environment environment, Biome biome) {
        return environment == Environment.NETHER ? 32 : 0;
    }

}