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

package net.silthus.schat.core;

import net.silthus.schat.Chatter;
import net.silthus.schat.Message;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChatterTests extends TestBase {

    private Chatter chatter;

    @BeforeEach
    void setUp() {
        chatter = new ChatterImpl();
    }

    @Test
    void getActiveChannel_isEmpty() {
        assertThat(chatter.getActiveChannel()).isEmpty();
    }

    @NotNull
    private Message sendMessage(Message message) {
        chatter.sendMessage(message);
        return message;
    }

    @Test
    void getMessages_isEmpty() {
        assertThat(chatter.getMessages()).isEmpty();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getMessages_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> chatter.getMessages().add(randomMessage()));
    }

    @Test
    void sendMessage_storesMessage() {
        final Message message = sendMessage(randomMessage());
        assertThat(chatter.getMessages()).contains(message);
    }

    @Test
    void sendMessage_withSource() {
        final Message message = sendMessage(Message.message("Bob", randomText()));
        assertThat(chatter.getMessages()).contains(message);
    }
}
