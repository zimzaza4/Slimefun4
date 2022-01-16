package io.github.thebusybiscuit.slimefun4.core.services;

import java.io.File;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.plugin.Plugin;

import io.github.bakedlibs.dough.config.Config;
import io.github.bakedlibs.dough.updater.GitHubBuildsUpdater;
import io.github.bakedlibs.dough.updater.PluginUpdater;
import io.github.bakedlibs.dough.versions.PrefixedVersion;
import io.github.thebusybiscuit.slimefun4.api.SlimefunBranch;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This Class represents our {@link PluginUpdater} Service.
 * If enabled, it will automatically connect to https://thebusybiscuit.github.io/builds/
 * to check for updates and to download them automatically.
 *
 * @author TheBusyBiscuit
 *
 */
public class UpdaterService {

    /**
     * Our {@link Slimefun} instance.
     */
    private final Slimefun plugin;

    /**
     * Our {@link PluginUpdater} implementation.
     */
    private final PluginUpdater<PrefixedVersion> updater;

    /**
     * The {@link SlimefunBranch} we are currently on.
     * If this is an official {@link SlimefunBranch}, auto updates will be enabled.
     */
    private final SlimefunBranch branch;

    /**
     * This will create a new {@link UpdaterService} for the given {@link Slimefun}.
     * The {@link File} should be the result of the getFile() operation of that {@link Plugin}.
     *
     * @param plugin
     *            The instance of Slimefun
     * @param version
     *            The current version of Slimefun
     * @param file
     *            The {@link File} of this {@link Plugin}
     */
    public UpdaterService(@Nonnull Slimefun plugin, @Nonnull String version, @Nonnull File file) {
        this.plugin = plugin;
        GitHubBuildsUpdater autoUpdater = null;

        branch = SlimefunBranch.UNOFFICIAL;

        this.updater = autoUpdater;
    }

    /**
     * This method returns the branch the current build of Slimefun is running on.
     * This can be used to determine whether we are dealing with an official build
     * or a build that was unofficially modified.
     *
     * @return The branch this build of Slimefun is on.
     */
    public @Nonnull SlimefunBranch getBranch() {
        return branch;
    }

    /**
     * This method returns the build number that this is running on (or -1 if unofficial).
     * You should combine the usage with {@link #getBranch()} in order to properly see if this is
     * a development or stable build number.
     *
     * @return The build number of this Slimefun.
     */
    public int getBuildNumber() {
        if (updater != null) {
            PrefixedVersion version = updater.getCurrentVersion();
            return version.getVersionNumber();
        }

        return -1;
    }

    /**
     * This will start the {@link UpdaterService} and check for updates.
     * If it can find an update it will automatically be installed.
     */
    public void start() {
    }

    /**
     * This returns whether the {@link PluginUpdater} is enabled or not.
     * This includes the {@link Config} setting but also whether or not we are running an
     * official or unofficial build.
     *
     * @return Whether the {@link PluginUpdater} is enabled
     */
    public boolean isEnabled() {
        return Slimefun.getCfg().getBoolean("options.auto-update") && updater != null;
    }

    /**
     * This method is called when the {@link UpdaterService} was disabled.
     */
    public void disable() {
    }

    private void printBorder() {
        plugin.getLogger().log(Level.WARNING, "#######################################################");
    }

}