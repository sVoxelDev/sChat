/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.chat;

import lombok.NonNull;
import lombok.extern.java.Log;
import net.silthus.chat.targets.Chatter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Log(topic = Constants.PLUGIN_NAME)
public final class ChatterManager {

    private final SChat plugin;
    private final Map<String, Chatter> chatters = Collections.synchronizedMap(new HashMap<>());

    final PlayerListener playerListener;

    public ChatterManager(SChat plugin) {
        this.plugin = plugin;
        this.playerListener = new PlayerListener();
        plugin.getServer().getPluginManager().registerEvents(playerListener, plugin);
    }

    public Collection<Chatter> getChatters() {
        return List.copyOf(chatters.values());
    }

    public void autoJoinChannels(Chatter chatter) {
        plugin.getChannelRegistry().getChannels().stream()
                .filter(channel -> channel.canAutoJoin(chatter))
                .forEach(chatter::subscribe);
    }

    public Chatter getOrCreateChatter(@NonNull Player player) {
        if (chatters.containsKey(player.getUniqueId().toString()))
            return chatters.get(player.getUniqueId().toString());
        return registerChatter(new Chatter(player));
    }

    public Chatter getChatter(UUID id) {
        return chatters.get(id.toString());
    }

    public Optional<Chatter> getChatter(String playerName) {
        return chatters.values().stream()
                .filter(chatter -> chatter.getPlayer().getName().equalsIgnoreCase(playerName))
                .findFirst();
    }

    public Chatter registerChatter(@NonNull Player player) {
        return getOrCreateChatter(player);
    }

    public Chatter registerChatter(@NonNull Chatter chatter) {
        addChatterToCache(chatter);
        plugin.getServer().getPluginManager().registerEvents(chatter, plugin);
        return chatter;
    }

    private void addChatterToCache(@NotNull Chatter chatter) {
        unregisterListener(chatters.put(chatter.getIdentifier(), chatter));
    }

    public void unregisterChatter(@NonNull Player player) {
        Chatter chatter = chatters.remove(player.getUniqueId().toString());
        unregisterListener(chatter);
        unsubscribeAll(chatter);
    }

    private void unregisterListener(Chatter chatter) {
        if (chatter == null) return;
        HandlerList.unregisterAll(chatter);
    }

    private void unsubscribeAll(Chatter chatter) {
        if (chatter == null) return;
        chatter.getSubscriptions().forEach(channelSubscription -> chatter.unsubscribe(channelSubscription.channel()));
    }

    class PlayerListener implements Listener {


        @EventHandler(ignoreCancelled = true)
        public void onJoin(PlayerJoinEvent event) {
            autoJoinChannels(registerChatter(event.getPlayer()));
        }

        @EventHandler(ignoreCancelled = true)
        public void onQuit(PlayerQuitEvent event) {
            unregisterChatter(event.getPlayer());
        }
    }
}
