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

package net.silthus.chat;

import co.aikar.commands.BukkitCommandManager;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.config.PluginConfig;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.identities.Console;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.chat.Constants.Formatting.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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
                .filter(c -> c.getName().equals("global"))
                .findFirst();
        assertThat(channel)
                .isPresent().get()
                .extracting(
                        Channel::getName,
                        Channel::getDisplayName,
                        c -> toText(c.getConfig().format().applyTo(Message.message(Chatter.player(server.addPlayer()), "test").to(c).build()))
                ).contains(
                        "global",
                        text("Global"),
                        "&6[&aGlobal&6]&7[ADMIN]&aPlayer0[!]&7: test"
                );
    }

    @Test
    void writes_defaultConfig() {
        File config = new File(plugin.getDataFolder(), "config.yml");
        File defaultConfig = new File(plugin.getDataFolder(), "config.default.yml");

        assertThat(config).exists();
        assertThat(defaultConfig).exists();
    }

    @Test
    void loads_defaultFormats() {
        assertThat(Formats.formatFromTemplate(DEFAULT)).isPresent();
        assertThat(Formats.formatFromTemplate(CHANNEL)).isPresent();
        assertThat(Formats.formatFromTemplate(NO_FORMAT)).isPresent();
    }

    @Nested
    class Reload {

        @BeforeEach
        void setUp() {
            plugin.setChannelRegistry(spy(plugin.getChannelRegistry()));
        }

        @Test
        void reload_loadsNewConfigYAML() {
            loadTestConfig("reload-test.yml");
            final PluginConfig oldConfig = plugin.getPluginConfig();
            plugin.reload();
            final PluginConfig newConfig = plugin.getPluginConfig();

            assertThat(oldConfig).isNotEqualTo(newConfig);
            assertThat(newConfig)
                    .extracting(PluginConfig::defaults)
                    .extracting(PluginConfig.Defaults::channel)
                    .extracting(
                            ChannelConfig::autoJoin,
                            ChannelConfig::protect,
                            ChannelConfig::canLeave
                    ).contains(
                            true,
                            true,
                            false
                    );
        }

        @Test
        void reloadsChannels_ifConfigChanged() {
            loadTestConfig("reload-test.yml");
            plugin.reload();
            verify(plugin.getChannelRegistry()).load(any());
        }

        @Test
        void reloadsConsoleConfig() {
            loadTestConfig("reload-test.yml");
            plugin.reload();

            final Console console = Console.console();
            assertThat(console.getDisplayName()).isEqualTo(text("MyConsole"));
            assertThat(console.getConfig().defaultChannel()).isEqualTo("none");
        }
    }

}