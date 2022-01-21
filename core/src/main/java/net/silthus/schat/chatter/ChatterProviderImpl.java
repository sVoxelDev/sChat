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

package net.silthus.schat.chatter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;

final class ChatterProviderImpl implements ChatterProvider {

    static final ChatterProvider NIL = new NilChatterProvider();

    private final ChatterFactory factory;
    private final Map<UUID, Chatter> chatters = new HashMap<>();

    ChatterProviderImpl(ChatterFactory factory) {
        this.factory = factory;
    }

    @Override
    public Chatter get(@NonNull UUID id) {
        return chatters.computeIfAbsent(id, uuid -> factory.createChatter(id));
    }

    static final class NilChatterProvider implements ChatterProvider {

        @Override
        public Chatter get(@NonNull UUID id) {
            return Chatter.empty();
        }
    }
}
