package com.github.xhexed.leadermobs;

import com.github.xhexed.leadermobs.command.CommandManager;
import com.github.xhexed.leadermobs.manager.ConfigManager;
import com.github.xhexed.leadermobs.manager.PlayerDataManager;
import com.github.xhexed.leadermobs.manager.MobEventManager;
import com.github.xhexed.leadermobs.manager.RewardManager;
import com.github.xhexed.leadermobs.util.PluginUtil;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public class LeaderMobs extends JavaPlugin {
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private PluginUtil pluginUtil;
    private MobEventManager mobEventManager;
    private RewardManager rewardManager;
    public boolean papi;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager(this);
        pluginUtil = new PluginUtil(this);
        mobEventManager = new MobEventManager(this);
        rewardManager = new RewardManager(this);

        PluginCommand command = Objects.requireNonNull(getCommand("lm"));
        CommandManager commandManager = new CommandManager(this);
        command.setExecutor(commandManager);
        command.setTabCompleter(commandManager);

        reloadPlugin();
    }

    public void reloadPlugin() {
        configManager.reloadConfig();
        playerDataManager.reloadData();
        rewardManager.reloadData();

        PluginManager manager = getServer().getPluginManager();
        Logger logger = getLogger();
        HandlerList.unregisterAll(this);
        boolean found = false;
        if (manager.isPluginEnabled("MythicMobs")) {
            final String[] version = Objects.requireNonNull(manager.getPlugin("MythicMobs")).getDescription().getVersion().split("\\.");
            final int mainVersion = Integer.parseInt(version[0]);
            if (mainVersion < 4 || (mainVersion == 4 && Integer.parseInt(version[1]) < 9)) {
                logger.info("Found legacy version of MythicMobs (4.9.0-), hooking...");
                try {
                    manager.registerEvents((Listener) Class.forName("com.github.xhexed.leadermobs.listener.LegacyMythicMobsListener").getDeclaredConstructor(getClass()).newInstance(this), this);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                logger.info("Found MythicMobs, hooking...");
                try {
                    manager.registerEvents((Listener) Class.forName("com.github.xhexed.leadermobs.listener.MythicMobsListener").getDeclaredConstructor(getClass()).newInstance(this), this);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            found = true;
        }
        if (!found) {
            logger.severe("Couldn't find any custom mobs plugin...");
            return;
        }

        if (manager.isPluginEnabled("PlaceholderAPI")) {
            logger.info("Found PlaceholderAPI, hooking...");
            papi = true;
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public MobEventManager getMobEventHandler() {
        return mobEventManager;
    }

    public PluginUtil getPluginUtil() {
        return pluginUtil;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }
}
