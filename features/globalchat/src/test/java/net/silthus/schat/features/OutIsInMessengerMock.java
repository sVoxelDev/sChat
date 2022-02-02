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

package net.silthus.schat.features;

import java.util.LinkedList;
import java.util.Queue;
import net.silthus.schat.messenger.IncomingPluginMessageConsumer;
import net.silthus.schat.messenger.Messenger;
import net.silthus.schat.messenger.PluginMessage;
import org.checkerframework.checker.nullness.qual.NonNull;

import static org.assertj.core.api.Assertions.assertThat;

public class OutIsInMessengerMock implements IncomingPluginMessageConsumer, Messenger {

    private final Queue<PluginMessage> received = new LinkedList<>();

    private boolean calledSendOutgoing = false;

    @Override
    public boolean consumeIncomingMessage(@NonNull PluginMessage message) {
        received.add(message);
        return true;
    }

    @Override
    public boolean consumeIncomingMessageAsString(@NonNull String encodedString) {
        return false;
    }

    @Override
    public void sendPluginMessage(@NonNull PluginMessage pluginMessage) {
        this.calledSendOutgoing = true;
    }

    public void assertOutgoingMessageSent() {
        assertThat(calledSendOutgoing).isTrue();
    }

    public void assertReceivedMessage(PluginMessage msg) {
        assertThat(received).contains(msg);
    }
}
