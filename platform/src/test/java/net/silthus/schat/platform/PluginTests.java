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

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.platform.AudienceProvider;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.user.PermissionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.IdentityHelper.randomIdentity;
import static net.silthus.schat.TestHelper.assertNPE;
import static net.silthus.schat.UserHelper.mockAudienceProvider;
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

            @Nested class then_getUserFactory {

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
                    assertThatExceptionOfType(UserFactory.InvalidPlayerType.class).isThrownBy(() -> plugin.getUserFactory(String.class));
                }

                @Test
                void given_super_type_does_not_throw() {
                    assertThat(plugin.getUserFactory(TestCommandSender.class)).isNotNull();
                }
            }
        }

    }

    private static class TestPlugin extends SChatPlugin {

        private final FakeUserFactory userFactory;

        TestPlugin() {
            userFactory = new FakeUserFactory();
        }

        @Override
        protected UserFactory<?> provideUserFactory() {
            return userFactory;
        }
    }

    private static class FakeUserFactory extends UserFactory<TestCommandSender> {
        @Override
        protected Class<TestCommandSender> getType() {
            return TestCommandSender.class;
        }

        @Override
        protected Identity getIdentity(TestCommandSender player) {
            return player.getIdentity();
        }

        @Override
        protected PermissionHandler getPermissionHandler(TestCommandSender player) {
            return player.getPermissionHandler();
        }

        @Override
        protected AudienceProvider getAudienceProvider(TestCommandSender player) {
            return mockAudienceProvider();
        }
    }

    @Getter
    @Setter
    private static class TestCommandSender {
        private final Identity identity = randomIdentity();
        private PermissionHandler permissionHandler = permission -> false;
    }

    private static class TestPlayer extends TestCommandSender {
    }
}
