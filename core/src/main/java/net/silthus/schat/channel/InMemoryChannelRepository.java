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

package net.silthus.schat.channel;

import lombok.extern.java.Log;
import net.silthus.schat.repository.InMemoryRepository;
import org.jetbrains.annotations.NotNull;

class InMemoryChannelRepository extends InMemoryRepository<String, Channel> implements ChannelRepository {

    @Override
    public void add(@NotNull Channel channel) {
        if (contains(channel.key()))
            throw new DuplicateChannel(channel);
        super.add(channel);
    }

    @Log(topic = "sChat:ChannelRepository")
    static final class Logging extends InMemoryChannelRepository {

        @Override
        public void add(@NotNull Channel channel) {
            super.add(channel);
            log.info("Added Channel: " + channel);
        }

        @Override
        public Channel remove(@NotNull String key) {
            final Channel channel = super.remove(key);
            if (channel != null)
                log.info("Removed Channel: " + key);
            return channel;
        }
    }
}
