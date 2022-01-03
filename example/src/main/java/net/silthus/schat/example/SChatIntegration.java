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

package net.silthus.schat.example;

import lombok.Getter;
import net.silthus.schat.SChat;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.settings.Setting;

import static net.kyori.adventure.text.Component.text;

final class SChatIntegration {

    private static final Setting<Boolean> IS_FACTION_CHANNEL = Setting.setting(Boolean.class, "faction_channel", false);
    private static final Setting<String> FACTION_NAME = Setting.setting(String.class, "faction", "global");

    @Getter
    private final SChat sChat;

    SChatIntegration(SChat sChat) {
        this.sChat = sChat;
    }

    void enable() {
        Channel.channel("custom_example")
            .displayName(text("Custom Channel"));
    }

    void disable() {

    }
}
