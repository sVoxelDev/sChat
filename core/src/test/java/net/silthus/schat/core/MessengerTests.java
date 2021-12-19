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

package net.silthus.schat.core;

import java.util.UUID;
import net.silthus.schat.core.chatter.ChatterEntity;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.message.Message.message;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class MessengerTests {

    private FakePlayer player;

    @BeforeEach
    void setUp() {
        player = spy(new FakePlayer());
    }

    @Test
    void create() {
        final Messenger messenger = new Messenger(StubSenderFactory.createStubSenderFactory(player));
        final ChatterEntity chatter = new ChatterEntity(Identity.identity(UUID.randomUUID()));
        final Message message = message(text("Hi"));
        messenger.sendMessage(chatter, message);
        verify(player).sendMessage(message.getMessage());
    }

}
