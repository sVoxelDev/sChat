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

package net.silthus.schat.identity;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.pointer.Pointers;

/**
 * Holds the id, name and display name of an identified entity.
 *
 * <p>Identities could be players, channels, the console and so on.
 * If the identity is a player the id of the player in the platform matches the identity's id.</p>
 *
 * @since next
 */
@Getter
@EqualsAndHashCode(of = {"uniqueId"})
final class IdentityImpl implements Identity {

    static final Identity NIL = Identity.identity(NIL_IDENTITY_ID, "", Component.empty());

    private final @NonNull UUID uniqueId;
    private final Pointers pointers;

    IdentityImpl(
        @NonNull UUID uniqueId,
        @NonNull Pointers pointers) {
        this.uniqueId = uniqueId;
        this.pointers = pointers;
    }
}
