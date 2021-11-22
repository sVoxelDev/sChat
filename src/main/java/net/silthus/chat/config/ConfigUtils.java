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

import com.google.common.base.Strings;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.silthus.chat.Constants;
import net.silthus.chat.Format;
import net.silthus.chat.Formats;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNullElseGet;

@Log(topic = Constants.PLUGIN_NAME)
public final class ConfigUtils {

    static Format getFormatFromConfig(@NonNull ConfigurationSection config, Format defaultFormat, String template, String format) {
        if (!config.isSet("format")) return defaultFormat;
        return Formats.format(config.getConfigurationSection("format"))
                .or(() -> Formats.formatFromTemplate(config.getString("format", template)))
                .orElseGet(() -> Formats.miniMessage(config.getString("format", format)));
    }

    @NotNull
    static ConfigurationSection getSection(@NonNull ConfigurationSection config, String section) {
        return getSection(config, section, false);
    }

    @NotNull
    static ConfigurationSection getSection(@NonNull ConfigurationSection config, String section, boolean suppressWarning) {
        final String path = Strings.isNullOrEmpty(config.getCurrentPath()) ? "" : config.getCurrentPath() + ".";
        return requireNonNullElseGet(
                config.getConfigurationSection(section),
                () -> suppressWarning ? config.createSection(section) : warnAndDefault(path + section, config.createSection(section))
        );
    }

    private static <TConfig> TConfig warnAndDefault(String section, TConfig defaultValue) {
        warnSectionNotDefined(section);
        return defaultValue;
    }

    static void warnSectionNotDefined(String section) {
        log.warning("No '" + section + "' section found inside your config.yml! Make sure your config is up-to-date with the config.default.yml.");
    }
}
