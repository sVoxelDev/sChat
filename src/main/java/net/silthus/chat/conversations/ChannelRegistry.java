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

package net.silthus.chat.conversations;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.kyori.adventure.text.Component;
import net.silthus.chat.Constants;
import net.silthus.chat.Conversation;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.config.PluginConfig;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Log(topic = Constants.PLUGIN_NAME)
@AllArgsConstructor
public class ChannelRegistry implements Iterable<Channel> {

    private final Map<String, Channel> channels = new HashMap<>();

    @Override
    public @NotNull Iterator<Channel> iterator() {
        return getChannels().iterator();
    }

    public List<Channel> getChannels() {
        return List.copyOf(channels.values());
    }

    public Channel get(UUID id) {
        return channels.values().stream()
                .filter(channel -> channel.getUniqueId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Channel get(@NonNull String identifier) {
        return channels.get(identifier.toLowerCase());
    }

    public Channel getOrCreate(String identifier, ChannelConfig config) {
        return Optional.ofNullable(get(identifier)).orElseGet(() -> createAndRegister(identifier, config));
    }

    public Optional<Channel> find(String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(channels.get(name.toLowerCase()))
                .or(() -> channels.values().stream()
                        .filter(channel -> channel.getDisplayName().equals(Component.text(name)))
                        .findFirst()
                ).or(() -> {
                    try {
                        return Optional.ofNullable(get(UUID.fromString(name)));
                    } catch (Exception ignored) {
                        return Optional.empty();
                    }
                });
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
        loadChannels(config);
    }

    public Channel register(@NonNull final Channel channel) {
        return channels.merge(channel.getName().toLowerCase(), channel, (existing, newChannel) -> {
            existing.setConfig(newChannel.getConfig());
            return existing;
        });
    }

    public void clear() {
        getChannels().forEach(Conversation::close);
        channels.clear();
    }

    public void remove(@NonNull Channel channel) {
        remove(channel.getName());
    }

    public Channel remove(String identifier) {
        if (identifier == null) return null;
        final Channel channel = this.channels.remove(identifier.toLowerCase());
        if (channel != null) channel.close();
        return channel;
    }

    private void loadChannels(@NonNull PluginConfig config) {
        for (Map.Entry<String, ChannelConfig> entry : config.channels().entrySet()) {
            final String key = entry.getKey().toLowerCase();
            if (contains(key)) {
                get(key).setConfig(entry.getValue());
            } else {
                createAndRegister(key, entry.getValue());
            }
        }
        final Set<String> channelsToRemove = new HashSet<>(channels.keySet());
        channelsToRemove.removeAll(config.channels().keySet());
        channelsToRemove.forEach(this::remove);
    }

    private Channel createAndRegister(@NonNull String identifier, @NonNull ChannelConfig config) {
        return register(Channel.createChannel(identifier, config));
    }
}
