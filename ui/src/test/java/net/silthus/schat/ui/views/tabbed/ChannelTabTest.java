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
package net.silthus.schat.ui.views.tabbed;

import lombok.SneakyThrows;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelHelper;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.ui.view.ViewConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.message.MessageHelper.randomMessage;
import static org.assertj.core.api.Assertions.assertThat;

class ChannelTabTest {

    private final EventBusMock eventBus = EventBusMock.eventBusMock();
    private ChatterMock chatter;
    private Channel channel;
    private ChannelTab tab;

    @BeforeEach
    void setUp() {
        chatter = ChatterMock.randomChatter();
        channel = ChannelHelper.randomChannel();

        TabbedChannelsView view = new TabbedChannelsView(chatter, new ViewConfig());
        eventBus.register(view);

        chatter.activeChannel(channel);
        tab = view.tab(channel).get();
    }

    @AfterEach
    void tearDown() {
        eventBus.close();
    }

    @SneakyThrows
    private void sendMessages(int amount) {
        for (int i = 0; i < amount; i++) {
            channel.sendMessage(randomMessage());
            Thread.sleep(1L);
        }
    }

    @Nested class unread_counter {
        @Test
        void given_no_messages_then_unread_counter_is_zero_() {
            assertThat(tab.unreadCount()).isZero();
        }

        @Nested class given_tab_is_inactive {
            @BeforeEach
            void setUp() {
                chatter.activeChannel(null);
            }

            @Test
            void unread_counter_increases() {
                sendMessages(2);
                assertThat(tab.unreadCount()).isEqualTo(2);
                sendMessages(3);
                assertThat(tab.unreadCount()).isEqualTo(5);
                assertThat(tab.isUnread()).isTrue();
            }

            @Test
            void when_channel_becomes_active_unread_counter_resets() {
                sendMessages(3);
                chatter.activeChannel(channel);
                assertThat(tab.unreadCount()).isZero();
                chatter.activeChannel(null);
                sendMessages(2);
                assertThat(tab.unreadCount()).isEqualTo(2);
            }
        }

        @Nested class given_tab_is_active {
            @BeforeEach
            void setUp() {
                chatter.activeChannel(channel);
            }

            @Test
            void then_unread_counter_does_not_increase() {
                sendMessages(3);
                assertThat(tab.unreadCount()).isZero();
                assertThat(tab.isUnread()).isFalse();
            }
        }
    }

    @Nested
    class length {

        @Test
        void empty_tab_is_of_zero_length() {
            assertThat(tab.length()).isZero();
        }

        @Test
        void tab_with_messages_has_size_of_message_count() {
            sendMessages(3);
            assertThat(tab.length()).isEqualTo(3);
            sendMessages(2);
            assertThat(tab.length()).isEqualTo(5);
        }
    }
}
