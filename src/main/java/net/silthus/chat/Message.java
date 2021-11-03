package net.silthus.chat;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
@Builder(builderMethodName = "message", toBuilder = true)
public class Message {

    public static MessageBuilder message() {
        return new MessageBuilder(Format.defaultFormat());
    }

    public static MessageBuilder message(String message) {
        return new MessageBuilder(Format.noFormat()).text(message);
    }

    public static MessageBuilder message(ChatSource source, String message) {
        return new MessageBuilder(Format.defaultFormat()).text(message).source(source);
    }

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
