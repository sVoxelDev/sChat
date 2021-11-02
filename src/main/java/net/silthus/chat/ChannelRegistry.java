package net.silthus.chat;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.silthus.chat.config.PluginConfig;

import java.util.*;

@Log(topic = Constants.PLUGIN_NAME)
@AllArgsConstructor
public class ChannelRegistry implements Iterable<Channel> {

    private final SChat plugin;
    private final Map<String, Channel> channels = Collections.synchronizedMap(new HashMap<>());

    @Override
    public Iterator<Channel> iterator() {
        return getChannels().iterator();
    }

    public List<Channel> getChannels() {
        return List.copyOf(channels.values());
    }

    public Optional<Channel> get(String identifier) {
        if (identifier == null) return Optional.empty();
        return Optional.ofNullable(channels.get(identifier.toLowerCase()));
    }

    public int size() {
        return channels.size();
    }

    public boolean contains(String identifier) {
        if (identifier == null) return false;
        return channels.containsKey(identifier.toLowerCase());
    }

    public boolean contains(Channel channel) {
        return channels.containsValue(channel);
    }

    public void load(@NonNull PluginConfig config) {
        channels.clear();
        loadChannels(config);
    }

    private void loadChannels(@NonNull PluginConfig config) {
        config.channels().entrySet().stream()
                .map(entry -> Channel.channel(entry.getKey(), entry.getValue()))
                .forEach(this::add);
    }

    public void add(@NonNull Channel channel) {
        this.channels.put(channel.getIdentifier(), channel);
    }

    public boolean remove(@NonNull Channel channel) {
        return this.channels.remove(channel.getIdentifier(), channel);
    }

    public Channel remove(String identifier) {
        if (identifier == null) return null;
        return this.channels.remove(identifier.toLowerCase());
    }
}
