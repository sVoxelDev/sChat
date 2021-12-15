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

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MessageTargetTests<T extends MessageTarget> extends TestBase {

    protected T target;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        target = (T) new DummyTarget();
    }

    @NotNull
    private Message sendMessage(Message message) {
        target.sendMessage(message);
        return message;
    }

    @Test
    void getMessages_isEmpty() {
        assertThat(target.getMessages()).isEmpty();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getMessages_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> target.getMessages().add(randomMessage()));
    }

    @Test
    void sendMessage_storesMessage() {
        final Message message = sendMessage(randomMessage());
        assertThat(target.getMessages()).contains(message);
    }

    @Test
    void sendMessage_withSource() {
        final Message message = sendMessage(Message.message("Bob", randomText()));
        assertThat(target.getMessages()).contains(message);
    }

}
