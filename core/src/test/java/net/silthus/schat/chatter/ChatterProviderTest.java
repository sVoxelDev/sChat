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

package net.silthus.schat.chatter;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static net.silthus.schat.AssertionHelper.assertNPE;
import static org.assertj.core.api.Assertions.assertThat;

class ChatterProviderTest {
    private ChatterProviderImpl provider;

    @BeforeEach
    void setUp() {
        provider = new ChatterProviderImpl(new ChatterFactoryStub());
    }

    @Nested class get {
        @SuppressWarnings("ConstantConditions")
        @Test void given_null_throws_npe() {
            assertNPE(() -> provider.get(null));
        }

        @Nested class given_valid_identity {
            @Test void then_chatter_is_cached() {
                final UUID id = randomUUID();
                final Chatter chatter = provider.get(id);
                assertThat(chatter).isSameAs(provider.get(id));
            }
        }
    }
}
