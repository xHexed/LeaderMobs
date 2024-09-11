package com.github.xhexed.leadermobs.config.mobmessage.message;

import com.github.xhexed.leadermobs.LeaderMobs;
import com.github.xhexed.leadermobs.config.mobmessage.MobMessage;
import com.github.xhexed.leadermobs.data.DamageTracker;
import com.github.xhexed.leadermobs.data.MobDamageTracker;
import com.github.xhexed.leadermobs.data.MobData;
import com.github.xhexed.leadermobs.util.PlaceholderParser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class DamageMessage extends MobEventMessage {
    private MobMessage mobMessage;
    private boolean hideEmptyHeader;
    private boolean hideEmptyFooter;
    private List<String> headerMessages;
    private List<String> footerMessages;
    private String defaultPlacePrefix;
    private Map<Integer, String> placePrefixes = new HashMap<>();

    public DamageMessage(LeaderMobs plugin, MobMessage mobMessage, ConfigurationSection config) {
        super(plugin, config);
        this.mobMessage = mobMessage;
        hideEmptyHeader = config.getBoolean("hide-empty-header");
        hideEmptyFooter = config.getBoolean("hide-empty-footer");
        headerMessages = config.getStringList("header-messages");
        headerMessages.replaceAll((message) -> ChatColor.translateAlternateColorCodes('&', message));
        footerMessages = config.getStringList("footer-messages");
        footerMessages.replaceAll((message) -> ChatColor.translateAlternateColorCodes('&', message));
        defaultPlacePrefix = ChatColor.translateAlternateColorCodes('&', config.getString("default-place-prefix", ""));
        if (config.contains("place-prefix")) {
            ConfigurationSection placePrefix = config.getConfigurationSection("place-prefix");
            placePrefixes.clear();
            for (String place : Objects.requireNonNull(placePrefix).getKeys(false)) {
                placePrefixes.put(Integer.valueOf(place), ChatColor.translateAlternateColorCodes('&', placePrefix.getString(place, "")));
            }
        }
    }

    public void sendMessages(DamageTracker tracker, MobData data) {
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            DamageTracker.TopDamageResult result = tracker.getTopDamageResult();
            List<DamageTracker.DamageData> damageData = result.getDamageData();

            if (!(damageData.isEmpty() && hideEmptyHeader)) {
                for (String headerMessage : headerMessages) {
                    sendMessage(headerMessage, data, tracker.getTracker());
                }
            }

            for (int place = 1; place <= Math.min(damageData.size(), mobMessage.getPlacesToBroadcast()); place++) {
                DamageTracker.DamageData info = damageData.get(place - 1);

                Double damage = info.getTotalDamage();
                OfflinePlayer player = Bukkit.getOfflinePlayer(info.getTracker());

                for (String message : getMessages()) {
                    message = PlaceholderParser.PLACE_PREFIX.matcher(message).replaceAll(placePrefixes.getOrDefault(place, defaultPlacePrefix));
                    message = PlaceholderParser.DAMAGE_POS.matcher(message).replaceAll(Integer.toString(place));
                    message = PlaceholderParser.PLAYER_NAME.matcher(message).replaceAll(player.getName());
                    message = PlaceholderParser.DAMAGE.matcher(message).replaceAll(PlaceholderParser.DOUBLE_FORMAT.format(damage));
                    message = PlaceholderParser.PERCENTAGE.matcher(message).replaceAll(PlaceholderParser.DOUBLE_FORMAT.format(PlaceholderParser.getPercentage(damage, result.getTotalDamage())));
                    message = getPlugin().getMessageParser().replacePlaceholder(player, message);
                    sendMessage(message, data, tracker.getTracker());
                }
            }

            if (!(damageData.isEmpty() && hideEmptyFooter)) {
                for (String footerMessage : footerMessages) {
                    sendMessage(footerMessage, data, tracker.getTracker());
                }
            }
        }, getDelay());
        if (getTitleMessage() != null) {
            getTitleMessage().sendToPlayers(data, (m) -> PlaceholderParser.replaceMobPlaceholder(m, tracker.getTracker()));
        }
        if (getActionbarMessage() != null) {
            getActionbarMessage().sendToPlayers(data, (m) -> PlaceholderParser.replaceMobPlaceholder(m, tracker.getTracker()));
        }
    }

    private void sendMessage(String message, MobData data, MobDamageTracker tracker) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (getPlugin().getPlayerDataManager().getPlayerData().getBoolean(p.getName(), false)) continue;
            message = getPlugin().getMessageParser().parseMobEventMessage(message, data, p);
            message = PlaceholderParser.replaceMobPlaceholder(message, tracker);
            getPlugin().getMessageManager().sendMessage(p, message);
        }
    }
}
