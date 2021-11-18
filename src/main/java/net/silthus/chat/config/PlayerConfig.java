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

package net.silthus.chat.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Value
@Builder(toBuilder = true)
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerConfig {

    public static PlayerConfig playerConfig(ConfigurationSection config) {
        return playerDefaults().withConfig(config).build();
    }

    public static PlayerConfig playerDefaults() {
        return builder().build();
    }

    @Builder.Default
    Pattern nickNamePattern = Pattern.compile("[a-zA-Z0-9_]{3,16}");
    @Builder.Default
    List<String> blockedNickNames = List.of("Notch");

    public PlayerConfig.PlayerConfigBuilder withConfig(ConfigurationSection config) {
        if (config == null) return toBuilder();
        final String pattern = config.getString("nickname_pattern");
        return toBuilder()
                .nickNamePattern(pattern != null ? Pattern.compile(pattern) : nickNamePattern)
                .blockedNickNames(getBlockedNickNames(config));
    }

    private List<String> getBlockedNickNames(ConfigurationSection config) {
        if (!config.isSet("blocked_nicknames")) return blockedNickNames;
        return config.getStringList("blocked_nicknames");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerConfig that)) return false;
        return nickNamePattern.pattern().equals(that.nickNamePattern.pattern()) && blockedNickNames.equals(that.blockedNickNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickNamePattern, blockedNickNames);
    }
}
