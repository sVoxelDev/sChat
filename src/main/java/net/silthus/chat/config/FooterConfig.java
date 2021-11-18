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

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.bukkit.configuration.ConfigurationSection;

@Log
@Value
@With
@Builder(toBuilder = true)
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FooterConfig {

    public static FooterConfig defaultFooter() {
        return FooterConfig.builder().build();
    }

    public static FooterConfig footerConfig(ConfigurationSection config) {
        return defaultFooter().withConfig(config).build();
    }

    @Builder.Default
    boolean enabled = true;

    FooterConfig.FooterConfigBuilder withConfig(ConfigurationSection config) {
        if (config == null) return toBuilder();
        return toBuilder()
                .enabled(config.getBoolean("enabled", enabled));
    }
}
