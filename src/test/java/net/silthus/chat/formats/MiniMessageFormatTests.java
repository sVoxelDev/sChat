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

package net.silthus.chat.formats;

import net.silthus.chat.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MiniMessageFormatTests extends TestBase {

    @Test
    void create() {
        assertThat(toText("<message>", Message.message("test").build()))
                .isEqualTo("test");
    }

    @Test
    void withColor() {
        assertThat(toText("<green><message>", Message.message("test").build()))
                .isEqualTo("&atest");
    }

    @Test
    void withSource() {
        assertThat(toText("<sender_name>: <message>", Message.message(ChatSource.player(server.addPlayer()), "test").build()))
                .isEqualTo("Player0: test");
    }

    @Test
    void withNullSource() {
        assertThat(toText("<sender_name>: <message>", Message.message("test").build()))
                .isEqualTo(": test");
    }

    @Test
    void withChannelName() {
        Message message = Message.message(ChatSource.player(server.addPlayer()), "test")
                .to(ChatTarget.channel("test channel")).build();

        assertThat(toText("[<channel_name>]<sender_name>: <message>", message))
                .isEqualTo("[test channel]Player0: test");
    }

    @Test
    void withVaultPrefix() {
        
    }

    @Test
    void withoutMessageTag_appendsMessageTag() {
        MiniMessageFormat format = new MiniMessageFormat("source: ");
        String text = toText(format.applyTo(Message.message("test").build()));
        assertThat(text).isEqualTo("source: test");
    }

    private String toText(String format, Message message) {
        return toText(Format.miniMessage(format).applyTo(message));
    }
}
