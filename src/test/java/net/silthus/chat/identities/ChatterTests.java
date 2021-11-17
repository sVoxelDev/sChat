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

package net.silthus.chat.identities;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.*;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.persistence.PlayerData;
import net.silthus.chat.renderer.View;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.chat.Constants.Persistence.PLAYER_DATA;
import static net.silthus.chat.Message.message;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChatterTests extends TestBase {
    private PlayerMock player;
    private PlayerChatter chatter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player = new PlayerMock(server, "Test");
        player.addAttachment(plugin, Constants.Permissions.getChannelPermission(ChatTarget.channel("test")), true);
        chatter = (PlayerChatter) plugin.getChatterManager().registerChatter(spy(new PlayerChatter(player)));
        chatter.setView(new View(chatter));
        server.addPlayer(player);
    }

    private Message sendMessage(Player source, String message) {
        return ChatSource.player(source).message(message).to(chatter).send();
    }

    @Test
    void create_PlayerChatter() {
        assertThat(chatter)
                .isInstanceOf(Listener.class)
                .extracting(
                        Identity::getUniqueId,
                        Chatter::getName,
                        c -> toText(c.getDisplayName())
                ).contains(
                        player.getUniqueId(),
                        player.getName(),
                        player.getDisplayName()
                );
    }

    @Test
    void createFromIdentity() {
        Chatter chatter = Chatter.chatter(Identity.identity(UUID.randomUUID(), "Test", text("Test")));

        assertThat(plugin.getChatterManager().getChatters()).contains(chatter);
        assertThat(chatter.getName()).isEqualTo("Test");
    }

    @Test
    void create_registersOnChatListener() {

        Bukkit.getPluginManager().registerEvents(chatter, plugin);
        assertThat(getRegisteredListeners()).contains(chatter);
    }

    @Test
    void of_usesGlobalChatterCache() {
        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.player(player);
        Message message = message("test").to(chatter).send();

        Chatter newChatter = Chatter.player(player);
        assertThat(newChatter).isSameAs(chatter);
        assertThat(newChatter.getLastReceivedMessage()).isEqualTo(message);
    }

    @Test
    void equals_isTrue_forSamePlayer() {

        PlayerMock player = server.addPlayer();

        Chatter chatter0 = Chatter.player(player);
        Chatter chatter1 = Chatter.player(player);

        assertThat(chatter0).isEqualTo(chatter1);
    }

    @Test
    void sendMessage_sendsMessageToPlayer() {
        chatter.sendMessage("Hello Chatter!");

        assertReceivedMessage(player, "Hello Chatter!");
    }

    @Test
    void sendMessage_formatsTheMessageIfNotFormatted() {
        sendMessage(server.addPlayer(), "Hi");

        assertLastReceivedMessage(player, "Player0: Hi");
    }

    @Test
    void sendMessage_storesLastMessage() {
        PlayerMock sender = server.addPlayer();
        sendMessage(sender, "Hi there");

        assertThat(chatter.getLastReceivedMessage())
                .isNotNull()
                .extracting(this::toCleanText)
                .isEqualTo("Player0: Hi there");
    }

    @Test
    void sendMessage_appendsActiveChannelFooter() {
        chatter.setActiveConversation(ChatTarget.channel("test"));
        chatter.sendMessage(Message.message("test").build());

        assertThat(cleaned(player.nextMessage())).contains("\u2502 \u2718test \u2502");
    }

    @Test
    void sendMessage_sendsAllPreviousChannelMessages() {
        Channel channel = createChannel("test");
        chatter.setActiveConversation(channel);
        for (int i = 1; i < 5; i++) {
            message(i + "").to(channel).send();
        }

        assertLastReceivedMessage(player, """
                [test]N/A: 1
                [test]N/A: 2
                [test]N/A: 3
                [test]N/A: 4
                """);
    }

    @Test
    void sendMessage_isOnlyDisplayedIfChannelIsActive() {
        Channel channel = createChannel("test");
        Channel other = createChannel("other");
        chatter.subscribe(other);
        chatter.setActiveConversation(channel);

        other.sendMessage("foobar");
        assertThat(cleaned(player.nextMessage())).doesNotContain("foobar");
    }

    @Test
    void sendMessage_printsAllLastSystemMessages() throws InterruptedException {
        Channel channel = createChannel("msgtest");
        chatter.setActiveConversation(channel);
        Message.message("system 1").to(chatter).send();
        Thread.sleep(1L);
        channel.sendMessage("channel 2");
        Thread.sleep(1L);
        Message.message("system 3").to(chatter).send();
        Thread.sleep(1L);
        Message.message("test").from(Chatter.player(server.addPlayer())).to(chatter).send();
        Thread.sleep(1L);
        chatter.setActiveConversation(channel);
        Message.message("channel 4").from(Chatter.player(server.addPlayer())).to(channel).send();

        assertLastReceivedMessage(player, """
                system 1
                [msgtest]N/A: channel 2
                system 3
                [msgtest][ADMIN]Player1[!]: channel 4
                """);
    }

    @Test
    void sendMessage_systemMessages_doNotRender_inPrivateMessages() {
        PlayerMock sourcePlayer = server.addPlayer();
        Chatter source = Chatter.player(sourcePlayer);
        PlayerMock targetPlayer = server.addPlayer();
        Chatter target = Chatter.player(targetPlayer);
        source.sendMessage("system");
        source.message("hi").to(target).send();

        assertLastReceivedMessage(sourcePlayer, "Player0: hi");
        assertLastReceivedMessage(targetPlayer, "Player0: hi");
    }

    @Test
    void getLastReceivedMessage_returnsTheLatestMessage() {
        PlayerMock sender = server.addPlayer();
        sendMessage(sender, "Hey 1");
        sendMessage(sender, "Hey 2");
        Message message = sendMessage(sender, "Hey 3");

        assertThat(chatter.getLastReceivedMessage()).isEqualTo(message);
    }

    @Test
    void getActiveChannel() {
        Chatter chatter = ChatTarget.player(new PlayerMock(server, "test"));
        Conversation channel = chatter.getActiveConversation();

        assertThat(channel).isNull();
    }

    @Test
    void setActiveChannel_setsChannel() {
        Channel channel = ChatTarget.channel("test");
        chatter.setActiveConversation(channel);

        assertThat(chatter.getActiveConversation())
                .isEqualTo(channel);
    }

    @Test
    void setActiveChannel_addsPlayerAsTargetToChannel() {
        Channel channel = ChatTarget.channel("test");
        assertThat(channel.getTargets()).doesNotContain(chatter);
        chatter.setActiveConversation(channel);
        assertThat(channel.getTargets()).contains(chatter);
    }

    @Test
    void setActiveChannel_toDefaultChannel() {
        Channel channel = ChatTarget.channel("test");
        chatter.setActiveConversation(channel);
        chatter.setActiveConversation(null);
        assertThat(chatter.getActiveConversation()).isNotNull()
                .isIn(chatter.getConversations());
    }

    @Test
    void subscribe_addsChatterAsChannelTarget() {
        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);

        assertThat(channel.getTargets()).contains(chatter);
        assertThat(chatter.getConversations())
                .contains(channel);
    }

    @Test
    void subscribe_onlyAddsChatterOnce() {
        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);
        chatter.subscribe(channel);

        assertThat(channel.getTargets()).containsOnlyOnce(chatter);
        assertThat(chatter.getConversations()).containsOnlyOnce(channel);
    }

    @Test
    void subscribe_sendsMessageToSubscriber() {
        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);

        Message message = channel.sendMessage("test");

        assertThat(chatter.getLastReceivedMessage())
                .isNotNull()
                .isEqualTo(message)
                .extracting(
                        Message::getText,
                        Message::getSource
                ).contains(
                        text("test"),
                        ChatSource.nil()
                );
    }

    @Test
    void not_subscribed_doesNotSendMessageToChatter() {
        Channel channel = ChatTarget.channel("foobar");
        channel.sendMessage("test");

        assertThat(chatter.getLastReceivedMessage()).isNull();
    }

    @Test
    void unsubscribe_doesNothingIfNotSubscribed() {
        assertThatCode(() -> chatter.unsubscribe(ChatTarget.channel("test")))
                .doesNotThrowAnyException();
    }

    @Test
    void unsubscribe_removesSubscription() {
        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);
        assertThat(chatter.getConversations()).contains(channel);

        chatter.unsubscribe(channel);
        assertThat(chatter.getConversations()).doesNotContain(channel);
    }

    @Test
    void unsubscribe_doesNotSendMessageToOldSubscriber() {
        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);

        chatter.unsubscribe(channel);
        channel.sendMessage("test");
        assertThat(chatter.getLastReceivedMessage()).isNull();
    }

    @Test
    void join_subscribesAndSetsActiveChannel() throws AccessDeniedException {
        Channel channel = ChatTarget.channel("test");
        chatter.join(channel);
        assertThat(chatter.getConversations()).contains(channel);
        assertThat(channel.getTargets()).contains(chatter);
        assertThat(chatter.getActiveConversation()).isSameAs(channel);
    }

    @Test
    void join_throwsIfPlayerCannotJoinChannel() {
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> chatter.join(createChannel("foo", cfg -> cfg.protect(true))));
    }

    @Test
    void canJoin_returnsTrueIfPlayerCanJoinTheChannel() {
        assertThat(chatter.canJoin(ChatTarget.channel("test"))).isTrue();
    }

    @Test
    void canJoin_isFalse_ifPlayerHasNoPermission() {
        assertThat(chatter.canJoin(createChannel("foobar", cfg -> cfg.protect(true)))).isFalse();
    }

    @Test
    void sendMessage_doesNotSendDuplicateMessage() {
        Message message = message("hi").format(Format.noFormat()).build();

        chatter.sendMessage(message);
        assertReceivedMessage(player, "hi");

        chatter.sendMessage(message);
        assertThat(player.nextMessage()).isNull();
    }

    @Test
    void sendGlobalMessage_ifChatterIsOffline() {
        Message message = message("hi").format(Format.noFormat()).build();
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.randomUUID());
        Chatter chatter = Chatter.player(player);
        chatter.sendMessage(message);

        assertThat(chatter.getLastReceivedMessage())
                .isNotNull().isEqualTo(message);
        verify(plugin.getBungeecord()).sendMessage(message);
    }

    @Test
    void view_containsTheLastMessagesThePlayerSaw() {
        Message message = message("hi").build();
        chatter.sendMessage(message);

        View view = chatter.getView();
        assertThat(view.messages()).contains(message);
    }

    @Test
    void canLeave_isTrueByDefault() {
        final Channel channel = createChannel("test");
        assertThat(chatter.canLeave(channel)).isTrue();
    }

    @Test
    void canLeave_isFalseIfSet() {
        final Channel channel = createChannel(config -> config.canLeave(false));
        assertThat(chatter.canLeave(channel)).isFalse();
    }

    @Test
    void canLeave_isTrue_ifConversation() {
        final Conversation conversation = Conversation.direct(chatter, Chatter.player(server.addPlayer()));
        assertThat(chatter.canLeave(conversation)).isTrue();
    }

    @Test
    void getMessage_returnsEmpty_unknownMessageId() {
        assertThat(chatter.getMessage(UUID.randomUUID())).isEmpty();
    }

    @Test
    void getMessage_returnsMessage() {
        final Message message = chatter.sendMessage("test");
        assertThat(chatter.getMessage(message.getId()))
                .isPresent().get().isEqualTo(message);
    }

    @Nested
    class SaveAndLoad {

        private PlayerMock player;
        private Chatter chatter;

        @BeforeEach
        void setUp() {
            player = server.addPlayer();
            chatter = Chatter.player(player);
        }

        @Test
        void save_storesPlayerSettingsInDataContainer() {
            final Channel channel = createChannel(config -> config.name("Test 1"));
            chatter.setActiveConversation(channel);

            chatter.save();

            assertSavedPlayerData(channel.getUniqueId(), channel.getName());
        }

        @Test
        void save_withNullActiveConversation_savesNull() {
            chatter.clearConversations();
            chatter.setActiveConversation(null);

            chatter.save();

            assertSavedPlayerData(null, null);
        }

        @Test
        void load_restoresActiveChannel() {
            final Channel channel = createChannel("test");
            chatter.setActiveConversation(channel);
            chatter.save();
            plugin.getChatterManager().removeChatter(chatter);

            final Chatter chatter = Chatter.player(player);
            chatter.load();
            assertThat(chatter.getActiveConversation()).isEqualTo(channel);
        }

        @Test
        void load_withoutSave_doesNothing() {
            final Channel channel = createChannel("foobar");
            chatter.setActiveConversation(channel);

            chatter.load();
            assertThat(chatter.getActiveConversation()).isEqualTo(channel);
        }

        @Test
        void load_withNullConversation_loads() {
            chatter.clearConversations();
            chatter.save();

            chatter.load();
            assertThat(chatter.getActiveConversation()).isNull();
        }

        @Test
        void save_doesNothingIfPlayerIsOffline() {
            final PlayerMock player = new PlayerMock(server, "test");
            final Chatter chatter = Chatter.player(player);
            chatter.save();

            assertThat(player.getPersistentDataContainer().get(PLAYER_DATA, PlayerData.type())).isNull();
        }

        @Test
        void load_doesNothingIfPlayerIsOffline() {
            final PlayerMock player = new PlayerMock(server, "test");
            final Chatter chatter = Chatter.player(player);
            chatter.load();

            assertThat(chatter.getActiveConversation()).isNull();
        }

        private void assertSavedPlayerData(UUID id, String name) {
            assertThat(player.getPersistentDataContainer().get(PLAYER_DATA, PlayerData.type()))
                    .isNotNull()
                    .extracting(
                            PlayerData::activeConversationId,
                            PlayerData::activeConversationName
                    ).contains(
                            id,
                            name
                    );
        }
    }

    @Nested
    class UnreadMessages {

        private Channel test;
        private Channel foo;

        @BeforeEach
        void setUp() {
            test = createChannel("test");
            foo = createChannel("foo");
            chatter.setActiveConversation(test);
            chatter.subscribe(foo);
        }

        @Test
        void containsUnreadMessages() {
            final Message message = foo.sendMessage("hi");

            assertThat(chatter.getUnreadMessages(foo)).hasSize(1).contains(message);
            assertThat(chatter.getUnreadMessages(test)).isEmpty();
        }

        @Test
        void clearsUnreadMessages_onConversationActive() {
            final Message message = foo.sendMessage("hi");

            assertThat(chatter.getUnreadMessages(foo)).hasSize(1).contains(message);
            chatter.setActiveConversation(foo);
            assertThat(chatter.getUnreadMessages(foo)).isEmpty();
        }

        @Test
        void unsubscribe_removesUnreadMessages() {

            final Message message = foo.sendMessage("hi");
            assertThat(chatter.getUnreadMessages(foo)).hasSize(1).contains(message);
            chatter.unsubscribe(foo);
            assertThat(chatter.getUnreadMessages(foo)).isEmpty();
        }
    }

    @Nested
    @DisplayName("direct messaging")
    class DirectMessages {

        private Chatter sender;
        private PlayerMock sendingPlayer;
        private PlayerChatter receiver;
        private PlayerMock receivingPlayer;

        @BeforeEach
        void setUp() {
            sendingPlayer = server.addPlayer();
            sender = Chatter.player(sendingPlayer);
            receivingPlayer = server.addPlayer();
            receiver = (PlayerChatter) Chatter.player(receivingPlayer);
        }

        @Test
        void directMessage_isReceivedByOtherPlayer() {
            Message message = sender.message("Hi player!").to(receiver).send();

            assertThat(receiver.getLastReceivedMessage()).isEqualTo(message);
            assertReceivedMessage(receivingPlayer, "Player0: Hi player!");
        }
    }

    @Nested
    @DisplayName("with player chatting")
    class PlayerChatEvent {

        private ArgumentCaptor<AsyncPlayerChatEvent> eventCaptor;

        @BeforeEach
        public void setUp() {
            Bukkit.getPluginManager().registerEvents(chatter, plugin);
            eventCaptor = ArgumentCaptor.forClass(AsyncPlayerChatEvent.class);
        }

        @Test
        void onChat_catchesChatEvent() {
            AsyncPlayerChatEvent event = chat("Hello!");

            assertThat(event.getMessage()).isEqualTo("Hello!");
        }

        @Test
        void onChat_forwardsMessageToActiveChannel() {
            Channel channel = ChatTarget.channel("test");
            chatter.setActiveConversation(channel);
            AsyncPlayerChatEvent event = chat("Hi");

            assertThat(channel.getLastReceivedMessage())
                    .isNotNull()
                    .extracting(Message::getText)
                    .isEqualTo(text("Hi"));
            assertThat(event.isCancelled()).isTrue();
        }

        @Test
        void onChat_withNoActiveChannel_sendsPlayerAnErrorMessage() {
            chatter.clearConversations();
            AsyncPlayerChatEvent event = chat("Hi");

            assertThat(player.nextMessage())
                    .isEqualTo(Constants.Errors.NO_ACTIVE_CHANNEL);
            assertThat(chatter.getLastReceivedMessage()).isNull();
            assertThat(event.isCancelled()).isTrue();
        }

        @Test
        void onChat_onlyReactsToChatter() {
            Channel channel = ChatTarget.channel("test");
            chatter.setActiveConversation(channel);

            PlayerMock player2 = new PlayerMock(server, "test");
            AsyncPlayerChatEvent event = chat(player2, "hi");

            assertThat(event.isCancelled()).isFalse();
            assertThat(player2.nextMessage()).isNull();
            assertThat(channel.getLastReceivedMessage()).isNull();
        }

        private AsyncPlayerChatEvent chat(String message) {
            return chat(player, message);
        }

        private AsyncPlayerChatEvent chat(Player player, String message) {
            player.chat(message);
            server.getScheduler().waitAsyncEventsFinished();
            verify(chatter, atLeastOnce()).onPlayerChat(eventCaptor.capture());
            return eventCaptor.getValue();
        }
    }
}
