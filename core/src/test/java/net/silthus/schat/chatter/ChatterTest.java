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
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.events.chatter.ChatterChangedActiveChannelEvent;
import net.silthus.schat.events.chatter.ChatterJoinedChannelEvent;
import net.silthus.schat.events.chatter.ChatterLeftChannelEvent;
import net.silthus.schat.events.chatter.ChatterReceivedMessageEvent;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.ui.ViewConnectorMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.eventbus.EventBusMock.eventBusMock;
import static net.silthus.schat.identity.IdentityHelper.randomIdentity;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ChatterTest {
    private ChatterMock chatter;
    private Identity identity;
    private EventBusMock eventBus;

    @BeforeEach
    void setUp() {
        identity = randomIdentity();
        eventBus = eventBusMock();
        ChatterPrototype.configure(eventBus);
        chatter = ChatterMock.chatterMock(identity);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void given_null_identity_then_create_throws() {
        assertNPE(() -> Chatter.chatter(null));
    }

    @Test
    void given_valid_identity_uses_identity_properties() {
        assertThat(chatter).extracting(
            Chatter::uniqueId,
            Chatter::name,
            Chatter::displayName
        ).contains(
            identity.uniqueId(),
            identity.name(),
            identity.displayName()
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
            chatter.activeChannel(channel);
        }

        private void assertChannelTargetsContains(Chatter chatter) {
            assertThat(channel.targets()).containsOnlyOnce(chatter);
        }

        private void assertChatterHasChannel(Channel channel) {
            assertThat(chatter.channels()).containsOnlyOnce(channel);
        }

        private void assertJoinSuccess() {
            assertChannelTargetsContains(chatter);
            assertChatterHasChannel(channel);
        }

        @Nested class when_setActiveChannel_is_called {
            @Test
            void sets_active_channel() {
                setActiveChannel(channel);
                assertThat(chatter.activeChannel())
                    .isPresent().get().isEqualTo(channel);
            }

            @Test
            void fires_ActiveChannelChangedEvent() {
                setActiveChannel(channel);
                eventBus.assertEventFired(new ChatterChangedActiveChannelEvent(chatter, null, channel));
            }

            @Test
            void given_channel_is_active_when_it_is_set_active_then_ActiveChannelChangedEvent_is_not_fired() {
                setActiveChannel(channel);
                eventBus.reset();
                setActiveChannel(channel);
                eventBus.assertNoEventFired(ChatterChangedActiveChannelEvent.class);
            }

            @Test
            void leave_clears_active_channel() {
                setActiveChannel(channel);
                chatter.leave(channel);
                assertThat(chatter.activeChannel()).isNotPresent();
            }

            @Nested class given_chatter_without_channel {
                @Test
                void joins_channel() {
                    setActiveChannel(channel);
                    assertJoinSuccess();
                }
            }
        }

        @Nested class when_channel_is_active_channel {
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

            @Test
            void fires_JoinedChannelEvent() {
                joinChannel();
                eventBus.assertEventFired(new ChatterJoinedChannelEvent(chatter, channel));
            }
        }

        @Nested class leave {
            @Test
            void chatter_is_not_joined_then_left_event_is_not_fired() {
                chatter.leave(channel);
                eventBus.assertNoEventFired(ChatterLeftChannelEvent.class);
            }

            @Test
            void chatter_has_joined_channel_then_left_event_is_fired() {
                chatter.join(channel);
                chatter.leave(channel);
                eventBus.assertEventFired(new ChatterLeftChannelEvent(chatter, channel));
            }

            @Test
            void removes_chatter_from_channel_targets() {
                channel.addTarget(chatter);
                chatter.leave(channel);
                assertThat(channel.targets()).doesNotContain(chatter);
            }

            @Test
            void removes_channel_from_chatter_channels() {
                chatter.join(channel);
                chatter.leave(channel);
                chatter.assertNotJoinedChannel(channel);
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
            assertThat(chatter.messages()).contains(message);
        }

        @Test
        void ReceivedMessage_event_is_fired() {
            final Message message = randomMessage();
            chatter.sendMessage(message);
            eventBus.assertEventFired(new ChatterReceivedMessageEvent(chatter, message));
        }

        @Nested class given_valid_view_connector {
            private final ViewConnectorMock view = new ViewConnectorMock();

            @BeforeEach
            void setUp() {
                chatter = ChatterMock.chatterMock(identity, builder -> builder.viewConnector(c -> view));
            }

            private void assertViewUpdated() {
                view.assertUpdateCalled();
            }

            private void assertViewNotUpdated() {
                view.assertUpdateNotCalled();
            }

            private void assertLastMessageIs(Message message) {
                assertThat(chatter.messages().last()).isEqualTo(message);
            }

            @Test
            void then_message_handler_is_called() {
                sendRandomMessage();
                assertViewUpdated();
            }

            @Test
            void then_context_holds_last_message() {
                final Message message = sendRandomMessage();
                assertLastMessageIs(message);
            }

            @Test
            void when_active_channel_is_set_then_view_is_updated() {
                chatter.activeChannel(randomChannel());
                assertViewUpdated();
            }

            @Test
            void when_channel_is_joined_then_view_is_updated() {
                chatter.join(randomChannel());
                assertViewUpdated();
            }

            @Test
            void when_channel_is_already_joined_then_view_is_not_updated() {
                final Channel channel = randomChannel();
                chatter.join(channel);
                view.resetUpdateCalls();
                chatter.join(channel);
                assertViewNotUpdated();
            }

            @Nested class update {
                @Test
                void given_no_messages_when_update_is_called_then_context_has_no_last_message() {
                    chatter.updateView();
                    assertViewUpdated();
                    assertThat(chatter.messages().last()).isNull();
                }

                @Test
                void given_messages_when_update_is_called_holds_last_message() {
                    final Message message = sendRandomMessage();
                    chatter.updateView();
                    assertViewUpdated();
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

            private Chatter chatter;
            private boolean permissionHandlerCalled = false;

            @BeforeEach
            void setUp() {
                chatter = Chatter.chatterBuilder(randomIdentity()).permissionHandler(permission -> {
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
