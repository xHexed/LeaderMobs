package com.github.xhexed.leadermobs.config.mobmessage.message;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.data.MobData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class TitleMessage {
    private LeaderMobs plugin;

    private String title;
    private String subtitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;
    private long delay;
    
    public TitleMessage(LeaderMobs plugin, ConfigurationSection config) {
        this.plugin = plugin;
        title = ChatColor.translateAlternateColorCodes('&', config.getString("title", ""));
        subtitle = ChatColor.translateAlternateColorCodes('&', config.getString("sub-title", ""));
        fadeIn = config.getInt("fade-in", 10);
        stay = config.getInt("stay", 70);
        fadeOut = config.getInt("fade-out", 20);
        delay = config.getLong("delay", 0L);
    }

    public void sendToPlayers(MobData data, Function<String, String> filter) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (plugin.getPlayerDataManager().getPlayerData().getBoolean(player.getName(), false)) continue;
                player.sendTitle(
                        filter.apply(plugin.getMessageParser().parseMobEventMessage(title, data, player)),
                        filter.apply(plugin.getMessageParser().parseMobEventMessage(subtitle, data, player)),
                        fadeIn, stay, fadeOut);
            }
        }, delay);
    }
}
