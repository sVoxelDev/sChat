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

package net.silthus.schat.channel.usecases;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import org.jetbrains.annotations.Nullable;

public interface JoinChannel {

    default void joinChannel(Args args) throws Error {
        performChecks(args);
        args.chatter().addChannel(args.channel());
        args.channel().addTarget(args.chatter());
    }

    private void performChecks(Args args) {
        for (final Check check : args.channel().getChecks(Check.class)) {
            check.testAndThrow(args);
        }
    }

    interface Check extends net.silthus.schat.Check {

        static Result success() {
            return new Result(true, null);
        }

        static Result failure(Throwable error) {
            return new Result(false, error);
        }

        Result test(Args args);

        default void testAndThrow(Args args) throws Error {
            final Result result = test(args);
            if (result.failure())
                result.raiseError();
        }
    }

    record Args(Chatter chatter, Channel channel) {

        public static Args of(Chatter chatter, Channel channel) {
            return new Args(chatter, channel);
        }
    }

    record Result(boolean success, @Nullable Throwable error) {

        public boolean failure() {
            return !success();
        }

        public void raiseError() throws Error {
            if (error() instanceof Error)
                throw (Error) error();
            throw new Error(error());
        }
    }

    class Error extends RuntimeException {
        public Error() {
            super();
        }

        public Error(String message) {
            super(message);
        }

        public Error(String message, Throwable cause) {
            super(message, cause);
        }

        public Error(Throwable cause) {
            super(cause);
        }

        protected Error(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
