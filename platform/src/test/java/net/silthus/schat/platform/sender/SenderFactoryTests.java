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

package net.silthus.schat.platform.sender;

import net.silthus.schat.platform.FakeSenderFactory;
import net.silthus.schat.platform.TestPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SenderFactoryTests {

    private FakeSenderFactory factory;

    @BeforeEach
    void setUp() {
        factory = new FakeSenderFactory();
    }

    @Nested class given_player {

        private TestPlayer player;

        @BeforeEach
        void setUp() {
            player = new TestPlayer();
        }

        @Test
        void when_getSender_is_called_then_factory_uses_player_identity() {
            assertThat(factory.getSender(player).getIdentity()).isEqualTo(player.getIdentity());
        }
    }
}
