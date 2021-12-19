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

package net.silthus.schat.core;

import net.silthus.schat.identity.PlayerOut;

public final class StubSenderFactory extends SenderFactory<FakePlayer> {

    public static StubSenderFactory createStubSenderFactory(FakePlayer player) {
        return new StubSenderFactory(player);
    }

    private StubSenderFactory(FakePlayer player) {
        super(new FakePlayerOut(player));
    }

    public StubSenderFactory(PlayerOut<FakePlayer> playerAdapter) {
        super(playerAdapter);
    }

    @Override
    protected Sender.SendMessage<FakePlayer> sendMessage() {
        return FakePlayer::sendMessage;
    }
}
