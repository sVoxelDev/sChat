/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.platform.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

class PluginTests {

    @Nested class given_new_plugin {
        private TestServer plugin;

        @BeforeEach
        void setUp() {
            plugin = new TestServer();
        }

        @Nested class when_enable_is_called {
            @BeforeEach
            void setUp() {
                plugin.load();
                plugin.enable();
            }

            @Test
            void then_ChannelRepository_is_not_null() {
                assertThat(plugin.getChannelRepository()).isNotNull();
            }

            @Test
            void then_commands_are_registered() {
                verify(TestServer.dummyCommand, atLeastOnce()).register(any(), any());
            }

            @Test
            void then_config_is_loaded() {
                assertThat(plugin.getConfig()).isNotNull();
                assertThat(plugin.getConfig().get(CHANNELS)).isNotNull();
            }

            @Test
            void then_channels_are_loaded() {
                assertThat(plugin.getChannelRepository().all()).isNotEmpty();
            }
        }
    }
}
