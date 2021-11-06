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

import net.kyori.adventure.text.Component;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Message;

public class DirectConversation extends AbstractConversation {

    public DirectConversation(ChatTarget target1, ChatTarget target2) {
        super(target1.getName() + "#" + target2.getName());
        setDisplayName(Component.text("<partner_name>"));
        addTarget(target1);
        addTarget(target2);
    }

    @Override
    public void sendMessage(Message message) {
        addReceivedMessage(message);
        getTargets().stream()
                .filter(target -> !target.getConversations().contains(this))
                .forEach(target -> target.setActiveConversation(this));
        message.copy()
                .conversation(this)
                .targets(getTargets())
                .send();
    }
}