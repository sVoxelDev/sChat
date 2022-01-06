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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import net.silthus.schat.SChat;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.checks.JoinChannel;
import net.silthus.schat.settings.Setting;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.channel.Channel.JOIN_PERMISSION;
import static net.silthus.schat.channel.Channel.REQUIRES_JOIN_PERMISSION;
import static net.silthus.schat.checks.Check.failure;
import static net.silthus.schat.checks.Check.success;
import static net.silthus.schat.permission.Permission.of;

final class SChatIntegration {

    private static final Setting<Boolean> IS_FACTION_CHANNEL = Setting.setting(Boolean.class, "faction_channel", false);
    private static final Setting<String> FACTION_NAME = Setting.setting(String.class, "faction", "global");

    private final List<Channel> factionChannels = new ArrayList<>();

    @Getter
    private final SChat sChat;

    SChatIntegration(SChat sChat) {
        this.sChat = sChat;
    }

    void enable() {
        factionChannels.add(Channel.channel("my-faction")
            .displayName(text("My Faction"))
            .setting(REQUIRES_JOIN_PERMISSION, true)
            .setting(JOIN_PERMISSION, of("faction.my-faction"))
            .setting(IS_FACTION_CHANNEL, true)
            .setting(FACTION_NAME, "my-faction")
            .check(new JoinFactionChannelCheck())
            .create());

        for (final Channel factionChannel : factionChannels) {
            getSChat().getChannels().add(factionChannel);
        }
    }

    void disable() {
        for (final Channel factionChannel : factionChannels) {
            getSChat().getChannels().remove(factionChannel);
        }
    }

    private final class JoinFactionChannelCheck implements JoinChannel {

        @Override
        public Result test(Args args) {
            if (!args.channel().get(IS_FACTION_CHANNEL)) return success();
            if (isFactionMember(args.chatter().getUniqueId(), args.channel().get(FACTION_NAME)))
                return success();
            return failure(new Error("Not a member of the channel's faction!"));
        }
    }

    private boolean isFactionMember(UUID playerId, String faction) {
        return switch (faction) {
            case "my-faction", "global" -> true;
            default -> false;
        };
    }
}
