package com.github.xhexed.leadermobs.manager.reward;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DamageReward {
    public List<DamagePlaceReward> placeRewards = new ArrayList<>();

    public DamageReward(ConfigurationSection config) {
        for (String place : config.getKeys(false)) {
            placeRewards.add(new DamagePlaceReward(Objects.requireNonNull(config.getConfigurationSection(place))));
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
