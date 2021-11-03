package net.silthus.chat;

import co.aikar.commands.BukkitCommandManager;
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
                .hasSizeGreaterThanOrEqualTo(1);
        Optional<Channel> channel = plugin.getChannelRegistry().getChannels().stream()
                .filter(c -> c.getIdentifier().equals("global"))
                .findFirst();
        assertThat(channel)
                .isPresent().get()
                .extracting(
                        Channel::getIdentifier,
                        Channel::getName,
                        c -> toText(c.getConfig().format().applyTo(Message.message(ChatSource.player(server.addPlayer()), "test").to(c).build()))
                ).contains(
                        "global",
                        "Global",
                        "&6[&aGlobal&6]&ePlayer0&7: &atest"
                );
    }
}