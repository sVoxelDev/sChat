/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.message;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.commands.SendMessageResult;

import static net.silthus.schat.commands.SendMessageResult.success;
import static org.assertj.core.api.Assertions.assertThat;

public class MessageTargetSpy implements MessageTarget {

    public static MessageTargetSpy targetSpy() {
        return new MessageTargetSpy();
    }

    private final List<Message> receivedMessages = new ArrayList<>();

    @Override
    public SendMessageResult sendMessage(@NonNull Message message) {
        receivedMessages.add(message);
        return success(message);
    }

    public void assertReceivedMessage(Message message) {
        assertThat(receivedMessages).contains(message);
    }

    public void assertReceivedMessageWithText(Component text) {
        assertThat(receivedMessages).anyMatch(message -> message.text().equals(text));
    }
}
