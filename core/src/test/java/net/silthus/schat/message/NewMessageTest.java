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

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.message.NewMessage.emptyMessage;
import static net.silthus.schat.message.NewMessage.message;
import static org.assertj.core.api.Assertions.assertThat;

class NewMessageTest {

    @Test
    void has_unique_id() {
        final NewMessage message = emptyMessage();
        assertThat(message.getId()).isNotNull();
        assertThat(message.getId()).isNotEqualTo(emptyMessage().getId());
    }

    @Test
    void given_same_id_are_equal() {
        final UUID id = UUID.randomUUID();
        final NewMessage message = message().id(id).create();
        final NewMessage message2 = message().id(id).create();
        assertThat(message).isEqualTo(message2);
    }

    @Test
    void given_no_target_uses_nil_identity() {
        assertThat(emptyMessage().getTarget()).isEqualTo(Identity.nil());
    }

    @Test
    void given_no_text_uses_empty_component() {
        assertThat(emptyMessage().getText()).isEqualTo(Component.empty());
    }
}
