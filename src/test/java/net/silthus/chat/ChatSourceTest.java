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

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatSourceTest extends TestBase {

    @Test
    void of_identifier() {
        ChatSource source = ChatSource.named("test");
        assertIdAndName(source, "test", "test");
    }

    @Test
    void of_identifier_withDisplayName() {
        ChatSource source = ChatSource.named("test", Component.text("Test Source"));
        assertIdAndName(source, "test", "Test Source");
    }

    @Test
    void isPlayer_returnsFalse() {
        assertThat(ChatSource.named("test").isPlayer()).isFalse();
    }

    @Test
    void isPlayer_ofPlayer_returnsTrue() {
        assertThat(ChatSource.player(server.addPlayer()).isPlayer()).isTrue();
    }

    private void assertIdAndName(ChatSource source, String id, String name) {
        assertThat(source)
                .isNotNull()
                .extracting(
                        ChatSource::getIdentifier,
                        c -> toText(c.getName())
                ).contains(
                        id,
                        name
                );
    }

    @Test
    void message_createsMessageWithSource() {

        ChatSource source = ChatSource.named("test");
        Message message = source.message("Hi there!").send();

        assertThat(message.getSource()).isEqualTo(source);
    }
}