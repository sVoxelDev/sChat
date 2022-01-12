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

package net.silthus.schat.platform;

import lombok.Getter;
import lombok.NonNull;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.platform.sender.SenderFactory;
import org.jetbrains.annotations.ApiStatus;

import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;

@Getter
public abstract class AbstractSChatPlugin implements SChatPlugin {

    private ChannelRepository channelRepository;
    private SenderFactory<?> senderFactory;

    public final void enable() {
        senderFactory = provideUserFactory();

        channelRepository = provideChannelRepository();
    }

    @ApiStatus.OverrideOnly
    protected ChannelRepository provideChannelRepository() {
        return createInMemoryChannelRepository();
    }

    protected abstract SenderFactory<?> provideUserFactory();

    @Override
    @SuppressWarnings("unchecked")
    public final <T> SenderFactory<T> getUserFactory(@NonNull Class<T> playerClass) {
        senderFactory.checkPlayerType(playerClass);
        return (SenderFactory<T>) senderFactory;
    }
}
