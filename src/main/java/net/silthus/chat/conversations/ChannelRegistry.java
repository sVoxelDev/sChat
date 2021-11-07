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
import net.silthus.chat.Constants;
import net.silthus.chat.SChat;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.config.PluginConfig;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Log(topic = Constants.PLUGIN_NAME)
@AllArgsConstructor
public final class ChannelRegistry implements Iterable<Channel> {

    private final SChat plugin;
    private final Map<String, Channel> channels = Collections.synchronizedMap(new HashMap<>());

    @Override
    public @NotNull Iterator<Channel> iterator() {
        return getChannels().iterator();
    }

    public List<Channel> getChannels() {
        return List.copyOf(channels.values());
    }

    public Optional<Channel> get(String identifier) {
        if (identifier == null) return Optional.empty();
        return Optional.ofNullable(channels.get(identifier.toLowerCase()));
    }

    public Channel getOrCreate(@NonNull String identifier) {
        return channels.computeIfAbsent(identifier.toLowerCase(), Channel::new);
    }

    public Channel getOrCreate(@NonNull String identifier, ChannelConfig config) {
        return channels.computeIfAbsent(identifier.toLowerCase(), s -> new Channel(s, config));
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

    public void clear() {
        channels.clear();
    }

    public void load(@NonNull PluginConfig config) {
        clear();
        loadChannels(config);
    }

    private void loadChannels(@NonNull PluginConfig config) {
        config.channels().entrySet().stream()
                .map(entry -> Channel.channel(entry.getKey(), entry.getValue()))
                .forEach(this::add);
    }

    public void add(@NonNull Channel channel) {
        this.channels.put(channel.getName(), channel);
    }

    public boolean remove(@NonNull Channel channel) {
        return this.channels.remove(channel.getName(), channel);
    }

    public Channel remove(String identifier) {
        if (identifier == null) return null;
        return this.channels.remove(identifier.toLowerCase());
    }
}
