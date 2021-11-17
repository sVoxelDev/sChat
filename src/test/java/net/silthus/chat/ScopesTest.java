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

import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.conversations.Channel;
import net.silthus.configmapper.ConfigOption;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ScopesTest extends TestBase {

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

    @Test
    void scope_withClassName_castsToClass() {
        Scopes.register(TestScope.class);
        final TestScope scope = Scopes.scope(TestScope.class);

        assertThat(scope).isNotNull();
    }

    @Test
    void onApply_calledWhenScopeIsSetToChannel() {
        final TestScope scope = setupTestScope();
        final Channel channel = channelWithScope(scope);

        verify(scope).onApply(channel);
    }

    @Test
    void onRemove_calledWhenScopeIsReplaced() {
        final TestScope scope = setupTestScope();
        final Channel channel = channelWithScope(scope);

        channel.setConfig(ChannelConfig.builder().scope(Scopes.global()).build());
        verify(scope).onRemove(channel);
        assertThat(channel.getConfig().scope()).isNotEqualTo(scope);
    }

    @Test
    void close_callsOnRemove() {
        final TestScope scope = setupTestScope();
        final Channel channel = channelWithScope(scope);

        channel.close();
        verify(scope).onRemove(channel);
    }

    private Channel channelWithScope(TestScope scope) {
        return createChannel(config -> config.scope(scope));
    }

    private TestScope setupTestScope() {
        Scopes.register(TestScope.class);
        return spy(Scopes.scope(TestScope.class));
    }

    @Scope.Name("foo")
    static class TestScope implements Scope {

        @ConfigOption
        private String myConfig;
        @ConfigOption
        private List<String> worlds = new ArrayList<>();

        @Override
        public void onApply(Channel channel) {

        }

        @Override
        public Collection<ChatTarget> filterTargets(Channel channel, Message message) {
            return new ArrayList<>();
        }

        @Override
        public void onRemove(Channel channel) {

        }
    }

    static class NotAnnotatedScope implements Scope {

        @Override
        public Collection<ChatTarget> filterTargets(Channel channel, Message message) {
            return new ArrayList<>();
        }
    }
}