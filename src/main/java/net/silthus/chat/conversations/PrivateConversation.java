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

import lombok.EqualsAndHashCode;
import net.kyori.adventure.text.Component;
import net.silthus.chat.*;
import net.silthus.chat.config.PrivateChatConfig;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class PrivateConversation extends AbstractConversation {

    PrivateConversation(PrivateChatConfig config, Chatter... chatters) {
        super(Arrays.stream(chatters).map(Identity::getName).collect(Collectors.joining(",")));
        setDisplayName(config.name());
        setFormat(config.format());
        addTargets(List.of(chatters));
        if (config.global())
            addTarget(SChat.instance().getBungeecord());
    }

    PrivateConversation(UUID id, String name, Component displayName, Collection<ChatTarget> targets) {
        super(id, name);
        setDisplayName(displayName);
        addTargets(targets);
    }

    @Override
    protected void processMessage(Message message) {
        getTargets().stream()
                .filter(target -> !target.getConversations().contains(this))
                .forEach(target -> target.setActiveConversation(this));
        getTargets().forEach(target -> target.sendMessage(message));
    }
}