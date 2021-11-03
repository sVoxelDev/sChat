package net.silthus.chat;

import lombok.Builder;
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
        return new MessageBuilder();
    }

    public static MessageBuilder message(String message) {
        return message().text(message);
    }

    public static MessageBuilder message(ChatSource source, String message) {
        return message(message).source(source);
    }

    Message parent;
    @Builder.Default
    ChatSource source = ChatSource.nil();
    @Builder.Default
    Component text = Component.empty();
    @Builder.Default
    Format format = Format.defaultFormat();
    Channel channel;
    @Builder.Default
    Collection<ChatTarget> targets = new HashSet<>();

    public MessageBuilder copy() {
        return toBuilder().parent(this);
    }

    public Component formattedMessage() {
        return format.applyTo(this);
    }

    public static class MessageBuilder {

        public MessageBuilder from(ChatSource source) {
            return source(source);
        }

        public MessageBuilder to(Channel channel) {
            return channel(channel).to((ChatTarget) channel);
        }

        public MessageBuilder to(ChatTarget... targets) {
            return targets(Stream.concat(targets$set ? targets$value.stream() : Stream.empty(), Arrays.stream(targets)).collect(Collectors.toSet()));
        }

        public MessageBuilder to(Collection<ChatTarget> targets) {
            HashSet<ChatTarget> chatTargets = new HashSet<>(targets);
            if (this.targets$set)
                chatTargets.addAll(this.targets$value);
            return targets(chatTargets);
        }

        public MessageBuilder text(Component component) {
            this.text$value = component;
            this.text$set = true;
            return this;
        }

        public MessageBuilder text(String text) {
            return text(Component.text(text));
        }

        public MessageBuilder targets(ChatTarget... targets) {
            return targets(Arrays.asList(targets));
        }

        public MessageBuilder targets(Collection<ChatTarget> targets) {
            this.targets$value = Set.copyOf(targets);
            this.targets$set = true;
            return this;
        }

        private MessageBuilder parent(Message message) {
            this.parent = message;
            return this;
        }

        public Message send() {
            Message message = build();
            message.getTargets().forEach(target -> target.sendMessage(message));
            return message;
        }
    }
}
