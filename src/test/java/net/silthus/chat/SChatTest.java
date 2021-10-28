package net.silthus.chat;

import co.aikar.commands.BukkitCommandManager;
import net.md_5.bungee.api.ChatColor;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class SChatTest extends TestBase {

    @Test
    void instance_isSet() {
        assertThat(SChat.instance()).isNotNull();
    }

    @Test
    void createThrows() {
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(SChat::new);
    }

    @Test
    void onEnable_registersCommands() {

        assertThat(plugin.getCommandManager())
                .isNotNull()
                .extracting(BukkitCommandManager::hasRegisteredCommands)
                .isEqualTo(true);
    }

    @Test
    void onEnable_loadsChannelsFromConfig() {

        assertThat(plugin.getChannelRegistry().getChannels())
                .hasSize(1);
        Optional<Channel> channel = plugin.getChannelRegistry().getChannels().stream().findFirst();
        assertThat(channel)
                .isPresent().get()
                .extracting(
                        Channel::getIdentifier,
                        Channel::getName,
                        c -> c.getConfig().getFormat().applyTo(Message.of("test")),
                        c -> c.getConfig().getFormat().applyTo(Message.of(ChatSource.of(server.addPlayer()), "test"))
                ).contains(
                        "global",
                        "Global",
                        ChatColor.GREEN + "test",
                        ChatColor.GOLD + "[" + ChatColor.GREEN + "G" + ChatColor.GOLD + "]" + ChatColor.RESET + "Player0: " + ChatColor.GREEN + "test"
                );
    }
}