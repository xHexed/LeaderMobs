package com.github.xhexed.leadermobs.config;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

@Getter
public class PluginMessage {
    private String toggleBroadcastOnMessage;
    private String toggleBroadcastOffMessage;

    public PluginMessage(ConfigurationSection config) {
        ConfigurationSection toggleBroadcast = config.getConfigurationSection("toggle-broadcast");
        if (toggleBroadcast != null) {
            toggleBroadcastOnMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(toggleBroadcast.getString("true")));
            toggleBroadcastOffMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(toggleBroadcast.getString("false")));
        }
    }
}
