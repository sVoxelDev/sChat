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

package net.silthus.schat.platform.sender;

import java.util.LinkedList;
import java.util.Queue;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;

import static net.silthus.schat.identity.IdentityHelper.randomIdentity;
import static org.assertj.core.api.Assertions.assertThat;

@Getter
public class SenderMock implements Sender {

    public static SenderMock senderMock() {
        return new SenderMock(randomIdentity());
    }

    public static SenderMock senderMock(Identity identity) {
        return new SenderMock(identity);
    }

    private final Identity identity;
    private final Queue<Component> messages = new LinkedList<>();

    public SenderMock(Identity identity) {
        this.identity = identity;
    }

    @Override
    public void sendMessage(Component message) {
        messages.add(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return false;
    }

    @Override
    public void performCommand(String commandLine) {

    }

    @Override
    public boolean isConsole() {
        return false;
    }

    public void assertLastMessageIs(Component component) {
        assertThat(messages.peek()).isEqualTo(component);
    }
}
