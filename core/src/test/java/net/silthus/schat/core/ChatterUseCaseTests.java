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

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.core.channel.ChannelEntity;
import net.silthus.schat.core.chatter.ChatterEntity;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChatterUseCaseTests extends TestBase {

    private Chatter chatter;
    private Channel channel;

    @BeforeEach
    void setUp() {
        chatter = new ChatterEntity(Identity.identity("test")).getApiProxy();
        channel = new ChannelEntity("test", text("test")).getApiProxy();
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

    @Nested
    class SendMessage {

        private Message message;

        @BeforeEach
        void setUp() {
            message = sendMessage(Message.message("Bob", text("Hi")));
        }

        @Test
        void sendMessage_storesMessage() {
            assertThat(chatter.getMessages()).contains(message);
        }

        @Test
        void sendMessage_hasSource() {
            assertThat(message.getSource()).isEqualTo("Bob");
        }

        @Test
        void sendMessage_hasText() {
            assertThat(message.getMessage()).isEqualTo(text("Hi"));
        }
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
            chatter.join(channel);
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
                chatter.leave(channel);
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
            chatter.setActiveChannel(channel);
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
            chatter.leave(channel);
            assertThat(chatter.getActiveChannel()).isEmpty();
        }
    }
}
