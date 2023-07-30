package com.github.xhexed.leadermobs.util;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.config.mobmessage.AbstractMobMessage;
import com.github.xhexed.leadermobs.config.mobmessage.message.DamageMessage;
import com.github.xhexed.leadermobs.data.DamageTracker;
import com.github.xhexed.leadermobs.data.MobDamageTracker;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PluginUtil {
    private LeaderMobs plugin;

    public PluginUtil(LeaderMobs plugin) {
        this.plugin = plugin;
    }

    public String getMobSpawnMessage(String mobName, int x, int y, int z, String message) {
        message = Util.NAME.matcher(message).replaceAll(ChatColor.stripColor(mobName));
        message = Util.POS_X.matcher(message).replaceAll(Integer.toString(x));
        message = Util.POS_Y.matcher(message).replaceAll(Integer.toString(y));
        message = Util.POS_Z.matcher(message).replaceAll(Integer.toString(z));
        message = replacePlaceholder(null, message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMobDeathMessage(MobDamageTracker info, String mobName, String message) {
        message = Util.NAME.matcher(message).replaceAll(ChatColor.stripColor(mobName));
        message = Util.replaceMobPlaceholder(message, info);
        message = replacePlaceholder(null, message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String replacePlaceholder(OfflinePlayer player, String string) {
        if (plugin.papi)
            string = PlaceholderAPI.setPlaceholders(player, string);
        return string;
    }

    public void sendPlaceMessage(double total, AbstractMobMessage mobMessage, DamageMessage damageMessage, List<DamageTracker> damageList) {
        for (int place = 1; place <= damageList.size(); place++) {
            if (place >= mobMessage.placesToBroadcast) break;

            DamageTracker info = damageList.get(place - 1);

            Double damage = info.getTotalDamage();
            OfflinePlayer player = Bukkit.getOfflinePlayer(info.getTracker());

            for (String message : damageMessage.messages) {
                message = Util.PLACE_PREFIX.matcher(message).replaceAll(damageMessage.placePrefixes.getOrDefault(place, damageMessage.defaultPlacePrefix));
                message = Util.DAMAGE_POS.matcher(message).replaceAll(Integer.toString(place));
                message = Util.PLAYER_NAME.matcher(message).replaceAll(player.getName());
                message = Util.DAMAGE.matcher(message).replaceAll(Util.DOUBLE_FORMAT.format(damage));
                message = Util.PERCENTAGE.matcher(message).replaceAll(Util.DOUBLE_FORMAT.format(Util.getPercentage(damage, total)));
                message = replacePlaceholder(player, message);
                sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }


    public void sendMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (plugin.getPlayerDataManager().getPlayerData().getBoolean(p.getName(), false)) return;
            p.sendMessage(replacePlaceholder(p, ChatColor.translateAlternateColorCodes('&', message)));
        });
    }

    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        if (plugin.getPlayerDataManager().getPlayerData().getBoolean(player.getName(), false)) return;
        player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
    }

    public void sendActionBar(Player player, String message) {
        if (plugin.getPlayerDataManager().getPlayerData().getBoolean(player.getName(), false)) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}
