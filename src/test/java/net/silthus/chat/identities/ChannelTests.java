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
import net.kyori.adventure.text.Component;
import net.silthus.chat.*;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.scopes.GlobalScope;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ChannelTests extends TestBase {

    private Channel channel;

    @BeforeEach
    public void setUp() {
        super.setUp();

        channel = ChatTarget.channel("test");
    }

    @Test
    void create() {
        assertThat(channel)
                .isInstanceOf(ChatTarget.class);
        assertThat(channel)
                .extracting(
                        Channel::getName,
                        Channel::getPermission
                ).contains(
                        "test",
                        Constants.Permissions.CHANNEL_PERMISSION + ".test"
                );
        assertThat(channel.getDisplayName())
                .isEqualTo(Component.text("test"));
        assertThat(channel.getConfig())
                .extracting(ChannelConfig::name)
                .isEqualTo(null);
        assertThat(channel.getConfig().format())
                .isNotNull();
        assertThat(plugin.getChannelRegistry().getChannels()).contains(channel);
    }

    @Test
    void create_withConfig_isRegistered() {
        Channel channel = Channel.channel("test", ChannelConfig.builder().protect(true).autoJoin(false).build());
        assertThat(plugin.getChannelRegistry().getChannels()).contains(channel);
    }

    @Test
    void create_lowerCasesIdentifier() {
        Channel channel = ChatTarget.channel("TEsT");
        assertThat(channel.getName()).isEqualTo("test");
    }

    @Test
    void equalsBasedOnAlias() {
        Channel channel1 = ChatTarget.channel("test");
        Channel channel2 = ChatTarget.channel("test");

        assertThat(channel1).isEqualTo(channel2);
    }

    @Test
    void hasEmptyTargetList_ifNoConsole() {
        Channel channel = createChannel(config -> config.sendToConsole(false));
        assertThat(channel.getTargets()).isEmpty();
    }

    @Test
    void hasConsoleByDefault() {
        assertThat(channel.getTargets()).containsOnly(Console.console());
    }

    @Test
    void getTargets_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> channel.getTargets().add(Chatter.of(server.addPlayer())));
    }

    @Test
    void join_addsPlayerToChannelTargets() {
        Chatter player = Chatter.of(server.addPlayer());
        channel.addTarget(player);

        assertThat(channel.getTargets())
                .contains(player);
    }

    @Test
    void join_canOnlyJoinChannelOnce() {
        Chatter player = Chatter.of(server.addPlayer());
        channel.addTarget(player);
        channel.addTarget(player);

        assertThat(channel.getTargets())
                .containsOnlyOnce(player);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void join_throwsNullPointer_ifPlayerIsNull() {

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> channel.subscribe(null));
    }

    @Test
    void leave_removesChatTarget_fromChannelTargets() {
        Chatter chatter = Chatter.of(server.addPlayer());
        channel.addTarget(chatter);
        assertThat(channel.getTargets()).contains(chatter);

        channel.removeTarget(chatter);

        assertThat(channel.getTargets()).doesNotContain(chatter);
    }

    @Test
    void leave_doesNothingIfPlayerIsNotJoined() {

        Chatter player = Chatter.of(server.addPlayer());
        channel.addTarget(player);
        assertThatCode(() -> channel.removeTarget(Chatter.of(server.addPlayer())))
                .doesNotThrowAnyException();
        assertThat(channel.getTargets()).contains(player);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void leave_throwsNullPointer_ifPlayerIsNull() {

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> channel.unsubscribe(null));
    }

    @Test
    void sendMessage_sendsMessageToNobody() {

        PlayerMock player = server.addPlayer();
        Chatter chatter = ChatSource.player(player);
        chatter.message("test").to(createChannel("nousers", config -> config.sendToConsole(false))).send();

        assertThat(channel.getTargets()).doesNotContain(chatter);
        assertThat(player.nextMessage()).isNull();
    }

    @Test
    void sendMessage_sendsMessageToAllJoinedTargets() {

        PlayerMock player0 = server.addPlayer();
        Chatter chatter0 = Chatter.of(player0);
        chatter0.setActiveConversation(channel);
        PlayerMock player1 = server.addPlayer();
        Chatter chatter1 = Chatter.of(player1);
        chatter1.setActiveConversation(channel);
        PlayerMock player2 = server.addPlayer();

        chatter0.message("test").to(channel).send();

        assertThat(cleaned(player0.nextMessage())).contains("test");
        assertThat(cleaned(player1.nextMessage())).contains("test");
        assertThat(player2.nextMessage()).isNull();
    }

    @Test
    void sendFormattedMessage_doesNotFormatAgain() {

        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        chatter.setActiveConversation(channel);

        ChatSource.player(server.addPlayer())
                .message("test")
                .to(channel)
                .format(Format.defaultFormat())
                .send();

        assertReceivedMessage(player, "Player1: test");
    }

    @Test
    void sendMessage_storesLastMessage() {

        try {
            PlayerMock player = server.addPlayer();
            Chatter chatter = Chatter.of(player);
            channel.addTarget(chatter);

            Message message = ChatSource.player(server.addPlayer())
                    .message("test")
                    .to(channel)
                    .send();

            assertThat(channel.getLastReceivedMessage())
                    .isNotNull()
                    .isEqualTo(message)
                    .extracting(
                            m -> m.getSource().getDisplayName()
                    ).isEqualTo(
                            Component.text("Player1")
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void sendMessage_storesFormattedChannelMessage() {
        Channel channel = Channel.channel("test", ChannelConfig.defaults().format(Format.miniMessage("[<channel_name]<sender_name>: <message>")));
        Message message = ChatSource.named("test").message("test").to(channel).send();

        assertThat(channel.getLastReceivedMessage())
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    void getLastReceivedMessage_returnsNullByDefault() {

        assertThat(channel.getLastReceivedMessage())
                .isNull();
    }

    @Test
    void getReceivedMessages_isEmpty() {

        assertThat(channel.getReceivedMessages())
                .isEmpty();
    }

    @Test
    void sendMultipleMessage_returnedbyLastMessages() {

        ChatSource source1 = ChatSource.player(server.addPlayer());
        ChatSource source2 = ChatSource.player(server.addPlayer());

        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        channel.addTarget(chatter);

        source1.message("test").to(channel).send();
        source2.message("foobar").to(channel).send();
        channel.sendMessage("Heyho");

        assertThat(channel.getReceivedMessages())
                .hasSize(3)
                .extracting(message -> toText(message.getText()))
                .containsExactly(
                        "test",
                        "foobar",
                        "Heyho"
                );
    }

    @Test
    void sendMessage_setsTargetToChannel() {

        Chatter chatter = Chatter.of(server.addPlayer());
        channel.addTarget(chatter);

        ChatSource.player(server.addPlayer())
                .message("test")
                .to(channel)
                .send();

        assertThat(chatter.getLastReceivedMessage())
                .isNotNull()
                .extracting(Message::getConversation)
                .isSameAs(channel);
    }

    @Test
    void subscribedTarget_isRemoved_onRemoveChatter() {
        PlayerMock player = server.addPlayer();
        Chatter chatter = Chatter.of(player);
        chatter.subscribe(channel);

        assertThat(channel.getTargets()).contains(chatter);
        plugin.getChatterManager().removeChatter(player);
        assertThat(channel.getTargets()).doesNotContain(chatter);
    }

    @Test
    void canJoin_unprotected_isTrue() {
        Channel channel = createChannel(c -> c.protect(false));

        assertThat(Chatter.of(server.addPlayer()).canJoin(channel)).isTrue();
    }

    @Test
    void canJoin_protected_withoutPermission_isFalse() {
        Channel channel = createChannel(c -> c.protect(true));

        assertThat(Chatter.of(server.addPlayer()).canJoin(channel)).isFalse();
    }

    @Test
    void canJoin_protected_withPermission_isTrue() {
        Channel channel = createChannel(c -> c.protect(true));

        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, channel.getPermission(), true);

        assertThat(Chatter.of(player).canJoin(channel)).isTrue();
    }

    @Test
    void canAutoJoin_true_ifConfigured() {

        Channel channel = createChannel(config -> config.autoJoin(true));
        assertThat(Chatter.of(server.addPlayer()).canAutoJoin(channel)).isTrue();
    }

    @Test
    void canAutoJoin_playerWithPermission_notConfigured_isTrue() {
        Channel channel = createChannel(config -> config.autoJoin(false));
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, channel.getAutoJoinPermission(), true);

        assertThat(Chatter.of(player).canAutoJoin(channel)).isTrue();
    }

    @Test
    void canAutoJoin_protectedChannel_noPermission_isFalse() {
        Channel channel = createChannel(config -> config.autoJoin(true).protect(true));

        assertThat(Chatter.of(server.addPlayer()).canAutoJoin(channel)).isFalse();
    }

    @Test
    void canAutoJoin_protected_bothPermissions_isTrue() {
        Channel channel = createChannel(config -> config.protect(true).autoJoin(false));

        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, channel.getPermission(), true);
        player.addAttachment(plugin, channel.getAutoJoinPermission(), true);

        assertThat(Chatter.of(player).canAutoJoin(channel)).isTrue();
    }

    @Test
    void message_isSentToConsole() {
        Channel channel = createChannel(config -> config.sendToConsole(true));

        Message message = channel.sendMessage("test");
        assertThat(Console.console().getLastReceivedMessage())
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    void message_sendToConsole_false_isNotSentToConsole() {
        Channel channel = createChannel(config -> config.sendToConsole(false));

        channel.sendMessage("test");
        assertThat(Console.console().getLastReceivedMessage())
                .isNull();
    }

    @Test
    void message_sendGlobal_if_configured() {
        Channel channel = createChannel(config -> config.scope(new GlobalScope()));

        Message message = channel.sendMessage("test");
        verify(plugin.getBungeecord()).sendMessage(message);
    }

    @Test
    void message_notGlobal_isNotSent_toPluginChannel() {
        Channel channel = createChannel("test");

        channel.sendMessage("test");
        verify(plugin.getBungeecord(), never()).sendMessage(any(Message.class));
    }

    @Test
    void sendMessage_doesNotProcessSameMessageTwice() {
        Channel channel = createChannel(config -> config.scope(new GlobalScope()));
        Message message = channel.sendMessage("test");
        channel.sendMessage(message);

        verify(plugin.getBungeecord()).sendMessage(any(Message.class));
    }

    @Test
    void unsubscribe_removesActiveConversation() {
        Channel channel = createChannel(config -> config);
        Chatter chatter = Chatter.of(server.addPlayer());
        chatter.setActiveConversation(channel);

        chatter.unsubscribe(channel);
        assertThat(chatter.getConversations()).doesNotContain(channel);
        assertThat(chatter.getActiveConversation())
                .isNotNull().isNotEqualTo(channel);
    }

    @Test
    void close_unsubscribesAllTargets() {
        final Channel channel = createChannel(config -> config);
        final Chatter chatter = Chatter.of(server.addPlayer());
        chatter.subscribe(channel);

        channel.close();
        assertThat(channel.getTargets()).isEmpty();
        assertThat(chatter.getConversations()).doesNotContain(channel);
    }

    @Nested
    @DisplayName("with config")
    class WithConfig {

        @Test
        void createFromConfig() {

            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("name", "Test");
            cfg.set("format", "<green><message>");
            cfg.set("console", true);
            cfg.set("protect", true);
            cfg.set("auto_join", true);

            Channel channel = Channel.channel("config-test", ChannelConfig.of(cfg));

            assertThat(channel.getDisplayName()).isEqualTo(Component.text("Test"));
            assertThat(channel.getConfig())
                    .extracting(
                            ChannelConfig::format,
                            ChannelConfig::sendToConsole,
                            ChannelConfig::protect,
                            ChannelConfig::autoJoin
                    ).contains(
                            Format.miniMessage("<green><message>"),
                            true,
                            true,
                            true
                    );
        }
    }
}
