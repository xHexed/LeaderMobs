package com.github.xhexed.leadermobs.util;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LegacyMessageSender implements MessageSender {
    private BukkitAudiences audiences;

    public LegacyMessageSender(Plugin plugin) {
        audiences = BukkitAudiences.create(plugin);
    }

    @Override
    public void sendMessage(Player player, String message) {
        audiences.player(player).sendMessage(getComponent(message));
    }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        audiences.sender(sender).sendMessage(getComponent(message));
    }

    @Override
    public void sendActionbar(Player player, String message) {
        audiences.player(player).sendActionBar(getComponent(message));
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle, long fadeIn, long stay, long fadeOut) {
        audiences.player(player).showTitle(Title.title(
                getComponent(title),
                getComponent(subTitle),
                Title.Times.times(
                        Ticks.duration(fadeIn),
                        Ticks.duration(stay),
                        Ticks.duration(fadeOut)
                )
        ));
    }

    private Component getComponent(String input) {
        if (input.contains("§")) {
            return LegacyComponentSerializer.legacySection().deserialize(input);
        }
        else if (input.contains("&")) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(input);
        }
        else {
            return MiniMessage.miniMessage().deserialize(input);
        }
    }
}
