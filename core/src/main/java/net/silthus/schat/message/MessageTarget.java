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

import java.util.function.Predicate;
import lombok.NonNull;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.commands.SendMessageResult;

import static net.silthus.schat.channel.ChannelSettings.PRIVATE;

/**
 * Represents an object that can receive messages.
 *
 * <p>The primary example for this would be the {@link Chatter}.</p>
 *
 * @since next
 */
public interface MessageTarget {

    Predicate<MessageTarget> IS_CHATTER = target -> target instanceof Chatter;
    Predicate<MessageTarget> IS_PRIVATE_CHANNEL = target -> (target instanceof Channel channel) && channel.is(PRIVATE);

    /**
     * Sends a message to the target.
     *
     * @param message the message to sent
     * @return the result if the message was processed
     * @since next
     */
    SendMessageResult sendMessage(@NonNull Message message);
}
