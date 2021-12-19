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

package net.silthus.schat.core.chatter;

import java.util.Optional;
import java.util.UUID;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.Chatters;
import net.silthus.schat.core.channel.ChannelEntity;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.identity.PlayerIn;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatterInteractorTests {

    private Chatters useCase;
    private Chatter chatter;
    private Channel channel;

    @BeforeEach
    void setUp() {
        useCase = new ChatterInteractor(new InMemoryChatterRepository(createPlayerInMock()));
        chatter = useCase.getPlayer(UUID.randomUUID());
        channel = new ChannelEntity("test", text("Test"));
    }

    @NotNull
    private PlayerIn<?> createPlayerInMock() {
        final PlayerIn<?> playerIn = mock(PlayerIn.class);
        when(playerIn.fromId(any())).thenAnswer(invocation -> Optional.of(fakeIdentity(invocation.getArgument(0))));
        return playerIn;
    }

    @NotNull
    private Identity fakeIdentity(UUID id) {
        return Identity.identity(id, "Bob", text("Bob"));
    }

    @Test
    void getPlayer_byId_returnsChatter() {
        final UUID playerId = UUID.randomUUID();
        final Chatter chatter = useCase.getPlayer(playerId);
        assertThat(chatter.getId()).isEqualTo(playerId);
    }

    @Test
    void getPlayer_byId_hasName() {
        final Chatter chatter = useCase.getPlayer(UUID.randomUUID());
        assertThat(chatter.getName()).isNotBlank();
    }

    @Nested
    class JoinChannel {

        @BeforeEach
        void setUp() {
            useCase.joinChannel(chatter, channel);
        }

        @Test
        void join_addsChannel_toChatter() {
            assertThat(chatter.getChannels()).contains(channel);
        }

        @Test
        void join_addsTarget_toChannel() {
            assertThat(channel.getTargets()).contains(chatter);
        }

    }

    @Nested
    class LeaveChannel {

        @BeforeEach
        void setUp() {
            useCase.joinChannel(chatter, channel);
            useCase.leaveChannel(chatter, channel);
        }

        @Test
        void leave_removesChannel_fromChatter() {
            assertThat(chatter.getChannels()).doesNotContain(channel);
        }

        @Test
        void leave_removesTarget_fromChannel() {
            assertThat(channel.getTargets()).doesNotContain(chatter);
        }

        @Test
        void leave_removesActiveTarget_ifSame() {
            useCase.setActiveChannel(chatter, channel);
            assertThat(chatter.isActiveChannel(channel)).isTrue();
            useCase.leaveChannel(chatter, channel);
            assertThat(chatter.getActiveChannel()).isEmpty();
        }
    }

    @Nested
    class SetActiveChannel {

        @BeforeEach
        void setUp() {
            useCase.setActiveChannel(chatter, channel);
        }

        @Test
        void getActiveChannel_returnsActiveChannel() {
            assertThat(chatter.getActiveChannel())
                .isPresent().get()
                .isEqualTo(channel);
        }

        @Test
        void setActiveChannel_addsChannel() {
            assertThat(chatter.getChannels()).contains(channel);
        }

        @Test
        void setActiveChannel_addsChatter_asTarget() {
            assertThat(channel.getTargets()).contains(chatter);
        }
    }
}
