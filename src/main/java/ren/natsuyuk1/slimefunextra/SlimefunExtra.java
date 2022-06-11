package ren.natsuyuk1.slimefunextra;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import javax.annotation.Nonnull;

public class SlimefunExtra {
    public static void register(@Nonnull Slimefun sf) {
        IntegrationHelper.register(sf);
        VaultHelper.register(sf);
    }

    public static void shutdown() {
        IntegrationHelper.shutdown();
        VaultHelper.shutdown();
    }
}
