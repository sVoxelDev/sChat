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

import net.silthus.chat.conversations.Channel;
import net.silthus.configmapper.ConfigOption;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScopesTest {

    @Test
    void register_usesTaggedName() {
        Scopes.register(TestScope.class);

        assertThat(Scopes.scope("foo"))
                .isNotNull()
                .isInstanceOf(TestScope.class);
    }

    @Test
    void register_noAnnotation_usesClassName() {
        Scopes.register(NotAnnotatedScope.class);

        assertThat(Scopes.scope("not-annotated"))
                .isNotNull()
                .isInstanceOf(NotAnnotatedScope.class);
    }

    @Test
    void get_nonExisting_returnsNull() {
        assertThat(Scopes.scope("foobar"))
                .isNull();
    }

    @Test
    void create_withConfig_isAppliedToScope() {
        Scopes.register(TestScope.class);
        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("my_config", "test");
        List<String> worlds = List.of("test", "world");
        cfg.set("worlds", worlds);

        Scope scope = Scopes.scope("foo", cfg);
        assertThat(scope)
                .extracting("myConfig", "worlds")
                .contains("test", worlds);
    }

    @Scope.Name("foo")
    static class TestScope implements Scope {

        @ConfigOption
        private String myConfig;
        @ConfigOption
        private List<String> worlds = new ArrayList<>();

        @Override
        public Collection<ChatTarget> apply(Channel channel, Message message) {
            return new ArrayList<>();
        }
    }

    static class NotAnnotatedScope implements Scope {

        @Override
        public Collection<ChatTarget> apply(Channel channel, Message message) {
            return new ArrayList<>();
        }
    }
}