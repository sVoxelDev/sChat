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

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import net.kyori.adventure.text.Component;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageTargetSpy implements MessageTarget {

    public static MessageTargetSpy targetSpy() {
        return new MessageTargetSpy();
    }

    private final List<Message> receivedMessages = new ArrayList<>();

    @Override
    public void sendMessage(@NonNull Message message) {
        receivedMessages.add(message);
    }

    public void assertReceivedMessage(Message message) {
        assertThat(receivedMessages).contains(message);
    }

    public void assertReceivedMessageWithText(Component text) {
        assertThat(receivedMessages).anyMatch(message -> message.text().equals(text));
    }
}
