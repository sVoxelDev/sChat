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

package net.silthus.schat.view;

import net.silthus.schat.chatter.ChatterMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.view.ViewProvider.simpleViewProvider;
import static org.assertj.core.api.Assertions.assertThat;

class SimpleViewProviderTests {
    private ViewProvider viewProvider;

    @BeforeEach
    void setUp() {
        viewProvider = simpleViewProvider(chatter -> View.empty());
    }

    @Nested class getView {
        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_throws_npe() {
            assertNPE(() -> viewProvider.getView(null));
        }

        @Nested class given_valid_chatter {
            private ChatterMock chatter;

            @BeforeEach
            void setUp() {
                chatter = randomChatter();
            }

            @Test
            void then_returns_chatter_view() {
                final View view = viewProvider.getView(chatter);
                assertThat(view).isNotNull();
            }

            @Test
            void then_view_is_cached() {
                final View view = viewProvider.getView(chatter);
                assertThat(view).isSameAs(viewProvider.getView(chatter));
            }
        }
    }
}
