package net.silthus.chat.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.md_5.bungee.api.ChatColor;
import net.silthus.chat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        player.addAttachment(plugin, Constants.PERMISSION_PLAYER_CHANNEL_QUICKMESSAGE, true);
    }

    @Nested
    class ChannelCommands {

        @Test
        void join_JoinsPlayerToChannel() {
            Channel channel = ChatTarget.channel("test");
            plugin.getChannelRegistry().add(channel);

            player.addAttachment(plugin, channel.getPermission(), true);

            assertThat(player.performCommand("schat channel join test")).isTrue();
            assertThat(channel.getTargets()).contains(Chatter.of(player));
            assertThat(player.nextMessage()).contains(ChatColor.GRAY + "You joined the channel: " + ChatColor.GOLD + "test" + ChatColor.GRAY + ".");
        }

        @Test
        void join_withoutPermission_fails() {
            Channel channel = createChannel(cfg -> cfg.protect(true));
            plugin.getChannelRegistry().add(channel);

            assertThat(player.performCommand("ch test")).isTrue();
            assertThat(channel.getTargets()).isEmpty();
            assertThat(player.nextMessage()).contains(ChatColor.RED + "You don't have permission to access the 'test' channel.");
        }

        @Test
        void quickMessage_sendsMessageToChannel() {
            Channel channel = ChatTarget.channel("test");
            plugin.getChannelRegistry().add(channel);

            assertThat(player.performCommand("ch test Hey how are you?")).isTrue();
            assertThat(channel.getLastReceivedMessage()).isNotNull();
            assertThat(toText(channel.getLastReceivedMessage())).contains("Player0&7: &aHey how are you?");
        }

        @Test
        void quickMessage_noPermission_fails() {
            Channel channel = createChannel(config -> config.protect(true));
            plugin.getChannelRegistry().add(channel);

            assertThat(player.performCommand("ch test Hey how are you?")).isTrue();
            assertThat(channel.getLastReceivedMessage()).isNull();
            assertThat(player.nextMessage()).contains(ChatColor.RED + "You don't have permission to send messages to the 'test' channel.");
        }
    }
}