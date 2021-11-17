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
import net.silthus.chat.identities.Chatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;

@Value
@EqualsAndHashCode(of = "id")
@Builder(builderMethodName = "message", toBuilder = true)
public class Message implements Comparable<Message> {

    public static Message empty() {
        return Message.message(Component.empty()).build();
    }

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

    @Builder.Default
    UUID id = UUID.randomUUID();
    Instant timestamp = Instant.now();
    @Builder.Default
    ChatSource source = ChatSource.nil();
    @Builder.Default
    Component text = Component.empty();
    @Builder.Default
    Format format = Format.noFormat();
    Type type;
    Conversation conversation;
    @Builder.Default
    Collection<ChatTarget> targets = new HashSet<>();

    public MessageBuilder copy() {
        return toBuilder().id(UUID.randomUUID());
    }

    public Component formatted() {
        return format.applyTo(this);
    }

    public Type getType() {
        if (type != null) return type;
        if (conversation != null)
            return Type.CONVERSATION;
        if (source == ChatSource.NIL)
            return Type.SYSTEM;
        return Type.DEFAULT;
    }

    public Message send() {
        getTargets().forEach(target -> target.sendMessage(this));
        return this;
    }

    public void delete() {
        getTargets().forEach(target -> target.deleteMessage(this));
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

        public MessageBuilder to(Conversation conversation) {
            if (conversation == null) return this;
            return conversation(conversation).targets(conversation);
        }

        public MessageBuilder to(@NonNull ChatTarget... targets) {
            ArrayList<ChatTarget> targetList = new ArrayList<>();
            if (targets$set) targetList.addAll(targets$value);
            for (ChatTarget target : targets) {
                if (target instanceof Conversation)
                    conversation((Conversation) target);
                targetList.add(target);

            }
            return targets(targetList);
        }

        public MessageBuilder to(@NonNull Collection<ChatTarget> targets) {
            HashSet<ChatTarget> chatTargets = new HashSet<>(targets);
            if (this.targets$set)
                chatTargets.addAll(this.targets$value);
            return targets(chatTargets);
        }

        public MessageBuilder conversation(Conversation conversation) {
            this.conversation = conversation;
            return type(Type.CONVERSATION);
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

        public Message build() {
            checkForDirectConversation();

            UUID id = id$set ? id$value : $default$id();
            Format format = conversation != null ? conversation.getFormat() : defaultFormat;
            if (format$set) format = format$value;
            ChatSource source = source$set ? source$value : $default$source();
            Component text = text$set ? text$value : $default$text();
            Collection<ChatTarget> targets = targets$set ? targets$value : $default$targets();

            return new Message(id, source, text, format, type, conversation, targets);
        }

        public Message send() {
            return build().send();
        }

        private void checkForDirectConversation() {
            if (sourceIsChatTarget() && chatterTargeted()) {
                to(createDirectConversation((ChatTarget) source$value, firstTargetOrNull()));
            }
        }

        @Nullable
        private ChatTarget firstTargetOrNull() {
            return targets$value.stream().findFirst().orElse(null);
        }

        @Nullable
        private Conversation createDirectConversation(ChatTarget source, ChatTarget target) {
            if (source == null) return null;
            if (target == null) return null;
            return Conversation.direct(source, target);
        }

        private boolean sourceIsChatTarget() {
            return source$value instanceof ChatTarget;
        }

        private boolean chatterTargeted() {
            return conversation == null && targetsNotEmpty() && targets$value.stream().allMatch(target -> target instanceof Chatter);
        }

        private boolean targetsNotEmpty() {
            return targets$set && targets$value.size() > 0;
        }
    }

    public enum Type {
        SYSTEM,
        CONVERSATION;

        public static Type DEFAULT = SYSTEM;
    }
}
