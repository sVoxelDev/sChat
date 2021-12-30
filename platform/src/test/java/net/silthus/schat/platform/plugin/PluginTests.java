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

package net.silthus.schat.platform.plugin;

import cloud.commandframework.CommandManager;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.channel.Channels;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.plugin.bootstrap.Bootstrap;
import net.silthus.schat.platform.sender.Sender;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PluginTests {

    private PluginMock plugin;

    @BeforeEach
    void setUp() {
        plugin = new PluginMock();
    }

    @Test
    void channels_are_loaded() {
        plugin.enable();
        verify(plugin.getChannels()).load();
    }

    private static class PluginMock extends AbstractPlugin {
        @Override
        protected void setupSenderFactory() {
        }

        @Override
        protected @NotNull Channels provideChannelManager(ChannelRepository repository) {
            return mock(Channels.class);
        }

        @Override
        protected @NotNull ConfigurationAdapter provideConfigurationAdapter() {
            return mock(ConfigurationAdapter.class);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected CommandManager<Sender> provideCommandManager() {
            return mock(CommandManager.class);
        }

        @Override
        protected void registerListeners() {

        }

        @Override
        public Bootstrap getBootstrap() {
            return mock(Bootstrap.class);
        }
    }
}
