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
    private final Map<Integer, String> data;
    private final String mobname;

    public Reward(final String mobname, final Map<Integer, String> data) {
        this.data      = data;
        this.mobname = mobname;
        start();
    }

    @Override
    public final void start() {
        final LeaderMobs instance = LeaderMobs.getInstance();
        final Map<Integer, List<String>> rewards_final = new HashMap<>();
        if (data == null) return;
        debug("Start calculating rewards for mob: " + mobname);
        data.forEach((key, value) -> debug("Data: Place " + key + ", name: " + value));

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "rewards.yml"));
        if (!config.contains(mobname)) {
            debug("Rewards for boss: " + mobname + " not found...");
            interrupt();
            return;
        }
        setRewards(rewards_final, config);

        debug("Starting calculating final rewards for boss: " + mobname);
        rewards_final.forEach((key, value) -> {
            value.stream().map(reward_command -> "Place: " + key + ", commands: " + reward_command).forEach(Utils::debug);
        });
        final Server server = Bukkit.getServer();
        final CommandSender sender = Bukkit.getConsoleSender();
        rewards_final.forEach((place, value) -> {
            if (data.get(place) != null) {
                value.stream()
                        .map(command -> PLAYER_NAME.matcher(command).replaceAll(Objects.requireNonNull(Bukkit.getPlayer(data.get(place))).getName()))
                        .map(command -> DAMAGE_POS.matcher(command).replaceAll(place.toString()))
                        .forEach(command -> {
                            debug("Pos to exec reward: " + place + " - " + data.get(place));
                            server.dispatchCommand(sender, command);
                            debug("Executed reward command: " + command);
                        });
            }
        });
    }

    private void setRewards(final Map<? super Integer, ? super List<String>> rewards_final, final ConfigurationSection config) {
        Objects.requireNonNull(config.getConfigurationSection(mobname)).getKeys(false)
                .forEach(reward_places ->
                                 rewards_final.put(
                                         Integer.parseInt(reward_places),
                                         new ArrayList<>(config.getStringList(mobname + ".dealt." + reward_places + ".rewards"))));
    }
}
