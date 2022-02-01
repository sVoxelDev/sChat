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

package net.silthus.schat.channel;

import java.util.function.Function;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.ApiStatus;

public interface Feature {
    static <F extends Feature> Feature.Type<F> feature(Class<F> featureClass, Function<Channel, F> supplier) {
        return new Type<>(featureClass, supplier);
    }

    /**
     * The feature is initialized after all features of the channel are created.
     */
    @ApiStatus.OverrideOnly
    default void initialize() {
    }

    /**
     * The {@code onMessage} event is called everytime the channel receives a message.
     *
     * @param message the message the channel received
     */
    @ApiStatus.OverrideOnly
    default void onMessage(Message message) {
    }

    record Type<F extends Feature>(Class<F> type, Function<Channel, F> feature) {
        F createInstance(Channel channel) {
            return feature().apply(channel);
        }
    }
}
