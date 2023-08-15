package com.github.xhexed.leadermobs.config.mobmessage.message;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.data.MobData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class MobSpawnMessage extends MobEventMessage {
    public MobSpawnMessage(LeaderMobs plugin, ConfigurationSection config) {
        super(plugin, config);
    }

    public void sendMessages(MobData data) {
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            for (String message : getMessages()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (getPlugin().getPlayerDataManager().getPlayerData().getBoolean(p.getName(), false)) continue;
                    p.sendMessage(getPlugin().getMessageParser().parseMobEventMessage(message, data, p));
                }
            }
        }, getDelay());
        if (getTitleMessage() != null) {
            getTitleMessage().sendToPlayers(data, Function.identity());
        }
        if (getActionbarMessage() != null) {
            getActionbarMessage().sendToPlayers(data, Function.identity());
        }
    }
}
