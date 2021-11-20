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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;

@Value
@EqualsAndHashCode(of = "id")
@Builder(builderMethodName = "message", toBuilder = true)
public class Message implements Comparable<Message>, TemplateResolver {

    public static Message empty() {
        return Message.message(Component.empty()).build();
    }

    public static MessageBuilder message() {
        return new MessageBuilder(Formats.defaultFormat());
    }

    public static MessageBuilder message(String message) {
        return message(text(message));
    }

    public static MessageBuilder message(Component message) {
        return new MessageBuilder(Formats.noFormat()).text(message);
    }

    public static MessageBuilder message(ChatSource source, String message) {
        return message(source, text(message));
    }

    public static MessageBuilder message(ChatSource source, Component message) {
        return new MessageBuilder(Formats.defaultFormat()).text(message).source(source);
    }

    @Builder.Default
    UUID id = UUID.randomUUID();
    Instant timestamp = Instant.now();
    @Builder.Default
    ChatSource source = ChatSource.nil();
    @Builder.Default
    Component text = Component.empty();
    @Builder.Default
    Format format = Formats.noFormat();
    Type type;
    Conversation conversation;
    @Builder.Default
    Collection<ChatTarget> targets = new HashSet<>();
    Map<String, Template> templates;
    Component formatted;

    private Message(UUID id, ChatSource source, Component text, Format format, Type type, Conversation conversation, Collection<ChatTarget> targets, Map<String, Template> templates) {
        this.id = id;
        this.source = source;
        this.text = text;
        this.format = format;
        this.type = type;
        this.conversation = conversation;
        this.targets = targets;
        this.templates = templates;
        this.formatted = format.applyTo(this);
    }

    public MessageBuilder copy() {
        return toBuilder().id(UUID.randomUUID());
    }

    public Component formatted() {
        return formatted;
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
    public boolean canResolve(@NotNull String key) {
        return templates.containsKey(key);
    }

    @Override
    public @Nullable Template resolve(@NotNull String key) {
        return templates.get(key);
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
            this.defaultFormat = Formats.defaultFormat();
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

            return new Message(
                    getId(),
                    getSource(),
                    getText(),
                    getFormat(),
                    type,
                    conversation,
                    getTargets(),
                    getTemplates()
            );
        }

        public Message send() {
            return build().send();
        }

        private Map<String, Template> getTemplates() {
            final HashMap<String, Template> templates = new HashMap<>();
            templates.putAll(playerTemplate("sender_world", player -> player.getWorld().getName()));
            templates.putAll(playerTemplate("player_world", player -> player.getWorld().getName()));
            templates.putAll(playerTemplate("player_name", HumanEntity::getName));
            templates.putAll(playerTemplate("player_display_name", Player::getDisplayName));
            templates.put("sender_name", senderName(getSource()));
            templates.put("sender_display_name", senderDisplayName(getSource()));
            templates.put("sender_vault_prefix", vaultPrefixTemplate(getSource()));
            templates.put("sender_vault_suffix", vaultSuffixTemplate(getSource()));
            templates.put("channel_name", channelTemplate(conversation));
            templates.put("message", Template.template("message", getText()));
            return templates;
        }

        private Map<String, Template> playerTemplate(String key, Function<Player, String> value) {
            if (!source$set) return Map.of();
            final Player player = Bukkit.getPlayer(source$value.getUniqueId());
            if (player == null) return Map.of();
            return Map.of(key, Template.template(key, value.apply(player)));
        }

        private Template senderName(ChatSource source) {
            if (source == null) {
                return Template.template("sender_name", Component.empty());
            }
            return Template.template("sender_name", source.getName());
        }

        private Template senderDisplayName(ChatSource source) {
            if (source == null) {
                return Template.template("sender_display_name", Component.empty());
            }
            return Template.template("sender_display_name", source.getDisplayName());
        }

        private Template channelTemplate(Conversation conversation) {
            if (conversation != null) {
                return Template.template("channel_name", conversation.getDisplayName());
            }
            return Template.template("channel_name", Component.empty());
        }

        private Template vaultPrefixTemplate(ChatSource source) {
            return Template.template("sender_vault_prefix", MiniMessage.miniMessage().serialize(SChat.instance().getVaultProvider().getPrefix(source)));
        }

        private Template vaultSuffixTemplate(ChatSource source) {
            return Template.template("sender_vault_suffix", MiniMessage.miniMessage().serialize(SChat.instance().getVaultProvider().getSuffix(source)));
        }

        private void checkForDirectConversation() {
            if (sourceIsChatter() && chatterTargeted()) {
                to(createDirectConversation((Chatter) source$value, firstTargetOrNull()));
            }
        }

        @Nullable
        private Chatter firstTargetOrNull() {
            return targets$value.stream()
                    .filter(target -> target instanceof Chatter)
                    .map(target -> (Chatter) target)
                    .findFirst().orElse(null);
        }

        @Nullable
        private Conversation createDirectConversation(Chatter source, Chatter target) {
            if (source == null) return null;
            if (target == null) return null;
            return Conversation.privateConversation(source, target);
        }

        private boolean sourceIsChatter() {
            return source$value instanceof Chatter;
        }

        private boolean chatterTargeted() {
            return conversation == null && targetsNotEmpty() && targets$value.stream().allMatch(target -> target instanceof Chatter);
        }

        private boolean targetsNotEmpty() {
            return targets$set && targets$value.size() > 0;
        }

        private Component getText() {
            return text$set ? text$value : $default$text();
        }

        private Format getFormat() {
            Format format = conversation != null ? conversation.getFormat() : defaultFormat;
            if (format$set) format = format$value;
            return format;
        }

        private Collection<ChatTarget> getTargets() {
            return targets$set ? targets$value : $default$targets();
        }

        private ChatSource getSource() {
            return source$set ? source$value : $default$source();
        }

        private UUID getId() {
            return id$set ? id$value : $default$id();
        }
    }

    public enum Type {
        SYSTEM,
        CONVERSATION;

        public static Type DEFAULT = SYSTEM;
    }
}
