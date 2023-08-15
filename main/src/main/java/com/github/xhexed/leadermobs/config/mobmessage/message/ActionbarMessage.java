package com.github.xhexed.leadermobs.config.mobmessage.message;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.data.MobData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class ActionbarMessage {
    private LeaderMobs plugin;
    private String message;
    private long delay;

    public ActionbarMessage(LeaderMobs plugin, ConfigurationSection config) {
        this.plugin = plugin;
        this.delay = config.getLong("delay", 0L);
        message = ChatColor.translateAlternateColorCodes('&', config.getString("message", ""));
    }

    public void sendToPlayers(MobData data, Function<String, String> filter) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (plugin.getPlayerDataManager().getPlayerData().getBoolean(player.getName(), false)) continue;
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(filter.apply(plugin.getMessageParser().parseMobEventMessage(message, data, player)))
                );
            }
        }, delay);
    }
}
