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

package net.silthus.schat.chatter;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.view.ViewConnectorMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.Chatter.chatter;
import static net.silthus.schat.identity.IdentityHelper.randomIdentity;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ChatterTest {
    private Chatter chatter;
    private Identity identity;

    @BeforeEach
    void setUp() {
        identity = randomIdentity();
        chatter = Chatter.createChatter(identity);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void given_null_identity_then_create_throws() {
        assertNPE(() -> Chatter.createChatter(null));
    }

    @Test
    void given_valid_identity_uses_identity_properties() {
        assertThat(chatter).extracting(
            Chatter::getUniqueId,
            Chatter::getName,
            Chatter::getDisplayName
        ).contains(
            identity.getUniqueId(),
            identity.getName(),
            identity.getDisplayName()
        );
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void when_join_is_called_given_null_throws_npe() {
        assertNPE(() -> chatter.join(null));
    }

    @Test
    void when_isJoined_is_called_given_null_returns_false() {
        assertThat(chatter.isJoined(null)).isFalse();
    }

    @Nested class given_valid_channel {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = randomChannel();
        }

        private void setActiveChannel(Channel channel) {
            chatter.setActiveChannel(channel);
        }

        private void assertChannelTargetsContains(Chatter chatter) {
            assertThat(channel.getTargets()).containsOnlyOnce(chatter);
        }

        private void assertChatterHasChannel(Channel channel) {
            assertThat(chatter.getChannels()).containsOnlyOnce(channel);
        }

        private void assertJoinSuccess() {
            assertChannelTargetsContains(chatter);
            assertChatterHasChannel(channel);
        }

        @Nested class then_setActiveChannel {
            @Test
            void sets_active_channel() {
                setActiveChannel(channel);
                assertThat(chatter.getActiveChannel())
                    .isPresent().get().isEqualTo(channel);
            }

            @Test
            void given_null_channel_clears_active_channel() {
                setActiveChannel(channel);
                setActiveChannel(null);
                assertThat(chatter.getActiveChannel()).isNotPresent();
            }

            @Nested class given_chatter_without_channel {
                @Test
                void joins_channel() {
                    setActiveChannel(channel);
                    assertJoinSuccess();
                }
            }
        }

        @Nested class when_channel_set_as_active_channel {
            @BeforeEach
            void setUp() {
                setActiveChannel(channel);
            }

            @Test
            void then_isActiveChannel_returns_true() {
                assertThat(chatter.isActiveChannel(channel)).isTrue();
            }
        }

        @Test
        void when_channel_is_not_active_channel_then_isActiveChannel_returns_false() {
            assertThat(chatter.isActiveChannel(channel)).isFalse();
        }

        @Nested class when_join_is_called {
            private void joinChannel() {
                chatter.join(channel);
            }

            @Test
            void then_adds_chatter_to_channel() {
                joinChannel();
                assertChannelTargetsContains(chatter);
            }

            @Test
            void then_adds_channel_to_chatter() {
                joinChannel();
                assertChatterHasChannel(channel);
            }

            @Test
            void twice_then_only_adds_channel_and_chatter_once() {
                joinChannel();
                joinChannel();
                assertJoinSuccess();
            }

            @Test
            void then_isJoined_returns_true() {
                joinChannel();
                assertThat(chatter.isJoined(channel)).isTrue();
            }
        }

        @Test
        void given_chatter_has_not_joined_channel_then_isJoined_returns_false() {
            assertThat(chatter.isJoined(randomChannel())).isFalse();
        }
    }

    @Nested class sendMessage {

        private Message sendRandomMessage() {
            final Message message = randomMessage();
            chatter.sendMessage(message);
            return message;
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_message_then_throws_npe() {
            assertNPE(() -> chatter.sendMessage(null));
        }

        @Test
        void given_no_message_handler_then_does_not_throw() {
            assertThatCode(this::sendRandomMessage)
                .doesNotThrowAnyException();
        }

        @Test
        void then_message_is_added() {
            final Message message = randomMessage();
            chatter.sendMessage(message);
            assertThat(chatter.getMessages()).contains(message);
        }

        @Nested class given_valid_view_connector {
            private final ViewConnectorMock connectorMock = new ViewConnectorMock();

            @BeforeEach
            void setUp() {
                chatter = chatter(randomIdentity())
                    .viewConnector(c -> connectorMock).create();
            }

            private void assertViewConnectorCalled() {
                connectorMock.assertUpdateCalled();
            }

            private void assertLastMessageIs(Message message) {
                assertThat(chatter.getLastMessage()).isPresent().get().isEqualTo(message);
            }

            @Test
            void then_message_handler_is_called() {
                sendRandomMessage();
                assertViewConnectorCalled();
            }

            @Test
            void then_context_holds_last_message() {
                final Message message = sendRandomMessage();
                assertLastMessageIs(message);
            }

            @Nested class update {
                @Test
                void given_no_messages_when_update_is_called_then_context_has_no_last_message() {
                    chatter.updateView();
                    assertViewConnectorCalled();
                    assertThat(chatter.getLastMessage()).isNotPresent();
                }

                @Test
                void given_messages_when_update_is_called_holds_last_message() {
                    final Message message = sendRandomMessage();
                    chatter.updateView();
                    assertViewConnectorCalled();
                    assertLastMessageIs(message);
                }
            }
        }
    }

    @Nested class hasPermission {
        @Test
        void given_null_returns_false() {
            assertThat(chatter.hasPermission(null)).isFalse();
        }

        @Test
        void given_no_permission_handler_does_not_throw() {
            assertThatCode(() -> chatter.hasPermission("abc"))
                .doesNotThrowAnyException();
        }

        @Nested class given_permission_handler {

            private boolean permissionHandlerCalled = false;

            @BeforeEach
            void setUp() {
                chatter = chatter(randomIdentity()).permissionHandler(permission -> {
                    permissionHandlerCalled = true;
                    return false;
                }).create();
            }

            @Test
            void then_handler_is_called() {
                chatter.hasPermission("test");
                assertThat(permissionHandlerCalled).isTrue();
            }
        }
    }
}
