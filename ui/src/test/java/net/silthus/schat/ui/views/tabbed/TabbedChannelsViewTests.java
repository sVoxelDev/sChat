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
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.commands.CreatePrivateChannelCommand;
import net.silthus.schat.eventbus.EventBusMock;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageSource;
import net.silthus.schat.ui.view.ViewConfig;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.channel.ChannelHelper.ConfiguredSetting.set;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.channel.ChannelSettings.FORCED;
import static net.silthus.schat.channel.ChannelSettings.PRIORITY;
import static net.silthus.schat.chatter.ChatterMock.chatterMock;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.commands.CreatePrivateChannelCommand.createPrivateChannel;
import static net.silthus.schat.commands.SendPrivateMessageCommand.sendPrivateMessage;
import static net.silthus.schat.identity.Identity.identity;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.message.MessageHelper.randomMessage;
import static net.silthus.schat.message.MessageSource.of;
import static net.silthus.schat.ui.placeholder.ReplacementProvider.REPLACED_MESSAGE_FORMAT;
import static net.silthus.schat.ui.view.ViewConfig.FORMAT_CONFIG;
import static net.silthus.schat.ui.views.Views.tabbedChannels;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TabbedChannelsViewTests {

    private static final @NotNull MiniMessage COMPONENT_SERIALIZER = MiniMessage.miniMessage();
    private static final @NotNull MiniMessage COLOR_ONLY_SERIALIZER = MiniMessage.builder().tags(StandardTags.color()).build();
    private static final @NotNull PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = PlainTextComponentSerializer.plainText()
        .toBuilder().flattener(ComponentFlattener.textOnly()).build();

    private final EventBusMock eventBus = EventBusMock.eventBusMock();
    private ChatterMock chatter;
    private TabbedChannelsView view;

    @BeforeEach
    void setUp() {
        chatter = chatterMock(Identity.identity("Player"));
        view = new TabbedChannelsView(chatter, new ViewConfig());
        eventBus.register(view);

        CreatePrivateChannelCommand.prototype(builder -> builder.channelRepository(createInMemoryChannelRepository(eventBus)));
    }

    @AfterEach
    void tearDown() {
        eventBus.close();
    }

    @NotNull
    private String msgText(Message message) {
        return COMPONENT_SERIALIZER.serialize(message.text());
    }

    private void sendMessage(String text) {
        sendMessage(message(text).create());
    }

    @SneakyThrows
    @NotNull
    private Message sendMessage(Message message) {
        chatter.sendMessage(message);
        Thread.sleep(1L); // required to order messages by time
        return message;
    }

    private void sendMessageWithSource(String source, String text) {
        sendMessage(message(text).source(of(identity(source))).create());
    }

    private void assertTextRenders(String expected) {
        assertEquals(expected, PLAIN_TEXT_SERIALIZER.serialize(view.render()).trim());
    }

    private void assertTextContains(String... expected) {
        assertThat(PLAIN_TEXT_SERIALIZER.serialize(view.render()).trim())
            .contains(expected);
    }

    private void assertTextDoesNotContain(String... unexpected) {
        assertThat(PLAIN_TEXT_SERIALIZER.serialize(view.render()).trim())
            .doesNotContain(unexpected);
    }

    private void assertViewRenders(String expected) {
        assertEquals(expected, replaceNewLines(COMPONENT_SERIALIZER.serialize(view.render())).trim());
    }

    private void assertViewDoesNotContain(String... unexpected) {
        assertThat(replaceNewLines(COMPONENT_SERIALIZER.serialize(view.render())).trim()).doesNotContain(unexpected);
    }

    private void assertViewContains(String... expected) {
        assertThat(replaceNewLines(COMPONENT_SERIALIZER.serialize(view.render())).trim()).contains(expected);
    }

    private void assertColorOnlyViewContains(String... expected) {
        assertThat(replaceNewLines(COLOR_ONLY_SERIALIZER.serialize(view.render())).trim()).contains(expected);
    }

    private String replaceNewLines(String input) {
        return input.replaceAll("<br></br>", "\n").replaceAll("<br>", "\n");
    }

    @Test
    void update_renders_view_and_sends_message() {
        view.update();
        chatter.assertReceivedRawMessage(view.render());
    }

    @Nested
    class given_null_chatter {

        @Test
        @SuppressWarnings("ConstantConditions")
        void throws_npe() {
            assertNPE(() -> tabbedChannels(null, new ViewConfig()));
        }
    }

    @Test
    void adds_channel_tabs_when_view_is_created() {
        chatter = randomChatter();
        final Channel one = channelWith("one");
        final Channel two = channelWith("two");
        chatter.join(one);
        chatter.join(two);
        view = new TabbedChannelsView(chatter, new ViewConfig());

        assertThat(view.tabs()).containsKeys(one, two);
    }

    @Nested
    class given_single_message {

        @Test
        void renders_message_text() {
            final Message message = sendMessage(randomMessage());
            assertTextContains(msgText(message));
        }
    }

    @Nested
    class given_single_message_with_source {

        @BeforeEach
        void setUp() {
            sendMessageWithSource("Bob", "Hi");
        }

        @Test
        void renders_source_name_with_message_text() {
            assertTextContains("Bob: Hi");
        }
    }

    @Nested
    class given_two_messages {

        @Test
        void renders_both_messages() {
            sendMessage("Hey");
            sendMessageWithSource("Silthus", "Yo");
            assertViewRenders("""
                Hey
                <yellow>Silthus<gray>: </gray>Yo</yellow>"""
            );
        }
    }

    @Nested
    class given_message_with_formatted_setting {
        @BeforeEach
        void setUp() {
            final Message message = message().source(randomChatter()).create();
            message.set(REPLACED_MESSAGE_FORMAT, "FORMATTED");
            sendMessage(message);
        }

        @Test
        void renders_formatted_message() {
            assertViewContains("FORMATTED");
        }
    }

    @Nested
    class given_single_channel {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = createChannel("test");
            chatter.join(channel);
        }

        @Test
        void renders_channel_name() {
            assertTextRenders("| ❌test |");
        }

        @Nested
        class when_it_is_active {
            @BeforeEach
            void setUp() {
                chatter.activeChannel(channel);
            }

            @Test
            void underlines_channel() {
                assertViewRenders("| <red><click:run_command:'/channel leave test'><hover:show_text:\"<lang:schat.hover.leave-channel:'<gray>test'>\">❌<underlined><green>test</green></underlined></hover></click></red> |");
            }

            @Nested
            class and_different_format_is_used {
                @BeforeEach
                void setUp() {
                    channel.set(FORMAT_CONFIG, new TabFormatConfig().activeColor(RED));
                }

                @Test
                void uses_custom_format() {
                    assertViewRenders("| <red><click:run_command:'/channel leave test'><hover:show_text:\"<lang:schat.hover.leave-channel:'<gray>test'>\">❌<underlined><red>test</red></underlined></hover></click></red> |");
                }
            }
        }

        @Nested
        class when_it_is_inactive {
            @BeforeEach
            void setUp() {
                chatter.activeChannel(randomChannel());
            }

            @Test
            void then_channel_click_executes_join_command() {
                assertViewContains("<click:run_command:'/channel join test'>");
            }
        }

        @Nested
        class given_it_is_forced {
            @BeforeEach
            void setUp() {
                channel.set(FORCED, true);
            }

            @Test
            void does_not_render_leave_symbol() {
                assertTextRenders("| test |");
            }
        }
    }

    @Nested
    class given_private_channel {
        private ChatterMock target;

        @BeforeEach
        void setUp() {
            target = chatterMock(Identity.identity("target"));
            chatter.activeChannel(createPrivateChannel(chatter, target).channel());
        }

        @Test
        void renders_partner_name() {
            assertTextRenders("| ❌target |");
        }

        @Test
        void does_not_display_system_messages() {
            sendMessage("System");
            assertTextRenders("| ❌target |");
        }

        @Test
        void displays_private_messages() throws InterruptedException {
            sendPrivateMessage(chatter, target, text("Hi"));
            Thread.sleep(1L);
            sendPrivateMessage(target, chatter, text("Hey back"));
            assertViewContains("""
                <dark_aqua><lang:schat.chat.message.you><gray>: </gray><gray>Hi</gray></lang></dark_aqua>
                <yellow>target<gray>: </gray><gray>Hey back</gray></yellow>""");
        }
    }

    @Nested class given_private_and_public_channel {
        private Channel channel;
        private ChatterMock target;
        private Channel privateChannel;

        @BeforeEach
        void setUp() {
            target = chatterMock(Identity.identity("target"));
            channel = createChannel("public");
            target.join(channel);
            chatter.join(channel);
            privateChannel = createPrivateChannel(chatter, target).channel();
            chatter.activeChannel(privateChannel);
            target.activeChannel(channel);
        }

        @Test
        void message_sent_to_public_channel_is_not_shown_in_private_channel() {
            target.message("in public").to(channel).send();
            assertTextDoesNotContain("in public");
        }

        @Nested class given_chatter_leaves_private_chat {
            @BeforeEach
            void setUp() {
                chatter.leave(privateChannel);
            }

            @Test
            void then_private_chat_is_hidden() {
                assertTextDoesNotContain("target");
            }

            @Test
            void when_new_message_is_sent_to_inactive_private_chat_then_private_chat_is_shown() {
                target.message("hi").to(privateChannel).send();
                assertTextContains("target");
            }
        }
    }

    @Nested
    class given_two_channels {

        private @NotNull Channel channelOne;
        private @NotNull Channel channelTwo;

        @BeforeEach
        void setUp() {
            channelOne = createChannel("one");
            channelTwo = createChannel("two");
            chatter.join(channelOne);
            chatter.join(channelTwo);
        }

        @Test
        void renders_both_seperated_by_a_divider() {
            assertTextRenders("| ❌one | ❌two |");
        }

        @Nested
        class given_both_channels_received_messages {
            @BeforeEach
            void setUp() {
                sendMessage("System");
                message("one").source(of(identity("Bob"))).to(channelOne).type(Message.Type.CHAT).send();
                message("two").source(of(identity("Bob"))).to(channelTwo).type(Message.Type.CHAT).send();
            }

            @Test
            void when_no_channel_is_active_then_only_system_messages_are_displayed() {
                assertTextContains("System");
                assertTextDoesNotContain("Bob: one", "Bob: two");
            }

            @Nested
            class given_channel_one_is_active {
                @BeforeEach
                void setUp() {
                    chatter.activeChannel(channelOne);
                }

                @Test
                void then_message_one_is_displayed() {
                    assertTextRenders("""
                        System
                        Bob: one
                        | ❌one | ❌two₂ |""");
                }

                @Test
                void then_message_two_is_not_displayed() {
                    assertTextDoesNotContain("Bob: two");
                }

                @Test
                void then_channel_two_has_unread_indicator() {
                    assertColorOnlyViewContains("<white>two</white>");
                }

                @Test
                void unread_counter_is_shown() {
                    assertColorOnlyViewContains("<red>₂</red>");
                }

                @Test
                void given_no_unread_messages_channel_one_has_no_unread_counter() {
                    assertViewContains("<underlined><green>one</green></underlined>");
                }

                @Test
                void given_highlight_unread_is_false_then_unread_indicator_is_hidden() {
                    channelTwo.set(FORMAT_CONFIG, new TabFormatConfig().highlightUnread(false));
                    assertColorOnlyViewContains("<gray>two</gray>");
                }

                @Test
                void given_show_unread_counter_is_false_then_counter_is_hidden() {
                    channelTwo.set(FORMAT_CONFIG, new TabFormatConfig().showUnreadCount(false));
                    assertViewDoesNotContain("<red>₂</red>");
                }
            }
        }

        @Nested
        class with_different_priorities {
            @BeforeEach
            void setUp() {
                chatter.join(channelWith("zzz", PRIORITY, 1));
                chatter.join(createChannel("test"));
            }

            @Test
            void renders_higher_priority_channel_first() {
                assertTextRenders("| ❌zzz | ❌one | ❌test | ❌two |");
            }

            @Nested
            class and_private_channel {
                @BeforeEach
                void setUp() {
                    createPrivateChannel(chatter, chatterMock(Identity.identity("target")));
                }

                @Test
                void renders_private_channel_last() {
                    assertTextRenders("| ❌zzz | ❌one | ❌test | ❌two | ❌target |");
                }
            }
        }

        @Nested
        class with_custom_channel_join_config_format {

            @BeforeEach
            void setUp() {
                view.config().channelJoinConfig(JoinConfiguration.builder().separator(text(" - ")).build());
            }

            @Test
            void uses_custom_format() {
                assertTextRenders("❌one - ❌two");
            }
        }
    }

    @Nested
    class given_messages_and_channels {
        @BeforeEach
        void setUp() {
            chatter.join(createChannel("aaa"));
            final Channel channel = channelWith(
                "zzz",
                set(PRIORITY, 10)
            );
            channel.set(FORMAT_CONFIG, new TabFormatConfig().messageFormat((v, message) -> message.get(Message.SOURCE)
                .orElse(MessageSource.nil())
                .displayName()
                .append(text(": ").append(message.getOrDefault(Message.TEXT, empty())))
            ));
            chatter.activeChannel(channel);
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
                | <red><click:run_command:'/channel leave zzz'><hover:show_text:"<lang:schat.hover.leave-channel:'<gray>zzz'>">❌<underlined><green>zzz</green></underlined></hover></click></red> | <red><click:run_command:'/channel leave aaa'><hover:show_text:"<lang:schat.hover.leave-channel:'<gray>aaa'>">❌<italic><white><click:run_command:'/channel join aaa'><hover:show_text:"<gray><lang:schat.hover.join-channel:'aaa'>">aaa</hover></click></white></italic><red>₃</red></hover></click></red> |""");
        }
    }

    @Nested
    class dynamic_view_updates {

        private void assertViewUpdated() {
            chatter.assertLastRawMessage(view.render());
        }

        @Test
        void joined_channel_updates_view() {
            chatter.join(randomChannel());
            assertViewUpdated();
        }

        @Test
        void left_channel_removes_channel() {
            final Channel channel = channelWith("test");
            chatter.join(channel);
            chatter.leave(channel);

            assertTextDoesNotContain("test");
            assertViewUpdated();
        }

        @Test
        void changed_active_channel_updates_view() {
            final Channel test = channelWith("test");
            chatter.join(test);
            chatter.activeChannel(channelWith("active"));
            chatter.activeChannel(test);

            assertViewUpdated();
        }

        @Test
        void sendMessage_updates_view() {
            chatter.sendMessage(randomMessage());

            assertViewUpdated();
        }

        @Test
        void sendChannelMessage_updates_view() {
            final Channel channel = randomChannel();
            chatter.activeChannel(channel);
            channel.sendMessage(randomMessage());

            assertViewUpdated();
        }
    }
}
