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

package net.silthus.schat.message;

import org.junit.jupiter.api.Test;

import static net.silthus.schat.IdentityHelper.randomIdentity;
import static org.assertj.core.api.Assertions.assertThat;

class MessageTest {

    @Test
    void message_without_source_is_type_system() {
        final Message message = Message.message("Hi");
        assertThat(message.getType()).isEqualTo(Message.Type.SYSTEM);
    }

    @Test
    void message_with_source_is_type_chat() {
        final Message message = Message.message(randomIdentity(), "Hey");
        assertThat(message.getType()).isEqualTo(Message.Type.CHAT);
    }
}
