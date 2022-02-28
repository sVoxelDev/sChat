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
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identified;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.pointer.Pointered;

/**
 * A message source can be anything that is {@link Identified}
 * and used to send messages.
 *
 * <p>The primary source of messages is the {@link Chatter},
 * but any {@link Identity} can be wrapped as a source using the {@link #of(Identity)} method.</p>
 *
 * @since next
 */
public interface MessageSource extends Identified, Pointered {

    Predicate<MessageSource> IS_NIL = messageSource -> messageSource.identity().equals(Identity.nil());
    Predicate<MessageSource> IS_NOT_NIL = IS_NIL.negate();

    /**
     * Gets an empty message source represented by the {@link Identity#nil()}.
     *
     * @return the empty message source
     * @since next
     */
    static MessageSource nil() {
        return IdentityMessageSource.NIL;
    }

    /**
     * Wraps the given identity into a message source.
     *
     * @param identity the identity to wrap as a source
     * @return the wrapped message source
     * @since next
     */
    static MessageSource of(Identity identity) {
        return new IdentityMessageSource(identity);
    }

    /**
     * Helper method to send a message using this chatter as the source.
     *
     * @param text the text of the message
     * @return the message draft
     * @since next
     */
    default Message.Draft message(String text) {
        return Message.message(text).source(this);
    }

    /**
     * Helper method to send a message using this chatter as the source.
     *
     * @param text the text of the message
     * @return the message draft
     * @since next
     */
    default Message.Draft message(Component text) {
        return Message.message(text).source(this);
    }
}
