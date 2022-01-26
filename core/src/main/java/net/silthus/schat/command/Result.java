/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.schat.command;

import java.util.Optional;

/**
 * Represents a generic result, which can either be successful or fail.
 *
 * @since next
 */
@FunctionalInterface
public interface Result {

    /**
     * Instance of {@link Result} which always reports success.
     *
     * @since next
     */
    Result GENERIC_SUCCESS = () -> true;

    /**
     * Instance of {@link Result} which always reports failure.
     *
     * @since next
     */
    Result GENERIC_FAILURE = () -> false;

    static Result success() {
        return GENERIC_SUCCESS;
    }

    static Result failure() {
        return GENERIC_FAILURE;
    }

    static Result error(Throwable exception) {
        return new ResultImpl(false, exception);
    }

    static Result of(boolean result) {
        return new ResultImpl(result, null);
    }

    /**
     * Gets if the operation which produced this result completed successfully.
     *
     * @return if the result indicates a success
     * @since next
     */
    boolean wasSuccessful();

    /**
     * The exception that lead to the failed result.
     *
     * @return the exception responsible for the result failure
     * @since next
     */
    default Optional<Throwable> getFailureReason() {
        return Optional.empty();
    }
}
