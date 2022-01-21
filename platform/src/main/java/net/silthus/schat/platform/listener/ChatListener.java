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

package net.silthus.schat.platform.listener;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Messenger;
import net.silthus.schat.usecases.OnChat;

import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.platform.locale.Messages.CANNOT_CHAT_NO_ACTIVE_CHANNEL;

@Setter
@Accessors(fluent = true)
public class ChatListener implements OnChat {

    private ChatterProvider chatterProvider = ChatterProvider.nil();
    private Messenger messenger = Messenger.nil();

    protected final void onChat(@NonNull UUID chatterId, @NonNull Component text) {
        final Chatter chatter = chatterProvider.get(chatterId);
        try {
            onChat(chatter, text);
        } catch (NoActiveChannel e) {
            CANNOT_CHAT_NO_ACTIVE_CHANNEL.send(chatter);
        }
    }

    @Override
    public final Message onChat(@NonNull Chatter chatter, @NonNull Component text) throws NoActiveChannel {
        final Optional<Channel> channel = chatter.getActiveChannel();
        if (channel.isEmpty())
            throw new NoActiveChannel();
        return message(text).source(chatter).to(channel.get()).type(Message.Type.CHAT).send(messenger);
    }
}
