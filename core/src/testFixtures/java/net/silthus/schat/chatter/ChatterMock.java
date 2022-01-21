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

package net.silthus.schat.chatter;

import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.IdentityHelper.randomIdentity;
import static org.assertj.core.api.Assertions.assertThat;

public final class ChatterMock extends ChatterImpl {

    private ChatterMock(Builder builder) {
        super(builder);
    }

    public static @NotNull ChatterMock randomChatter() {
        return chatterMock(randomIdentity());
    }

    public static @NotNull ChatterMock chatterMock(Identity identity) {
        return new ChatterMock((Builder) Chatter.chatter(identity));
    }

    public void assertReceivedMessage(Component text) {
        assertThat(getMessages())
            .extracting(Message::text)
            .contains(text);
    }
}
