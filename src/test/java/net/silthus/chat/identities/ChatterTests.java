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
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.silthus.chat.*;
import net.silthus.chat.conversations.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static net.silthus.chat.Message.message;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChatterTests extends TestBase {
    private PlayerMock player;

    private Chatter chatter;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player = server.addPlayer();
        player.addAttachment(plugin, Constants.Permissions.getChannelPermission(ChatTarget.channel("test")), true);
        chatter = plugin.getChatterManager().registerChatter(spy(Chatter.of(player)));
    }

    private void sendMessage(Player source, String message) {
        ChatSource.player(source).message(message).to(chatter).send();
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
    void create_registersOnChatListener() {

        Bukkit.getPluginManager().registerEvents(chatter, plugin);
        assertThat(getRegisteredListeners()).contains(chatter);
    }

    @Test
    void of_usesGlobalChatterCache() {
        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        Message message = message("test").send();
        chatter.addReceivedMessage(message);

        Chatter newChatter = Chatter.of(player);
        assertThat(newChatter).isSameAs(chatter);
        assertThat(newChatter.getLastReceivedMessage()).isEqualTo(message);
    }

    @Test
    void equals_isTrue_forSamePlayer() {

        PlayerMock player = server.addPlayer();

        Chatter chatter0 = Chatter.of(player);
        Chatter chatter1 = Chatter.of(player);

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

        assertReceivedMessage(player, "Player1: Hi");
    }

    @Test
    void sendMessage_storesLastMessage() {
        PlayerMock sender = server.addPlayer();
        sendMessage(sender, "Hi there");

        assertThat(chatter.getLastReceivedMessage())
                .isNotNull()
                .extracting(this::toText)
                .isEqualTo("Player1: Hi there");
    }

    @Test
    void getActiveChannel() {
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
        assertThat(channel.getTargets()).isEmpty();
        chatter.setActiveConversation(channel);
        assertThat(channel.getTargets()).contains(chatter);
    }

    @Test
    void setActiveChannel_toNull() {
        Channel channel = ChatTarget.channel("test");
        chatter.setActiveConversation(channel);
        chatter.setActiveConversation(null);
        assertThat(chatter.getActiveConversation()).isNull();
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

        assertThat(channel.getTargets()).hasSize(1);
        assertThat(chatter.getConversations()).containsOnlyOnce(channel);
    }

    @Test
    void subscribe_sendsMessageToSubscriber() {
        Channel channel = ChatTarget.channel("test");
        chatter.subscribe(channel);

        Message message = channel.sendMessage("test");

        assertThat(chatter.getLastReceivedMessage())
                .isNotNull()
                .extracting(
                        Message::getParent,
                        this::toText,
                        Message::getSource
                ).contains(
                        message,
                        "&6[&atest&6]&7: test",
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

    @Nested
    @DisplayName("direct messaging")
    class DirectMessages {

        private Chatter sender;
        private PlayerMock sendingPlayer;
        private Chatter receiver;
        private PlayerMock receivingPlayer;

        @BeforeEach
        void setUp() {
            sendingPlayer = server.addPlayer();
            sender = Chatter.of(sendingPlayer);
            receivingPlayer = server.addPlayer();
            receiver = Chatter.of(receivingPlayer);
        }

        @Test
        void directMessage_isReceivedByOtherPlayer() {
            Message message = sender.message("Hi player!").to(receiver).send();

            assertThat(receiver.getLastReceivedMessage()).isEqualTo(message);
            assertThat(receivingPlayer.nextMessage()).isEqualTo("Player1: Hi player!");
        }
    }

    @Nested
    @DisplayName("with player chatting")
    class PlayerChatEvent {

        private ArgumentCaptor<AsyncChatEvent> eventCaptor;

        @BeforeEach
        public void setUp() {
            Bukkit.getPluginManager().registerEvents(chatter, plugin);
            eventCaptor = ArgumentCaptor.forClass(AsyncChatEvent.class);
        }

        @Test
        void onChat_catchesChatEvent() {

            AsyncChatEvent event = chat("Hello!");

            assertThat(event.message()).isEqualTo(Component.text("Hello!"));
        }

        @Test
        void onChat_forwardsMessageToActiveChannel() {

            Channel channel = ChatTarget.channel("test");
            chatter.setActiveConversation(channel);
            AsyncChatEvent event = chat("Hi");

            assertThat(channel.getLastReceivedMessage())
                    .isNotNull()
                    .extracting(ChatterTests.this::toText)
                    .isEqualTo("&6[&atest&6]&ePlayer0[!]&7: Hi");
            assertThat(event.isCancelled()).isTrue();
        }

        @Test
        void onChat_withNoActiveChannel_sendsPlayerAnErrorMessage() {

            AsyncChatEvent event = chat("Hi");

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
            AsyncChatEvent event = chat(player2, "hi");

            assertThat(event.isCancelled()).isFalse();
            assertThat(player2.nextMessage()).isNull();
            assertThat(channel.getLastReceivedMessage()).isNull();
        }

        private AsyncChatEvent chat(String message) {
            return chat(player, message);
        }

        private AsyncChatEvent chat(Player player, String message) {
            player.chat(message);
            server.getScheduler().waitAsyncEventsFinished();
            verify(chatter, atLeastOnce()).onPlayerChat(eventCaptor.capture());
            return eventCaptor.getValue();
        }
    }
}
