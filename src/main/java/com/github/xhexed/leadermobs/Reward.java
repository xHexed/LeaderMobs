package com.github.xhexed.leadermobs;

import com.github.xhexed.leadermobs.data.MobDamageInfo;
import com.github.xhexed.leadermobs.utils.Pair;
import com.github.xhexed.leadermobs.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static com.github.xhexed.leadermobs.utils.Utils.*;

public class Reward {
    private final String mobName;
    private final FileConfiguration config = LeaderMobs.rewards;

    public Reward(final String mobName, final MobDamageInfo info) {
        this.mobName = mobName;
        if (!config.contains(mobName)) {
            return;
        }
        giveRewards(getRewards(".dealt"), info.getTopDamageDealt(), info.getTotalDamageDealt(), DAMAGE_DEALT, DAMAGE_DEALT_PERCENTAGE);
        giveRewards(getRewards(".taken"), info.getTopDamageTaken(), info.getTotalDamageTaken(), DAMAGE_TAKEN, DAMAGE_TAKEN_PERCENTAGE);
    }

    private void giveRewards(final List<List<String>> rewards,
                             final List<Pair<Double, UUID>> topList,
                             final double totalDamage,
                             final Pattern damageFormat,
                             final Pattern percentageFormat) {
        IntStream.range(0, Math.min(topList.size(), rewards.size())).forEach(i -> {
            final Pair<Double, UUID> info = topList.get(i);
            final UUID uuid = info.getValue();
            final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            rewards.get(i).stream()
                    .map(command -> PLAYER_NAME.matcher(command).replaceAll(player.getName()))
                    .map(command -> DAMAGE_POS.matcher(command).replaceAll(Integer.toString(i + 1)))
                    .map(command -> damageFormat.matcher(command).replaceAll(DOUBLE_FORMAT.format(info.getKey())))
                    .map(command -> percentageFormat.matcher(command).replaceAll(DOUBLE_FORMAT.format(getPercentage(info.getKey(), totalDamage))))
                    .map(command -> Utils.replacePlaceholder(player, command))
                    .forEach(command -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
        });
    }

    private List<List<String>> getRewards(final String path) {
        final List<List<String>> rewards = new ArrayList<>();
        final ConfigurationSection section = config.getConfigurationSection(mobName + path);
        if (section == null) return rewards;
        section.getKeys(false)
                .forEach(place -> rewards.addAll(
                        Collections.singleton(config.getStringList(mobName + path + "." + place + ".rewards"))));
        return rewards;
    }
}
