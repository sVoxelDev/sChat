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

package net.silthus.schat.velocity.protocolize;

import com.velocitypowered.proxy.protocol.packet.Chat;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Messenger;
import net.silthus.schat.view.View;
import net.silthus.schat.view.ViewProvider;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.message.Message.message;

public final class ChatPacketListener extends AbstractPacketListener<Chat> {

    private static final @NotNull GsonComponentSerializer SERIALIZER = GsonComponentSerializer.gson();

    private final ChatterProvider chatterProvider;
    private final ViewProvider viewProvider;
    private final Messenger messenger;

    public ChatPacketListener(ChatterProvider chatterProvider, ViewProvider viewProvider, Messenger messenger) {
        super(Chat.class, Direction.DOWNSTREAM, 0);
        this.chatterProvider = chatterProvider;
        this.viewProvider = viewProvider;
        this.messenger = messenger;
    }

    // packet from server to proxy
    @Override
    public void packetReceive(PacketReceiveEvent<Chat> event) {
        final String message = event.packet().getMessage();
        if (message == null)
            return;

        final Chatter chatter = chatterProvider.get(event.player().uniqueId());
        final View view = viewProvider.getView(chatter);
        final Component packet = SERIALIZER.deserialize(message);
        if (view.isRenderedView(packet))
            return;

        message(packet).type(Message.Type.SYSTEM).send(messenger);
        event.cancelled(true);
    }

    // packet from proxy to server
    @Override
    public void packetSend(PacketSendEvent<Chat> packetSendEvent) {
    }
}
