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

package net.silthus.schat.ui.views;

import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.ui.view.View;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.channel.Channel.PRIORITY;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static net.silthus.schat.ui.format.ChannelFormat.COLOR;
import static net.silthus.schat.ui.view.View.VIEW_HEIGHT;
import static net.silthus.schat.ui.views.TabbedChannelsView.ACTIVE_CHANNEL;
import static net.silthus.schat.ui.views.TabbedChannelsView.CHANNEL_JOIN_CONFIG;
import static net.silthus.schat.ui.views.TabbedChannelsView.MESSAGE_FORMAT;
import static net.silthus.schat.ui.views.Views.tabbedChannels;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TabbedChannelsViewTests {

    private static final @NotNull MiniMessage COMPONENT_SERIALIZER = MiniMessage.miniMessage();
    private static final @NotNull PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = PlainTextComponentSerializer.plainText()
        .toBuilder().flattener(ComponentFlattener.textOnly()).build();
    private Chatter chatter;
    private View view;

    @BeforeEach
    void setUp() {
        chatter = randomChatter();
        view = tabbedChannels(chatter);
    }

    @NotNull
    private String msgText(Message message) {
        return COMPONENT_SERIALIZER.serialize(message.text());
    }

    @SneakyThrows
    @NotNull
    private Message sendMessage(Message message) {
        chatter.sendMessage(message);
        Thread.sleep(1L); // required to order messages by time
        return message;
    }

    private void sendMessage(String text) {
        sendMessage(Message.message(text).create());
    }

    private void sendMessageWithSource(String source, String text) {
        sendMessage(Message.message(text).source(Identity.identity(source)).create());
    }

    private void assertTextRenders(String expected) {
        assertEquals(expected, PLAIN_TEXT_SERIALIZER.serialize(view.render()).trim());
    }

    private void assertViewRenders(String expected) {
        assertEquals(expected, COMPONENT_SERIALIZER.serialize(view.render()).trim());
    }

    @Nested class given_null_chatter {

        @Test
        @SuppressWarnings("ConstantConditions")
        void throws_npe() {
            assertNPE(() -> tabbedChannels(null));
        }
    }

    @Nested class given_no_messages_and_no_channels {

        @Test
        void renders_view_with_only_empty_lines() {
            assertEquals("\n".repeat(Math.max(0, view.get(VIEW_HEIGHT))), COMPONENT_SERIALIZER.serialize(view.render()));
        }
    }

    @Nested class given_single_message {

        @Test
        void renders_message_text() {
            final Message message = sendMessage(randomMessage());
            assertTextRenders(msgText(message));
        }
    }

    @Nested class given_single_message_with_source {

        @BeforeEach
        void setUp() {
            sendMessageWithSource("Bob", "Hi");
        }

        @Test
        void renders_source_name_with_message_text() {
            assertTextRenders("Bob: Hi");
        }

        @Nested class and_custom_message_source_format {

            @Test
            void uses_format() {
                view = tabbedChannels(chatter)
                    .set(MESSAGE_FORMAT, msg ->
                        Component.text("<")
                            .append(msg.getOrDefault(Message.SOURCE, Identity.nil()).getDisplayName())
                            .append(Component.text("> "))
                            .append(msg.getOrDefault(Message.TEXT, Component.empty())));
                assertTextRenders("<Bob> Hi");
            }
        }
    }

    @Nested class given_two_messages {

        @Test
        void renders_both_messages() {
            sendMessage("Hey");
            sendMessageWithSource("Silthus", "Yo");
            assertTextRenders("""
            Hey
            Silthus: Yo"""
            );
        }
    }

    @Nested class given_single_channel {

        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = createChannel("test");
            chatter.join(channel);
        }

        @Test
        void renders_channel_name() {
            assertTextRenders("| test |");
        }

        @Nested class when_it_is_active {
            @BeforeEach
            void setUp() {
                chatter.setActiveChannel(channel);
            }

            @Test
            void underlines_channel() {
                assertViewRenders("| <green><underlined>test</underlined></green> |");
            }

            @Nested class and_different_format_is_used {
                @BeforeEach
                void setUp() {
                    view = tabbedChannels(chatter);
                    view.get(ACTIVE_CHANNEL).set(COLOR, RED);
                }

                @Test
                void uses_custom_format() {
                    assertViewRenders("| <red><underlined>test</underlined></red> |");
                }
            }
        }

        @Nested class when_it_is_inactive {
            @Test
            void then_channel_click_executes_join_command() {
                assertViewRenders("| <gray><click:run_command:\"/channel join test\">test</click></gray> |");
            }
        }
    }

    @Nested class given_two_channels {

        @BeforeEach
        void setUp() {
            chatter.join(createChannel("one"));
            chatter.join(createChannel("two"));
        }

        @Test
        void renders_both_seperated_by_a_divider() {
            assertTextRenders("| one | two |");
        }

        @Nested class with_different_priorities {
            @BeforeEach
            void setUp() {
                chatter.join(channelWith("zzz", PRIORITY, 1));
                chatter.join(createChannel("test"));
            }

            @Test
            void renders_higher_priority_channel_first() {
                assertTextRenders("| zzz | one | test | two |");
            }
        }

        @Nested class with_custom_channel_join_config_format {

            @BeforeEach
            void setUp() {
                view = tabbedChannels(chatter)
                    .set(CHANNEL_JOIN_CONFIG, JoinConfiguration.builder().separator(Component.text(" - ")).build());
            }

            @Test
            void uses_custom_format() {
                assertTextRenders("one - two");
            }
        }
    }

    @Nested class given_messages_and_channels {
        @BeforeEach
        void setUp() {
            chatter.join(createChannel("aaa"));
            chatter.setActiveChannel(channelWith("zzz", set(PRIORITY, 10)));
            sendMessage("No Source!");
            sendMessageWithSource("Player", "Hey");
            sendMessageWithSource("Player2", "Hello");
        }

        @Test
        void renders_full_view() {
            assertViewRenders("""
                No Source!
                Player: Hey
                Player2: Hello
                | <green><underlined>zzz</underlined></green> | <gray><click:run_command:"/channel join aaa">aaa</click></gray> |""");
        }
    }

    @Nested class given_rendered_view {
        private Component render;

        @BeforeEach
        void setUp() {
            render = view.render();
        }

        @Test
        void isRenderedView_returns_true() {
            assertThat(view.isRenderedView(render)).isTrue();
        }
    }
}
