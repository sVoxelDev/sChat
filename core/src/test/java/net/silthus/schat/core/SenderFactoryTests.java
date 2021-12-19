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

import java.util.Optional;
import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.identity.PlayerOut;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SenderFactoryTests {

    private PlayerOut<FakePlayer> playerAdapter;
    private SenderFactory<FakePlayer> factory;

    @BeforeEach
    void setUp() {
        playerAdapter = mockPlayerAdapter();
        factory = new StubSenderFactory(playerAdapter);
    }

    private PlayerOut<FakePlayer> mockPlayerAdapter() {
        PlayerOut<FakePlayer> playerAdapter = mock(PlayerOut.class);
        when(playerAdapter.toPlayer(any())).thenReturn(Optional.of(new FakePlayer()));
        return playerAdapter;
    }

    @Test
    void createSender_isNotNull() {
        final Sender sender = factory.createSender(mockChatter());
        assertThat(sender).isNotNull();
    }

    @Test
    void createSender_givenNoPlayer_returnsEmptySender() {
        when(playerAdapter.toPlayer(any())).thenReturn(Optional.empty());
        final Sender sender = factory.createSender(mockChatter());
        assertThat(sender).isNotNull();
    }

    @NotNull
    private Chatter mockChatter() {
        final Chatter chatter = mock(Chatter.class);
        when(chatter.getIdentity()).thenReturn(Identity.identity(UUID.randomUUID()));
        return chatter;
    }
}
