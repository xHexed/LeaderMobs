package com.github.xhexed.leadermobs;

import com.github.xhexed.leadermobs.commands.Commands;
import com.github.xhexed.leadermobs.listeners.BossListener;
import com.github.xhexed.leadermobs.listeners.MythicMobsListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

public class LeaderMobs extends JavaPlugin {
    public static File playerdata;
    public static boolean broadcast;
    public static boolean debug;
    public static boolean papi;
    public static boolean mvdw;
    private static LeaderMobs instance;
    private static File debugfile;

    public static LeaderMobs getInstance() { return instance; }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        if (!new File(getDataFolder(), "rewards.yml").exists()) {
            saveResource("rewards.yml", true);
        }

        final File datafile = new File(getDataFolder(), "data.yml");
        if (!datafile.exists()) {
            try {
                datafile.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        playerdata = datafile;

        final FileConfiguration config = getConfig();

        debug          = config.getBoolean("debug");
        broadcast = config.getBoolean("Messages.broadcast");

        final PluginManager manager = getServer().getPluginManager();
        final Logger logger = getLogger();
        if (manager.isPluginEnabled("Boss")) {
            logger.info("Found Boss, hooking...");
            manager.registerEvents(new BossListener(), this);
        }
        if (manager.isPluginEnabled("MythicMobs")) {
            logger.info("Found MythicMobs, hooking...");
            manager.registerEvents(new MythicMobsListener(), this);
        }
        else {
            logger.severe("Didn't found any hookable mobs plugin, disabling..");
            manager.disablePlugin(this);
            return;
        }

        if (manager.isPluginEnabled("PlaceholderAPI")) {
            logger.info("Found PlaceholderAPI, hooking...");
            papi = true;
        }
        if (manager.isPluginEnabled("MVdWPlaceholderAPI")) {
            logger.info("Found MVdWPlaceholderAPI, hooking...");
            mvdw = true;
        }

        Objects.requireNonNull(getCommand("lm")).setExecutor(new Commands());

        if (debug) {
            final File Debugfolder = new File(getDataFolder() + "/debugs");
            if (!Debugfolder.exists())
                Debugfolder.mkdirs();

            try {
                final SimpleDateFormat df = new SimpleDateFormat("MM-dd-hh");
                final String file = "debug_" + df.format(new Date()) + ".txt";
                logger.info("Debug mode is on, out file: " + file);
                debugfile = new File(getDataFolder() + "/debugs", file);
                final BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(debugfile, true));
                writer.write(("Server version: " + getServer().getVersion() + '\n').getBytes());
                writer.flush();
                writer.close();
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reload() {
        Bukkit.getPluginManager().disablePlugin(this);
        Bukkit.getPluginManager().enablePlugin(this);
    }

    @Override
    public void onDisable() {
        instance = null;
        HandlerList.unregisterAll(this);
    }

    public void debug(final String text) {
        if (!debug) return;
        try (final BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(debugfile, true))) {
            writer.write(('\n' + text).getBytes());
            writer.flush();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
