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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.pointer.Pointers;

import static net.silthus.schat.pointer.Pointers.pointersBuilder;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"identity"})
final class IdentityMessageSource implements MessageSource {

    static final IdentityMessageSource NIL = new IdentityMessageSource(Identity.nil());

    private final Identity identity;
    private final transient Pointers pointers;

    IdentityMessageSource(Identity identity) {
        this.identity = identity;
        pointers = pointersBuilder()
            .withForward(Identity.ID, identity(), Identity.ID)
            .withForward(Identity.NAME, identity(), Identity.NAME)
            .withForward(Identity.DISPLAY_NAME, identity(), Identity.DISPLAY_NAME)
            .create();
    }
}
