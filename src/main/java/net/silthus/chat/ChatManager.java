package net.silthus.chat;

import lombok.NonNull;
import lombok.extern.java.Log;
import net.silthus.chat.config.PluginConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.*;

@Log(topic = Constants.PLUGIN_NAME)
public class ChatManager {

    private final SChat plugin;
    private final Set<Channel> channels = new HashSet<>();
    private final Map<UUID, Chatter> chatters = new HashMap<>();

    public ChatManager(SChat plugin) {
        this.plugin = plugin;
    }

    public List<Channel> getChannels() {
        return List.copyOf(channels);
    }

    public Optional<Channel> getChannel(String alias) {
        return channels.stream()
                .filter(channel -> channel.getAlias().equals(alias))
                .findFirst();
    }

    public Collection<Chatter> getChatters() {
        return List.copyOf(chatters.values());
    }

    public Chatter getChatter(@NonNull Player player) {
        return chatters.getOrDefault(player.getUniqueId(), registerChatter(player));
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
        log.info("Loaded " + this.channels.size() + " channels.");
    }

    private void loadChannelFromConfig(String channelKey, ConfigurationSection config) {
        this.channels.add(new Channel(channelKey, config));
    }

    public Chatter registerChatter(Player player) {
        Chatter chatter = Chatter.of(player);
        plugin.getServer().getPluginManager().registerEvents(chatter, plugin);
        chatters.put(chatter.getUniqueId(), chatter);
        return chatter;
    }

    public void unregisterChatter(Player player) {
        Chatter chatter = chatters.remove(player.getUniqueId());
        if (chatter != null)
            HandlerList.unregisterAll(chatter);
    }
}
