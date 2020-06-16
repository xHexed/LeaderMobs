package com.github.xhexed.leadermobs;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.stream.IntStream;

import static com.github.xhexed.leadermobs.LeaderMobs.getInstance;
import static com.github.xhexed.leadermobs.utils.Utils.*;

public class Reward {
    private final String mobname;
    private final Server server = Bukkit.getServer();
    private final CommandSender sender = Bukkit.getConsoleSender();
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

        final Map<Integer, List<String>> dealtRewards = new HashMap<>();
        final Map<Integer, List<String>> takenRewards = new HashMap<>();
        setRewards(dealtRewards, ".dealt");
        setRewards(takenRewards, ".taken");

        giveRewards(dealtRewards, topDealtList);
        giveRewards(takenRewards, topTakenList);
    }

    private void giveRewards(final Map<Integer, ? extends List<String>> rewards, final List<UUID> topList) {
        IntStream.range(0, topList.size()).forEach(i -> {
            final UUID uuid = topList.get(i);
            final String player = Bukkit.getOfflinePlayer(uuid).getName();
            debugln("Giving reward for " + player);
            rewards.get(i + 1).stream()
                    .map(command -> PLAYER_NAME.matcher(command).replaceAll(player))
                    .map(command -> DAMAGE_POS.matcher(command).replaceAll(Integer.toString(i + 1)))
                    .forEach(command -> {
                        debugln("Place: " + i + "command:" + command);
                        server.dispatchCommand(sender, command);
                    });
        });
    }

    private void setRewards(final Map<? super Integer, ? super List<String>> rewards, final String path) {
        Objects.requireNonNull(config.getConfigurationSection(mobname + path)).getKeys(false)
                .forEach(place -> rewards.put(
                        Integer.parseInt(place),
                        new ArrayList<>(config.getStringList(mobname + path + "." + place + ".rewards"))));
    }
}
