/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
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
import net.silthus.schat.ui.view.View;
import net.silthus.schat.ui.view.ViewProvider;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.message.Message.message;

public final class ChatPacketListener extends AbstractPacketListener<Chat> {

    private static final @NotNull GsonComponentSerializer SERIALIZER = GsonComponentSerializer.gson();

    private final ChatterProvider chatterProvider;
    private final ViewProvider viewProvider;

    public ChatPacketListener(ChatterProvider chatterProvider, ViewProvider viewProvider) {
        super(Chat.class, Direction.DOWNSTREAM, 0);
        this.chatterProvider = chatterProvider;
        this.viewProvider = viewProvider;
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

        message(packet).type(Message.Type.SYSTEM).send();
        event.cancelled(true);
    }

    // packet from proxy to server
    @Override
    public void packetSend(PacketSendEvent<Chat> packetSendEvent) {
    }
}
