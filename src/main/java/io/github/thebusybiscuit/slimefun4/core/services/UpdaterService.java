package io.github.thebusybiscuit.slimefun4.core.services;

import io.github.thebusybiscuit.cscorelib2.config.Config;
import io.github.thebusybiscuit.cscorelib2.updater.Updater;
import io.github.thebusybiscuit.slimefun4.api.SlimefunBranch;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.PatternUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Level;

/**
 * This Class represents our {@link Updater} Service.
 * If enabled, it will automatically connect to https://thebusybiscuit.github.io/builds/
 * to check for updates and to download them automatically.
 *
 * @author TheBusyBiscuit
 */
public class UpdaterService {

    private final SlimefunPlugin plugin;
    private final Updater updater;

    /**
     * This will create a new {@link UpdaterService} for the given {@link SlimefunPlugin}.
     * The {@link File} should be the result of the getFile() operation of that {@link Plugin}.
     *
     * @param plugin  The instance of Slimefun
     * @param version The current version of Slimefun
     * @param file    The {@link File} of this {@link Plugin}
     */
    public UpdaterService(SlimefunPlugin plugin, String version, File file) {
        this.plugin = plugin;
        this.updater = null;
    }

    /**
     * This method returns the branch the current build of Slimefun is running on.
     * This can be used to determine whether we are dealing with an official build
     * or a build that was unofficially modified.
     *
     * @return The branch this build of Slimefun is on.
     */
    public SlimefunBranch getBranch() {
        return SlimefunBranch.UNOFFICIAL;
    }

    /**
     * This method returns the build number that this is running on (or -1 if unofficial).
     * You should combine the usage with {@link #getBranch()} in order to properly see if this is
     * a development or stable build number.
     *
     * @return The build number of this Slimefun.
     */
    public int getBuildNumber() {
        if (updater != null && PatternUtils.NUMERIC.matcher(this.updater.getLocalVersion()).matches())
            return Integer.parseInt(this.updater.getLocalVersion());

        return -1;
    }

    /**
     * This will start the {@link UpdaterService} and check for updates.
     * If it can find an update it will automatically be installed.
     */
    public void start() {
    }

    /**
     * This returns whether the {@link Updater} is enabled or not.
     * This includes the {@link Config} setting but also whether or not we are running an
     * official or unofficial build.
     *
     * @return Whether the {@link Updater} is enabled
     */
    public boolean isEnabled() {
        return false;
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