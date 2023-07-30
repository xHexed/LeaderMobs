package com.github.xhexed.leadermobs.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class PluginMessage {
    public String toggleBroadcastOn;
    public String toggleBroadcastOff;

    public PluginMessage(ConfigurationSection config) {
        ConfigurationSection toggleBroadcast = config.getConfigurationSection("toggle-broadcast");
        if (toggleBroadcast != null) {
            toggleBroadcastOn = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(toggleBroadcast.getString("true")));
            toggleBroadcastOff = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(toggleBroadcast.getString("false")));
        }
    }
}
