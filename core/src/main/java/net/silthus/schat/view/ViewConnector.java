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

import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;

public interface ViewConnector {

    static ViewConnector empty() {
        return () -> {};
    }

    static SimpleViewConnector createSimpleViewConnector(Chatter chatter, ViewProvider viewProvider, Out out) {
        return new SimpleViewConnector(chatter, viewProvider, out);
    }

    void update();

    interface Factory {
        static Factory empty() {
            return chatter -> ViewConnector.empty();
        }

        ViewConnector create(Chatter chatter);
    }

    interface Out {
        static Out empty() {
            return renderedView -> {};
        }

        void send(Component renderedView);
    }
}
