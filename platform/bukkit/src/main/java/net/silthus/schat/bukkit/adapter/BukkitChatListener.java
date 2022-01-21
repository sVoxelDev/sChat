package net.silthus.schat.bukkit.adapter;

import net.silthus.schat.platform.listener.ChatListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static net.kyori.adventure.text.Component.text;

public final class BukkitChatListener extends ChatListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        onChat(event.getPlayer().getUniqueId(), text(event.getMessage()));
        event.setCancelled(true);
    }
}
