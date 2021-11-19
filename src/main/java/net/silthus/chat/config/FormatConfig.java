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
import net.silthus.chat.Format;
import net.silthus.chat.Formats;
import net.silthus.chat.formats.MiniMessageFormat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import static net.silthus.chat.utils.AnnotationUtils.name;

@Value
@Builder(toBuilder = true)
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FormatConfig {

    public static FormatConfig formatConfig(ConfigurationSection config) {
        return formatDefaults().withConfig(config).build();
    }

    public static FormatConfig miniMessage(String format) {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("type", name(MiniMessageFormat.class));
        cfg.set("format", format);
        return formatConfig(cfg);
    }

    public static FormatConfig formatDefaults() {
        return FormatConfig.builder().build();
    }

    @Builder.Default
    String type = name(MiniMessageFormat.class);

    @Builder.Default
    ConfigurationSection config = new MemoryConfiguration();

    public FormatConfig.FormatConfigBuilder withConfig(ConfigurationSection config) {
        if (config == null) return toBuilder();
        return toBuilder()
                .type(config.getString("type", type))
                .config(config);
    }

    public Format toFormat() {
        return Formats.format(type, config).orElse(Formats.defaultFormat());
    }

    public FormatConfig registerAsTemplate(String name) {
        Formats.registerFormatTemplate(name, type, config);
        return this;
    }
}
