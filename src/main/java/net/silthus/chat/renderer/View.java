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

package net.silthus.chat.renderer;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.silthus.chat.*;
import net.silthus.chat.conversations.DirectConversation;
import net.silthus.chat.identities.Chatter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

@Data
@Accessors(fluent = true)
public class View {

    private final Chatter chatter;
    private final Map<UUID, Set<Message>> unreadMessages = new HashMap<>();
    private MessageRenderer renderer = MessageRenderer.TABBED;

    public View(@NonNull Chatter chatter, @NonNull MessageRenderer renderer) {
        this.chatter = chatter;
        this.renderer = renderer;
    }

    public View(Chatter chatter) {
        this.chatter = chatter;
    }

    public void sendTo(Player player) {
        SChat.instance().getAudiences().player(player).sendMessage(senderAudience(), render(), MessageType.SYSTEM);
    }

    private Component render() {
        return appendMessageTag(renderer.render(this));
    }

    public List<Message> messages() {
        return Stream.concat(getConversationMessages(), getSystemMessages())
                .distinct()
                .sorted()
                .toList();
    }

    public Optional<Message> lastMessage() {
        return messages().stream().reduce((message, message2) -> message2);
    }

    public List<Conversation> conversations() {
        return chatter().getConversations().stream()
                .sorted()
                .toList();
    }

    public Optional<Conversation> activeConversation() {
        return Optional.ofNullable(chatter().getActiveConversation());
    }

    public int unreadMessageCount(Conversation conversation) {
        if (conversation == null) return 0;
        return chatter().getUnreadMessages(conversation).size();
    }

    private Stream<Message> getSystemMessages() {
        if (isDirectConversation())
            return Stream.empty();
        return chatter().getReceivedMessages().stream()
                .filter(msg -> msg.getType() == Message.Type.SYSTEM);
    }

    private boolean isDirectConversation() {
        return activeConversation()
                .map(conversation -> conversation instanceof DirectConversation)
                .orElse(false);
    }

    private Stream<Message> getConversationMessages() {
        return activeConversation()
                .map(ChatTarget::getReceivedMessages)
                .stream()
                .flatMap(Collection::stream);
    }

    private Component appendMessageTag(Component message) {
        return message.append(Component.storageNBT()
                .nbtPath(Constants.NBT_IS_SCHAT_MESSAGE.asString())
                .storage(Constants.NBT_IS_SCHAT_MESSAGE));
    }

    private net.kyori.adventure.identity.Identity senderAudience() {
        return lastMessage()
                .map(Message::getSource)
                .map(net.silthus.chat.Identity::getUniqueId)
                .map(Identity::identity)
                .orElse(Identity.nil());
    }
}