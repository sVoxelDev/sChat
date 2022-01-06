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

package net.silthus.schat.ui;

import java.util.List;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;

import static net.silthus.schat.ui.TabbedChannelRenderer.MAX_LINES;

final class ChatterViewModel {
    private final Chatter chatter;

    ChatterViewModel(Chatter chatter) {
        this.chatter = chatter;
    }

    public List<ChannelViewModel> getChannels() {
        return chatter.getChannels().stream()
            .map(ChannelViewModel::new)
            .sorted()
            .toList();
    }

    public List<MessageViewModel> getMessages() {
        final List<Message> messages = chatter.getMessages();
        return messages.stream()
            .map(MessageViewModel::new)
            .filter(this::isIncludedMessage)
            .sorted()
            .skip(Math.max(0, messages.size() - MAX_LINES))
            .toList();
    }

    public boolean isActiveChannel(ChannelViewModel channel) {
        return chatter.isActiveChannel(channel.getChannel());
    }

    public boolean isIncludedMessage(MessageViewModel message) {
        if (message.isExcluded()) return false;
        if (message.isSystemMessage()) return true;
        return chatter.getActiveChannel()
            .map(channel -> channel.getMessages().contains(message.getMessage()))
            .orElse(true);
    }
}
