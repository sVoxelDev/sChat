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
import net.silthus.schat.Message;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class ChatterUseCaseTests extends TestBase {

    private User user;
    private ChattersInteractor useCase;
    private ChatterEntity chatter;
    private Channel channel;

    @BeforeEach
    void setUp() {
        useCase = new ChattersInteractor(userAdapter());
        chatter = new ChatterEntity(new User(UUID.randomUUID(), "test", text("test")));
        channel = new Channel("test");
    }

    private UserAdapter userAdapter() {
        final UserAdapter userAdapter = mock(UserAdapter.class);
        mockUser(userAdapter);
        return userAdapter;
    }

    private void mockUser(final UserAdapter userAdapter) {
        final UUID playerId = UUID.randomUUID();
        user = new User(playerId, "test", text("Player"));
        doReturn(user).when(userAdapter).getUser(playerId);
    }

    @Test
    void create() {
        assertThat(useCase.getPlayerChatter(user.id())).extracting(
            ChatterEntity::getId,
            ChatterEntity::getName,
            ChatterEntity::getDisplayName
        ).contains(
            user.id(),
            user.name(),
            user.displayName()
        );
    }

    @Test
    void getActiveChannel_isEmpty() {
        assertThat(chatter.getActiveChannel()).isEmpty();
    }

    @NotNull
    private Message sendMessage(Message message) {
        chatter.sendMessage(message);
        return message;
    }

    @Test
    void getMessages_isEmpty() {
        assertThat(chatter.getMessages()).isEmpty();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getMessages_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> chatter.getMessages().add(randomMessage()));
    }

    @Test
    void sendMessage_storesMessage() {
        final Message message = sendMessage(randomMessage());
        assertThat(chatter.getMessages()).contains(message);
    }

    @Test
    void sendMessage_withSource() {
        final Message message = sendMessage(Message.message("Bob", randomText()));
        assertThat(chatter.getMessages()).contains(message);
    }

    @Test
    void getChannels_isEmpty() {
        assertThat(chatter.getChannels()).isEmpty();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getChannels_isUnmodifiable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> chatter.getChannels().add(channel));
    }

    @Nested
    class JoinChannel {

        @BeforeEach
        void setUp() {
            useCase.join(chatter, channel);
        }

        @Test
        void join_addsChannel_toChatter() {
            assertThat(chatter.getChannels()).contains(channel);
        }

        @Test
        void join_addsTarget_toChannel() {
            assertThat(channel.getTargets()).contains(chatter);
        }

        @Nested
        class LeaveChannel {

            @BeforeEach
            void setUp() {
                useCase.leave(chatter, channel);
            }

            @Test
            void leave_removesChannel_fromChatter() {
                assertThat(chatter.getChannels()).doesNotContain(channel);
            }

            @Test
            void leave_removesTarget_fromChannel() {
                assertThat(channel.getTargets()).doesNotContain(chatter);
            }
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

        @Test
        void leave_removesActiveTarget_ifSame() {
            useCase.leave(chatter, channel);
            assertThat(chatter.getActiveChannel()).isEmpty();
        }
    }
}
