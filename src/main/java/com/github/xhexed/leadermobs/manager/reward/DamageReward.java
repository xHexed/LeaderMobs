package com.github.xhexed.leadermobs.manager.reward;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class DamageReward {
    public TreeMap<Integer, DamagePlaceReward> placeRewards;

    public DamageReward(ConfigurationSection config) {
        for (String place : config.getKeys(false)) {
            placeRewards.put(Integer.parseInt(place), new DamagePlaceReward(Objects.requireNonNull(config.getConfigurationSection(place))));
        }
    }

    public static class DamagePlaceReward {
        public int place;
        public List<String> commands;

        public DamagePlaceReward(ConfigurationSection config) {
            place = Integer.parseInt(config.getName());
            commands = config.getStringList("commands");
        }
    }
}
