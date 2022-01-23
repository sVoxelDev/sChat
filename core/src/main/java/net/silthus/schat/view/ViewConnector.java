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

package net.silthus.schat.view;

import java.util.Optional;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.Nullable;

public interface ViewConnector {

    static SimpleViewConnector createSimpleViewConnector(ViewProvider viewProvider) {
        return new SimpleViewConnector(viewProvider);
    }

    void update(@NonNull Context context);

    @Value
    @Accessors(fluent = true)
    class Context {
        public static Context of(@NonNull Chatter chatter, @NonNull Display display) {
            return new Context(chatter, display, null);
        }

        public static Context of(Chatter chatter, @NonNull Display display, Message lastMessage) {
            return new Context(chatter, display, lastMessage);
        }

        @NonNull Chatter chatter;
        @NonNull Display display;
        @Nullable Message lastMessage;

        private Context(@NonNull Chatter chatter, @NonNull Display display, @Nullable Message lastMessage) {
            this.chatter = chatter;
            this.display = display;
            this.lastMessage = lastMessage;
        }

        public Optional<Message> lastMessage() {
            return Optional.ofNullable(lastMessage);
        }
    }

}
