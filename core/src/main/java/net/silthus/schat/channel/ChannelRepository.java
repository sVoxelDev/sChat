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

import java.io.Serial;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.schat.repository.Repository;
import org.jetbrains.annotations.NotNull;

/**
 * Holds all channels registered on the server.
 *
 * @since next
 */
public interface ChannelRepository extends Repository<String, Channel> {

    /**
     * Creates a new in memory channel repository.
     *
     * @return the new in memory repository
     * @since next
     */
    static ChannelRepository createInMemoryChannelRepository() {
        return createInMemoryChannelRepository(false);
    }

    /**
     * Creates a new in memory channel repository.
     *
     * @param debug true to print all operations into the log
     * @return the new in memory repository
     * @since next
     */
    static ChannelRepository createInMemoryChannelRepository(boolean debug) {
        if (debug)
            return new InMemoryChannelRepository.Logging();
        else
            return new InMemoryChannelRepository();
    }

    /**
     * Adds the given channel to the repository if it does not exist.
     *
     * @param channel the channel to add
     * @throws DuplicateChannel if a channel with the same key exists
     */
    @Override
    void add(@NotNull Channel channel);

    /**
     * The exception is thrown if someone tries to add a channel with the same key to the repository.
     *
     * @since next
     */
    @Getter
    @Accessors(fluent = true)
    final class DuplicateChannel extends RuntimeException {
        @Serial private static final long serialVersionUID = 365583451304114504L;

        private final Channel channel;

        DuplicateChannel(Channel channel) {
            super("A channel with the key '" + channel.key() + "' already exists.");
            this.channel = channel;
        }
    }
}
