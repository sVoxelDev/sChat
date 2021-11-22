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

package net.silthus.chat.identities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.silthus.chat.*;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.renderer.View;

import java.util.Optional;
import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractChatter extends AbstractChatTarget implements Chatter {

    private static final MessageRenderer RENDERER = MessageRenderer.TABBED;
    @Setter
    private View view = new View(this, AbstractChatter.RENDERER);

    public AbstractChatter(UUID id, String name) {
        super(id, name);
    }

    public abstract Optional<Audience> getAudience();

    @Override
    public boolean deleteMessage(Message message) {
        final boolean deleted = super.deleteMessage(message);
        if (deleted)
            updateView();
        return deleted;
    }

    @Override
    public void updateView() {
        getAudience().ifPresent(view::sendTo);
    }

    @Override
    protected void processMessage(Message message) {
        getAudience().ifPresentOrElse(
                view::sendTo,
                () -> SChat.instance().getBungeecord().sendMessage(message)
        );
    }

    @Override
    public void join(Channel channel) throws AccessDeniedException {
        if (!canJoin(channel))
            throw new AccessDeniedException("You don't have permission to join the channel: " + channel.getName());
        setActiveConversation(channel);
    }

    @Override
    public boolean canJoin(Channel channel) {
        return channel.canJoin(this);
    }

    @Override
    public boolean canAutoJoin(Channel channel) {
        return channel.canAutoJoin(this);
    }

    @Override
    public boolean canLeave(Conversation conversation) {
        if (conversation instanceof Channel) {
            return ((Channel) conversation).canLeave(this);
        }
        return true;
    }

    @Override
    public boolean canSendMessage(Channel channel) {
        return channel.canSendMessage(this);
    }
}
