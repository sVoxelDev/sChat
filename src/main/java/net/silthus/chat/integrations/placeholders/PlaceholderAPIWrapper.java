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

package net.silthus.chat.integrations.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.silthus.chat.identities.PlayerChatter;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class PlaceholderAPIWrapper extends BasicPlaceholders {

    @Override
    public Component setPlaceholders(PlayerChatter chatter, Component text) {
        final Component component = super.setPlaceholders(chatter, text);
        return chatter.getPlayer()
                .map(player -> component
                        .replaceText(replacePlaceholderAPIPlaceholders(player))
                        .replaceText(replaceBracketPlaceholders(player))
                )
                .orElse(component);
    }

    private TextReplacementConfig replacePlaceholderAPIPlaceholders(Player player) {
        return TextReplacementConfig.builder()
                .match(Pattern.compile("(%[a-zA-Z0-9_-]+%)"))
                .replacement((matchResult, builder) -> Component.text(PlaceholderAPI.setPlaceholders(player, matchResult.group())))
                .build();
    }

    private TextReplacementConfig replaceBracketPlaceholders(Player player) {
        return TextReplacementConfig.builder()
                .match(Pattern.compile("(\\{[a-zA-Z0-9_-]+})"))
                .replacement((matchResult, builder) -> Component.text(PlaceholderAPI.setBracketPlaceholders(player, matchResult.group())))
                .build();
    }
}
