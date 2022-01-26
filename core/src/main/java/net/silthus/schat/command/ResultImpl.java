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

final class ResultImpl implements Result {

    private final boolean success;
    private final Throwable failureReason;

    ResultImpl(boolean success, Throwable failureReason) {
        this.success = success;
        this.failureReason = failureReason;
    }

    @Override
    public boolean wasSuccessful() {
        return success;
    }

    @Override
    public Optional<Throwable> getFailureReason() {
        return Optional.ofNullable(failureReason);
    }
}
