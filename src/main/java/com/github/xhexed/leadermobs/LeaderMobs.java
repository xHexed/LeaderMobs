package com.github.xhexed.leadermobs;

import com.github.xhexed.leadermobs.command.CommandManager;
import com.github.xhexed.leadermobs.config.ConfigManager;
import com.github.xhexed.leadermobs.data.PlayerDataManager;
import com.github.xhexed.leadermobs.listener.BossListener;
import com.github.xhexed.leadermobs.listener.EliteMobsListener;
import com.github.xhexed.leadermobs.listener.MythicMobsListener;
import com.github.xhexed.leadermobs.manager.MobEventManager;
import com.github.xhexed.leadermobs.manager.RewardManager;
import com.github.xhexed.leadermobs.util.PluginUtil;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
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
    public boolean mvdw;

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
        PluginManager manager = getServer().getPluginManager();
        Logger logger = getLogger();
        HandlerList.unregisterAll(this);
        boolean found = false;
        if (manager.isPluginEnabled("Boss")) {
            logger.info("Found Boss, hooking...");
            manager.registerEvents(new BossListener(this), this);
            found = true;
        }
        if (manager.isPluginEnabled("MythicMobs")) {
            logger.info("Found MythicMobs, hooking...");
            manager.registerEvents(new MythicMobsListener(this), this);
            found = true;
        }
        if (manager.isPluginEnabled("EliteMobs")) {
            logger.info("Found EliteMobs, hooking...");
            manager.registerEvents(new EliteMobsListener(this), this);
            found = true;
        }
        if (!found) {
            logger.severe("Couldn't find any hookable mobs plugin...");
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

        configManager.reloadConfig();
        playerDataManager.reloadData();
        rewardManager.reloadData();
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
