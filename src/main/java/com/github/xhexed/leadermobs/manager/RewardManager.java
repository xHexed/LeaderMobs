package com.github.xhexed.leadermobs.manager;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.data.MobDamageTracker;
import com.github.xhexed.leadermobs.manager.reward.TopDamageReward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RewardManager {
    private LeaderMobs plugin;
    private Map<String, Map<String, TopDamageReward>> pluginTopDamageRewards = new HashMap<>();

    public RewardManager(LeaderMobs plugin) {
        this.plugin = plugin;
    }

    public void giveReward(String plugin, String mobName, MobDamageTracker info) {
        if (!pluginTopDamageRewards.containsKey(plugin)) return;
        Map<String, TopDamageReward> topDamageRewards = pluginTopDamageRewards.get(plugin);
        if (!topDamageRewards.containsKey(mobName)) return;
        topDamageRewards.get(mobName).giveRewards(info);
    }

    public void reloadData() {
        plugin.saveResource("rewards.yml", false);
        FileConfiguration rewardConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "rewards.yml"));
        pluginTopDamageRewards.clear();
        ConfigurationSection pluginHooks = rewardConfig.getConfigurationSection("rewards.plugin-hooks");
        if (pluginHooks != null) {
            for (String pl : pluginHooks.getKeys(false)) {
                Map<String, TopDamageReward> damageRewards = new HashMap<>();
                ConfigurationSection mobList = pluginHooks.getConfigurationSection(pl);
                for (String mob : Objects.requireNonNull(mobList).getKeys(false)) {
                    damageRewards.put(mob, new TopDamageReward(plugin, Objects.requireNonNull(mobList.getConfigurationSection(mob))));
                }
                pluginTopDamageRewards.put(pl, damageRewards);
            }
        }
    }
}
