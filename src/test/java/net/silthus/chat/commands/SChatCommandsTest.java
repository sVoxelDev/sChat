package net.silthus.chat.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.md_5.bungee.api.ChatColor;
import net.silthus.chat.Channel;
import net.silthus.chat.Chatter;
import net.silthus.chat.TestBase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SChatCommandsTest extends TestBase {

    @Nested
    class ChannelCommands {

        @Test
        void join_JoinsPlayerToChannel() {

            Channel channel = new Channel("test");
            plugin.getChannelRegistry().add(channel);

            PlayerMock player = server.addPlayer();
            player.addAttachment(plugin, channel.getPermission(), true);

            assertThat(player.performCommand("schat channel join test")).isTrue();
            assertThat(channel.getTargets()).contains(Chatter.of(player));
            assertThat(player.nextMessage()).contains(ChatColor.GRAY + "You joined the channel: " + ChatColor.GOLD + "test" + ChatColor.GRAY + ".");
        }

        @Test
        void join_withoutPermission_fails() {

            Channel channel = new Channel("test");
            plugin.getChannelRegistry().add(channel);

            PlayerMock player = server.addPlayer();

            assertThat(player.performCommand("schat channel join test")).isTrue();
            assertThat(channel.getTargets()).isEmpty();
            assertThat(player.nextMessage()).contains(ChatColor.RED + "You don't have permission to access the 'test' channel.");
        }
    }
}