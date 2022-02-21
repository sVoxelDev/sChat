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
package net.silthus.schat.command;

import java.io.Serial;
import java.util.function.Function;

/**
 * Represents an operation that can be executed independently and yields a {@link Result}.
 *
 * <p>A list of common commands can be found inside the {@link net.silthus.schat.commands} package.</p>
 *
 * @since next
 */
public interface Command {

    /**
     * Executes this command and returns the result.
     *
     * @return the result of the command
     * @since next
     */
    Result execute();

    /**
     * A generic builder used to construct commands.
     *
     * @param <B> the type of the command builder
     * @param <C> the type of the command
     * @since next
     */
    interface Builder<B extends Builder<B, C>, C extends Command> {

        /**
         * Directly creates and then executes the command created from this builder.
         *
         * @return the result of the command execution
         * @since next
         */
        default Result execute() {
            return create().execute();
        }

        /**
         * Gets the factory that creates the command from this builder.
         *
         * @return the command factory
         * @since next
         */
        Function<B, ? extends C> command();

        /**
         * Instructs this builder to use the given command factory.
         *
         * @param command the type of the command to use
         * @return this builder
         * @since next
         */
        B use(Function<B, ? extends C> command);

        /**
         * Creates a new command instance from this builder.
         *
         * @return the created command
         * @since next
         */
        @SuppressWarnings("unchecked")
        default C create() {
            return command().apply((B) this);
        }
    }

    /**
     * A generic error that can be thrown by commands.
     *
     * @since next
     */
    class Error extends RuntimeException {
        @Serial private static final long serialVersionUID = 7958152949989995416L;
    }
}
