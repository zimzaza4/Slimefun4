package ren.natsuyuk1.slimefunextra.inventoryholder;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;

public class SlimefunBackpackHolder extends SlimefunInventoryHolder {
    private PlayerBackpack backpack;

    public void setBackpack(PlayerBackpack backpack) {
        if (this.backpack == null) {
            this.backpack = backpack;
        } else {
            throw new IllegalStateException("Backpack already initialized!");
        }
    }

    public PlayerBackpack getBackpack() {
        if (backpack == null) {
            throw new IllegalStateException("Backpack is null!");
        }
        return backpack;
    }
}
