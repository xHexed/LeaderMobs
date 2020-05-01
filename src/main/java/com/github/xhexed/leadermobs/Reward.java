package com.github.xhexed.leadermobs;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

import static com.github.xhexed.leadermobs.Utils.DAMAGE_POS;
import static com.github.xhexed.leadermobs.Utils.PLAYER_NAME;
import static com.github.xhexed.leadermobs.Utils.debug;

public class Reward extends Thread {
    private final List<UUID> topList;
    private final String mobname;

    public Reward(final String mobname, final List<UUID> topList) {
        this.mobname = mobname;
        this.topList = topList;
        start();
    }

    @Override
    public final void start() {
        final LeaderMobs instance = LeaderMobs.getInstance();
        final Map<Integer, List<String>> rewards = new HashMap<>();
        debug("Start calculating rewards for mob: " + mobname);

        debug("Place list: ");
        topList.forEach((uuid) -> debug(Bukkit.getOfflinePlayer(uuid).getName() + ", "));

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "rewards.yml"));
        if (!config.contains(mobname)) {
            debug("Rewards for boss: " + mobname + " not found...");
            interrupt();
            return;
        }
        setRewards(rewards, config);

        debug("Starting calculating final rewards for boss: " + mobname);
        rewards.forEach((key, value) -> value.stream().map(reward_command -> "Place: " + key + ", commands: " + reward_command).forEach(Utils::debug));
        final Server server = Bukkit.getServer();
        final CommandSender sender = Bukkit.getConsoleSender();
        rewards.forEach((place, value) -> {
            final UUID uuid = topList.get(place - 1);
            value.stream()
                    .map(command -> PLAYER_NAME.matcher(command).replaceAll(Objects.requireNonNull(Bukkit.getOfflinePlayer(uuid)).getName()))
                    .map(command -> DAMAGE_POS.matcher(command).replaceAll(place.toString()))
                    .forEach(command -> {
                        debug("Pos to exec reward: " + place + " - " + uuid);
                        server.dispatchCommand(sender, command);
                        debug("Executed reward command: " + command);
                    });
        });
    }

    private void setRewards(final Map<? super Integer, ? super List<String>> rewards, final ConfigurationSection config) {
        Objects.requireNonNull(config.getConfigurationSection(mobname + "dealt")).getKeys(false)
                .forEach(place -> rewards.put(
                        Integer.parseInt(place),
                        new ArrayList<>(config.getStringList(mobname + ".dealt." + place + ".rewards"))));
    }
}
