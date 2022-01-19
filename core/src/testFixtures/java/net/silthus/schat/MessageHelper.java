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

package net.silthus.schat;

import net.kyori.adventure.text.Component;
import net.silthus.schat.message.Message;
import org.apache.commons.lang3.RandomStringUtils;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.message.Message.message;

public final class MessageHelper {

    private MessageHelper() {
    }

    public static Component randomText() {
        return text(RandomStringUtils.random(30));
    }

    public static Message randomMessage() {
        return message(randomText());
    }
}
