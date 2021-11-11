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
import net.silthus.chat.scopes.GlobalScope;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ScopesTest {

    @Test
    @SuppressWarnings("ConstantConditions")
    void scopes_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> Scopes.scopes().add(new GlobalScope()));
    }

    @Test
    void register_usesTaggedName() {
        TestScope scope = new TestScope();
        Scopes.register(scope);

        assertThat(Scopes.scope("foo")).isEqualTo(scope);
    }

    @Test
    void register_noAnnotation_usesClassName() {
        NotAnnotatedScope scope = new NotAnnotatedScope();
        Scopes.register(scope);

        assertThat(Scopes.scope("not-annotated")).isEqualTo(scope);
    }

    @Scope.Name("foo")
    static class TestScope implements Scope {

        @Override
        public Collection<ChatTarget> apply(Channel channel) {
            return new ArrayList<>();
        }
    }

    static class NotAnnotatedScope implements Scope {

        @Override
        public Collection<ChatTarget> apply(Channel channel) {
            return new ArrayList<>();
        }
    }
}