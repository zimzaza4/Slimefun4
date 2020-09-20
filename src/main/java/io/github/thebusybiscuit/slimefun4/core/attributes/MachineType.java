package io.github.thebusybiscuit.slimefun4.core.attributes;

import javax.annotation.Nonnull;

public enum MachineType {

    CAPACITOR("电容"),
    GENERATOR("发电机"),
    MACHINE("机器");

    private final String suffix;

    MachineType(@Nonnull String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return suffix;
    }

}
