package com.github.xhexed.leadermobs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.stream.IntStream;

import static com.github.xhexed.leadermobs.LeaderMobs.getInstance;
import static com.github.xhexed.leadermobs.utils.Utils.*;

public class Reward {
    private final String mobname;
    private final FileConfiguration config = YamlConfiguration.loadConfiguration(new File(getInstance().getDataFolder(), "rewards.yml"));

    public Reward(final String mobname, final List<UUID> topDealtList, final List<UUID> topTakenList) {
        this.mobname      = mobname;
        debug("Place list: ");
        topDealtList.forEach((uuid) -> debug(Bukkit.getOfflinePlayer(uuid).getName() + ", "));
        debugln("");

        if (!config.contains(mobname)) {
            debugln("Rewards for mob: " + mobname + " not found...");
            return;
        }

        giveRewards(getRewards(".dealt"), topDealtList);
        giveRewards(getRewards(".taken"), topTakenList);
    }

    private void giveRewards(final List<List<String>> rewards, final List<UUID> topList) {
        if (topList.size() < rewards.size()) return;
        IntStream.range(0, rewards.size()).forEach(i -> {
            final UUID uuid = topList.get(i);
            final String player = Bukkit.getOfflinePlayer(uuid).getName();
            debugln("Giving reward for " + player);
            rewards.get(i).stream()
                    .map(command -> PLAYER_NAME.matcher(command).replaceAll(player))
                    .map(command -> DAMAGE_POS.matcher(command).replaceAll(Integer.toString(i + 1)))
                    .forEach(command -> {
                        debugln("Place: " + i + "command:" + command);
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                    });
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
