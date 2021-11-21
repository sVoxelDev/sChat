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

package net.silthus.chat.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.silthus.chat.*;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.PrivateConversation;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class SChatCommandsTest extends TestBase {

    private PlayerMock player;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player = server.addPlayer();
        player.addAttachment(plugin, Constants.PERMISSION_PLAYER_COMMANDS, true);
        player.addAttachment(plugin, Constants.PERMISSION_PLAYER_CHANNEL_COMMANDS, true);
        player.addAttachment(plugin, Constants.PERMISSION_PLAYER_CHANNEL_JOIN, true);
        player.addAttachment(plugin, Constants.PERMISSION_PLAYER_CHANNEL_LEAVE, true);
        player.addAttachment(plugin, Constants.PERMISSION_PLAYER_CHANNEL_QUICKMESSAGE, true);
        player.addAttachment(plugin, Constants.PERMISSION_PLAYER_DIRECT_MESSAGE, true);
        player.addAttachment(plugin, Constants.PERMISSION_SELECT_MESSAGE, true);
        player.addAttachment(plugin, Constants.PERMISSION_MESSAGE_DELETE, true);
    }

    @Test
    void reload() {
        player.performCommand("schat reload");
        verify(plugin.getChannelRegistry(), never()).load(any());

        loadTestConfig("reload-test.yml");
        player.addAttachment(plugin, Constants.PERMISSION_ADMIN_RELOAD, true);
        player.performCommand("schat reload");
        verify(plugin.getChannelRegistry()).load(any());
    }

    @Test
    void broadcast_sendsMessageToAllChannels() throws InterruptedException {
        final Chatter chatter = Chatter.player(player);
        final Channel channel = createChannel("test");
        final Channel foobar = createChannel("foobar");
        final Channel unsubscribed = createChannel("unsubscribed");
        chatter.subscribe(foobar);
        chatter.setActiveConversation(channel);

        broadcast();
        assertThat(getLastMessage(player)).contains("you do not have permission to perform this command");
        player.addAttachment(plugin, Constants.PERMISSION_ADMIN_BROADCAST, true);
        Thread.sleep(10L);

        final TextComponent expected = broadcast();
        chatter.getLastReceivedMessage().getText().contains(expected);
        channel.getLastReceivedMessage().getText().contains(expected);
        foobar.getLastReceivedMessage().getText().contains(expected);
        unsubscribed.getLastReceivedMessage().getText().contains(expected);
    }

    @NotNull
    private TextComponent broadcast() {
        final TextComponent expected = text("Hey you all!");
        player.performCommand("broadcast Hey you all!");
        return expected;
    }

    @Test
    void broadcast_marksMessageAsReadEverywhere() {
        final Chatter chatter = Chatter.player(player);
        final Channel active = createChannel("active");
        final Channel channel = createChannel("channel");
        chatter.subscribe(channel);
        channel.setActiveConversation(active);
        player.addAttachment(plugin, Constants.PERMISSION_ADMIN_BROADCAST, true);

        broadcast();
        assertThat(chatter.getUnreadMessages(channel)).isEmpty();
    }

    @Nested
    class ChannelCommands {

        @Test
        void join_JoinsPlayerToChannel() {
            Channel channel = createChannel("test");

            player.addAttachment(plugin, channel.getPermission(), true);

            assertThat(player.performCommand("schat channel join test")).isTrue();
            assertThat(channel.getTargets()).contains(Chatter.player(player));
            assertThat(player.nextMessage()).contains(ChatColor.GRAY + "You joined the channel: " + ChatColor.GOLD + "test" + ChatColor.GRAY + ".");
        }

        @Test
        void join_withoutPermission_fails() {
            Channel channel = createChannel("test", cfg -> cfg.protect(true));
            Chatter chatter = Chatter.player(player);

            assertThat(player.performCommand("ch test")).isTrue();
            assertThat(channel.getTargets()).doesNotContain(chatter);
            assertThat(cleaned(player.nextMessage())).contains("You don't have permission to access the 'test' channel.");
        }

        @Test
        void quickMessage_sendsMessageToChannel() {
            Channel channel = createChannel("test");

            assertThat(player.performCommand("ch test Hey how are you?")).isTrue();
            assertThat(channel.getLastReceivedMessage()).isNotNull();
            assertThat(toText(channel.getLastReceivedMessage())).contains("Player0[!]&7: Hey how are you?");
        }

        @Test
        void quickMessage_noPermission_fails() {
            Channel channel = createChannel("test", config -> config.protect(true));

            assertThat(player.performCommand("ch test Hey how are you?")).isTrue();
            assertThat(channel.getLastReceivedMessage()).isNull();
            assertThat(cleaned(player.nextMessage())).contains("You don't have permission to send messages to the 'test' channel.");
        }

        @Test
        void clickOnChannel_joinsChannel() {
            Channel channel = createChannel("test");
            String command = Constants.Commands.JOIN_CHANNEL.apply(channel).replaceFirst("/", "");
            Chatter chatter = Chatter.player(player);
            assertThat(chatter.getActiveConversation()).isNotEqualTo(channel);

            assertThat(player.performCommand(command)).isTrue();
            assertThat(chatter.getActiveConversation()).isEqualTo(channel);
        }

        @Test
        void leave_unsubscribesFromChannel() throws AccessDeniedException {
            Channel channel = createChannel("test");
            Chatter chatter = Chatter.player(player);
            chatter.join(channel);
            assertThat(chatter.getActiveConversation()).isEqualTo(channel);

            assertThat(player.performCommand("leave test")).isTrue();
            assertThat(chatter.getActiveConversation())
                    .isNotNull().isNotEqualTo(channel);
            assertThat(chatter.getConversations()).doesNotContain(channel);
        }
    }

    @Nested
    class DirectMessages {
        @Test
        void directMessage_sendsMessageToBothPlayers() {
            PlayerMock player1 = server.addPlayer();
            player.performCommand("tell Player1 Hey whats up?");

            assertLastMessage(player, "Hey whats up?");
            assertLastMessage(player1, "Hey whats up?");
        }

        @Test
        void directMessage_opensDirectConversation() {
            PlayerMock player1 = server.addPlayer();
            player.performCommand("dm Player1 Hi");

            assertThat(Chatter.player(player).getActiveConversation())
                    .isNotNull()
                    .isInstanceOf(PrivateConversation.class)
                    .extracting(ChatTarget::getLastReceivedMessage)
                    .extracting(Message::getText)
                    .isEqualTo(text("Hi"));
        }

        @Test
        void clickOnDirectConversationSetsConversationActive() {
            PlayerMock player1 = server.addPlayer();
            player.performCommand("w Player1 Hey!");

            Chatter chatter = Chatter.player(player);
            Conversation directConversation = chatter.getActiveConversation();
            assertThat(directConversation)
                    .isNotNull()
                    .isInstanceOf(PrivateConversation.class);
            chatter.setActiveConversation(createChannel("test"));

            player.performCommand(Constants.Commands.JOIN_CONVERSATION.apply(directConversation).replace("/", ""));
            assertThat(chatter.getActiveConversation())
                    .isEqualTo(directConversation);
        }

        @Test
        void directMessage_toOpenConversation() {
            PlayerMock player1 = server.addPlayer();
            Chatter target = Chatter.player(player1);
            player.performCommand(Constants.Commands.PRIVATE_MESSAGE.apply(target).replace("/", ""));

            Chatter sender = Chatter.player(player);
            assertThat(sender.getActiveConversation())
                    .isNotNull()
                    .isInstanceOf(PrivateConversation.class)
                    .extracting(Conversation::getTargets)
                    .asList()
                    .contains(sender, target);
            assertThat(target.getActiveConversation())
                    .isNotInstanceOf(PrivateConversation.class);
        }

        @Test
        void directMessage_cannotSendToSelf() {
            player.performCommand("dm Player0");

            assertThat(cleaned(player.nextMessage())).isEqualTo("You can't send messages to yourself.");
        }
    }

    @Nested
    class MessageModeration {

        private Chatter chatter;
        private Message message;

        @BeforeEach
        void setUp() {
            chatter = Chatter.player(player);
            message = chatter.sendMessage("hi");
        }

        @Test
        void selectMessage_selectsMessageInView() {
            selectMessage();
            assertThat(chatter.getView().selectedMessage())
                    .isPresent().get()
                    .isEqualTo(message);
            assertThat(cleaned(getLastMessage(player))).contains("> hi")
                    .contains(" [Delete]  [Abort] ");
        }

        @Test
        void selectMessage_again_deselectsMessage() {
            selectMessage();
            selectMessage();
            assertThat(chatter.getView().selectedMessage()).isEmpty();
            assertThat(toCleanText(chatter.getView().footer())).doesNotContain("[Abort]");
        }

        @Test
        void selectMessage_setsFooterTextToModerationMode() {
            selectMessage();
            assertThat(toCleanText(chatter.getView().footer()))
                    .contains("[Delete]  [Abort]");
        }

        @Test
        void selectMessage_onlyShowsButtonWithPermission() {
            final PlayerMock player = server.addPlayer();
            Chatter.player(player).sendMessage(message);
            player.addAttachment(plugin, Constants.PERMISSION_SELECT_MESSAGE, true);
            player.performCommand("schat message select " + message.getId());
            assertThat(cleaned(getLastMessage(player))).doesNotContain("[Delete]");
        }

        @Test
        void delete_removesMessage() {
            selectMessage();
            player.performCommand("schat message delete " + message.getId());
            assertThat(chatter.getReceivedMessages()).doesNotContain(message);
            assertThat(cleaned(getLastMessage(player)))
                    .doesNotContain("hi")
                    .doesNotContain("[Abort]");
        }

        private void selectMessage() {
            player.performCommand("schat message select " + message.getId());
        }
    }

    private void assertLastMessage(PlayerMock player, String message) {
        assertThat(Chatter.player(player).getLastReceivedMessage())
                .isNotNull()
                .extracting(Message::getText)
                .isEqualTo(text(message));
    }
}