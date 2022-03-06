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
import java.util.Optional;

/**
 * Represents a generic result, which can either be successful or fail.
 *
 * @since 1.0.0
 */
@FunctionalInterface
public interface Result {

    /**
     * Instance of {@link Result} which always reports success.
     *
     * @since 1.0.0
     */
    Result GENERIC_SUCCESS = () -> true;

    /**
     * Instance of {@link Result} which always reports failure.
     *
     * @since 1.0.0
     */
    Result GENERIC_FAILURE = () -> false;

    /**
     * Creates a new generic success result.
     *
     * @return a success
     * @since 1.0.0
     */
    static Result success() {
        return GENERIC_SUCCESS;
    }

    /**
     * Creates a new generic failure result.
     *
     * @return a failure
     * @since 1.0.0
     */
    static Result failure() {
        return GENERIC_FAILURE;
    }

    /**
     * Creates a new generic error result containing the given exception.
     *
     * @return a failure with an exception
     * @since 1.0.0
     */
    static Result error(Throwable exception) {
        return new ResultImpl(false, exception);
    }

    /**
     * Creates a success or failure based on the given boolean.
     *
     * @param result the result
     * @return the result based on the input
     * @since 1.0.0
     */
    static Result of(boolean result) {
        return new ResultImpl(result, null);
    }

    /**
     * Gets if the operation which produced this result completed successfully.
     *
     * @return if the result indicates a success
     * @since 1.0.0
     */
    boolean wasSuccessful();

    /**
     * Gets if the operation which produced this result failed.
     *
     * <p>You can get the {@link #failureReason()} or directly {@link #raiseError()}
     * to throw the reason of failure if one is present.</p>
     *
     * @return if the result indicates a failure
     * @since 1.0.0
     */
    default boolean wasFailure() {
        return !wasSuccessful();
    }

    /**
     * The exception that lead to the failed result.
     *
     * @return the exception responsible for the result failure
     * @since 1.0.0
     */
    default Optional<Throwable> failureReason() {
        return Optional.empty();
    }

    /**
     * Raises an {@link Error} if the result {@link #wasFailure()} and contains a {@link #failureReason()}.
     *
     * @return the result if no error occurred
     * @throws Error the error encapsulating the underlying {@link #failureReason()}
     * @since 1.0.0
     */
    default Result raiseError() throws Error {
        final Throwable throwable = failureReason().orElse(null);
        if (throwable != null)
            throw new Error(throwable);
        return this;
    }

    /**
     * Encapsulates an exception thrown during the execution of a {@link Command}.
     *
     * @since 1.0.0
     */
    final class Error extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 7484250245467394159L;

        Error(Throwable cause) {
            super(cause);
        }
    }
}
