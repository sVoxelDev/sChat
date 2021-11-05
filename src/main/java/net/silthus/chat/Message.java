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

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import net.kyori.adventure.text.Component;
import net.silthus.chat.targets.Channel;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
@EqualsAndHashCode(of = "id")
@Builder(builderMethodName = "message", toBuilder = true)
public class Message implements Comparable<Message> {

    public static MessageBuilder message() {
        return new MessageBuilder(Format.defaultFormat());
    }

    public static MessageBuilder message(String message) {
        return message(Component.text(message));
    }

    public static MessageBuilder message(Component message) {
        return new MessageBuilder(Format.noFormat()).text(message);
    }

    public static MessageBuilder message(ChatSource source, String message) {
        return new MessageBuilder(Format.defaultFormat()).text(message).source(source);
    }

    UUID id = UUID.randomUUID();
    Instant timestamp = Instant.now();
    Message parent;
    @Builder.Default
    ChatSource source = ChatSource.nil();
    @Builder.Default
    Component text = Component.empty();
    @Builder.Default
    Format format = Format.noFormat();
    Channel channel;
    @Builder.Default
    Collection<ChatTarget> targets = new HashSet<>();

    public MessageBuilder copy() {
        return toBuilder().parent(this);
    }

    public Component formatted() {
        return format.applyTo(this);
    }

    @Override
    public int compareTo(@NotNull Message o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }

    public static class MessageBuilder {

        private final Format defaultFormat;

        private MessageBuilder(Format defaultFormat) {
            this.defaultFormat = defaultFormat;
        }

        private MessageBuilder() {
            this.defaultFormat = Format.defaultFormat();
        }

        public MessageBuilder from(@NonNull ChatSource source) {
            return source(source);
        }

        public MessageBuilder to(@NonNull Channel channel) {
            return channel(channel).to((ChatTarget) channel);
        }

        public MessageBuilder to(@NonNull ChatTarget... targets) {
            return targets(Stream.concat(targets$set ? targets$value.stream() : Stream.empty(), Arrays.stream(targets)).collect(Collectors.toSet()));
        }

        public MessageBuilder to(@NonNull Collection<ChatTarget> targets) {
            HashSet<ChatTarget> chatTargets = new HashSet<>(targets);
            if (this.targets$set)
                chatTargets.addAll(this.targets$value);
            return targets(chatTargets);
        }

        public MessageBuilder text(@NonNull Component component) {
            this.text$value = component;
            this.text$set = true;
            return this;
        }

        public MessageBuilder text(@NonNull String text) {
            return text(Component.text(text));
        }

        public MessageBuilder targets(@NonNull ChatTarget... targets) {
            return targets(Arrays.asList(targets));
        }

        public MessageBuilder targets(@NonNull Collection<ChatTarget> targets) {
            this.targets$value = Set.copyOf(targets);
            this.targets$set = true;
            return this;
        }

        private MessageBuilder parent(Message message) {
            this.parent = message;
            return this;
        }

        public Message build() {
            Format format = channel != null ? channel.getConfig().format() : defaultFormat;
            if (format$set) format = format$value;
            ChatSource source = source$set ? source$value : $default$source();
            Component text = text$set ? text$value : $default$text();
            Collection<ChatTarget> targets = targets$set ? targets$value : $default$targets();

            return new Message(parent, source, text, format, channel, targets);
        }

        public Message send() {
            Message message = build();
            message.getTargets().forEach(target -> target.sendMessage(message));
            return message;
        }
    }
}
