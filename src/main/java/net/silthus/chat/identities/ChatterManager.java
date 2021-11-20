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

package net.silthus.chat.identities;

import lombok.NonNull;
import lombok.extern.java.Log;
import net.silthus.chat.Chatter;
import net.silthus.chat.Constants;
import net.silthus.chat.Identity;
import net.silthus.chat.SChat;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

@Log(topic = Constants.PLUGIN_NAME)
public final class ChatterManager {

    private final SChat plugin;
    private final Map<UUID, Chatter> chatters = new HashMap<>();
    private final Map<String, UUID> senderIds = new HashMap<>();

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
                .filter(chatter::canAutoJoin)
                .forEach(chatter::subscribe);
    }

    public Chatter registerChatter(@NonNull OfflinePlayer player) {
        return getOrCreateChatter(player);
    }

    public Chatter getOrCreateChatter(@NonNull Player player) {
        return getOrCreateChatter((OfflinePlayer) player);
    }

    public Chatter getOrCreateChatter(@NonNull OfflinePlayer player) {
        if (chatters.containsKey(player.getUniqueId()))
            return chatters.get(player.getUniqueId());
        return registerChatter(new PlayerChatter(player));
    }

    public Chatter getOrCreateChatter(@NonNull Identity identity) {
        if (chatters.containsKey(identity.getUniqueId()))
            return chatters.get(identity.getUniqueId());
        return registerChatter(new PlayerChatter(identity));
    }

    public Chatter getOrCreateChatter(@NonNull CommandSender sender) {
        if (sender instanceof Player)
            return getOrCreateChatter((Player) sender);

        final UUID uuid = getSenderId(sender);
        if (chatters.containsKey(uuid))
            return chatters.get(uuid);

        final CommandSendingChatter chatter = new CommandSendingChatter(uuid, sender);
        senderIds.put(chatter.getName(), uuid);
        return registerChatter(chatter);
    }

    public Chatter getChatter(UUID id) {
        return chatters.get(id);
    }

    public Optional<Chatter> getChatter(String name) {
        return chatters.values().stream()
                .filter(chatter -> chatter.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public void removeAllChatters() {
        List.copyOf(chatters.values())
                .forEach(this::removeChatter);
    }

    public void removeChatter(@NonNull Player player) {
        removeChatter(chatters.remove(player.getUniqueId()));
    }

    public void removeChatter(Chatter chatter) {
        if (chatter == null) return;
        chatter.save();
        chatters.remove(chatter.getUniqueId());
        HandlerList.unregisterAll(chatter);
        chatter.getConversations().forEach(chatter::unsubscribe);
    }

    Chatter registerChatter(@NonNull Chatter chatter) {
        if (chatters.containsKey(chatter.getUniqueId())) return getChatter(chatter.getUniqueId());
        chatters.put(chatter.getUniqueId(), chatter);
        chatter.load();
        plugin.getServer().getPluginManager().registerEvents(chatter, plugin);
        plugin.getBungeecord().sendChatter(chatter);
        return chatter;
    }

    private @NonNull UUID getSenderId(CommandSender sender) {
        if (sender instanceof Entity)
            return ((Entity) sender).getUniqueId();
        return senderIds.getOrDefault(sender.getName(), UUID.randomUUID());
    }

    class PlayerListener implements Listener {

        @EventHandler(ignoreCancelled = true)
        public void onJoin(PlayerJoinEvent event) {
            autoJoinChannels(registerChatter(event.getPlayer()));
        }

        @EventHandler(ignoreCancelled = true)
        public void onQuit(PlayerQuitEvent event) {
            getOrCreateChatter(event.getPlayer()).save();
        }
    }
}
