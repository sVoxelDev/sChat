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

package net.silthus.schat.bukkit;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.core.sender.Sender;
import net.silthus.schat.core.sender.SenderFactory;
import org.jetbrains.annotations.NotNull;

public final class BukkitAudiencesSenderFactory implements SenderFactory {

    private final BukkitAudiences audiences;

    public BukkitAudiencesSenderFactory(final @NotNull BukkitAudiences audiences) {
        this.audiences = audiences;
    }

    @Override
    public Sender createSender(final Chatter chatter) {
        return new AudienceSender(audiences.player(chatter.getId()));
    }

    private static class AudienceSender implements Sender {

        private final Audience audience;

        AudienceSender(final Audience audience) {
            this.audience = audience;
        }

        @Override
        public void sendMessage(final Component component) {
            audience.sendMessage(component);
        }
    }
}
