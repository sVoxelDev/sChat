package net.silthus.chat;

import lombok.NonNull;
import net.silthus.chat.config.PluginConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class ChannelManager implements Listener {

    private final SChat plugin;
    private final Set<Channel> channels = new HashSet<>();
    private final Map<UUID, Chatter> chatters = new HashMap<>();

    public ChannelManager(SChat plugin) {
        this.plugin = plugin;
    }

    public Collection<Channel> getChannels() {
        return List.copyOf(channels);
    }

    public Collection<Chatter> getChatters() {
        return List.copyOf(chatters.values());
    }

    public void load(@NonNull PluginConfig config) {
        channels.clear();
        loadChannelsFromConfig(config.getChannels());
    }

    private void loadChannelsFromConfig(ConfigurationSection channels) {
        if (channels == null) return;
        channels.getKeys(false).forEach(channelKey ->
                loadChannelFromConfig(channelKey, channels.getConfigurationSection(channelKey))
        );
    }

    private void loadChannelFromConfig(String channelKey, ConfigurationSection config) {
        this.channels.add(new Channel(channelKey, config));
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        registerChatter(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        unregisterChatter(event.getPlayer());
    }

    private void registerChatter(Player player) {
        Chatter chatter = Chatter.of(player);
        plugin.getServer().getPluginManager().registerEvents(chatter, plugin);
        chatters.put(chatter.getUniqueId(), chatter);
    }

    private void unregisterChatter(Player player) {
        Chatter chatter = chatters.remove(player.getUniqueId());
        if (chatter != null)
            HandlerList.unregisterAll(chatter);
    }
}
