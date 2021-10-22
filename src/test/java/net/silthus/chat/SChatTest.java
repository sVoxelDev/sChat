package net.silthus.chat;

import co.aikar.commands.BukkitCommandManager;
import org.junit.jupiter.api.Test;

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
}