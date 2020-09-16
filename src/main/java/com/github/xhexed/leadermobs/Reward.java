package com.github.xhexed.leadermobs;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.github.xhexed.leadermobs.utils.Utils.DAMAGE_POS;
import static com.github.xhexed.leadermobs.utils.Utils.PLAYER_NAME;

public class Reward {
    private final String mobname;
    private final FileConfiguration config = LeaderMobs.rewards;

    public Reward(final String mobname, final List<UUID> topDealtList, final List<UUID> topTakenList) {
        this.mobname      = mobname;

        if (!config.contains(mobname)) {
            return;
        }

        giveRewards(getRewards(".dealt"), topDealtList);
        giveRewards(getRewards(".taken"), topTakenList);
    }

    private void giveRewards(final List<List<String>> rewards, final List<UUID> topList) {
        if (topList.size() < rewards.size()) return;
        IntStream.range(0, rewards.size()).forEach(i -> {
            final UUID uuid = topList.get(i);
            final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            rewards.get(i).stream()
                    .map(command -> PLAYER_NAME.matcher(command).replaceAll(player.getName()))
                    .map(command -> DAMAGE_POS.matcher(command).replaceAll(Integer.toString(i + 1)))
                    .map(command -> PlaceholderAPI.setPlaceholders(player, command))
                    .map(command -> be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, command))
                    .forEach(command -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
        });
    }

    private List<List<String>> getRewards(final String path) {
        final List<List<String>> rewards = new ArrayList<>();
        final ConfigurationSection section = config.getConfigurationSection(mobname + path);
        if (section == null) return rewards;
        section.getKeys(false)
                .forEach(place -> rewards.addAll(
                        Collections.singleton(config.getStringList(mobname + path + "." + place + ".rewards"))));
        return rewards;
    }
}
