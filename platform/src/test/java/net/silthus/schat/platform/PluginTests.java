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

package net.silthus.schat.platform;

import net.silthus.schat.platform.sender.ChatterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.TestHelper.assertNPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PluginTests {

    @Nested class given_new_plugin {
        private TestPlugin plugin;

        @BeforeEach
        void setUp() {
            plugin = new TestPlugin();
        }

        @Nested class when_enable_is_called {
            @BeforeEach
            void setUp() {
                plugin.enable();
            }

            @Nested class then_getChatterFactory {

                @Test
                void is_not_null() {
                    assertThat(plugin.getUserFactory(TestPlayer.class)).isNotNull();
                }

                @Test
                @SuppressWarnings({"ConstantConditions"})
                void given_null_player_class_throws() {
                    assertNPE(() -> plugin.getUserFactory(null));
                }

                @Test
                void given_invalid_player_class_throws() {
                    assertThatExceptionOfType(ChatterFactory.InvalidPlayerType.class).isThrownBy(() -> plugin.getUserFactory(String.class));
                }

                @Test
                void given_super_type_does_not_throw() {
                    assertThat(plugin.getUserFactory(TestCommandSender.class)).isNotNull();
                }
            }
        }

    }

    private static class TestPlugin extends AbstractSChatPlugin {

        private final FakeChatterFactory userFactory;

        TestPlugin() {
            userFactory = new FakeChatterFactory();
        }

        @Override
        protected ChatterFactory<?> provideUserFactory() {
            return userFactory;
        }
    }

}
