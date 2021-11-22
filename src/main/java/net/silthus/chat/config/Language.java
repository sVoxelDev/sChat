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

import lombok.Value;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.silthus.chat.Constants;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Locale;

import static net.kyori.adventure.text.Component.text;

@Value
@Accessors(fluent = true)
public class Language {

    public static Language language(ConfigurationSection config, Locale locale) {
        return new Language(config, locale);
    }

    ConfigurationSection config;
    MiniMessage parser;
    Locale locale;

    private Language(ConfigurationSection config, Locale locale) {
        this.config = config;
        this.parser = MiniMessage.miniMessage();
        this.locale = locale;
    }

    public Language commands() {
        return section(Constants.Language.Commands.COMMANDS_BASE);
    }

    public Language section(String key) {
        final ConfigurationSection section = config.getConfigurationSection(key);
        if (section == null) return new Language(config.createSection(key), locale);
        return new Language(section, locale);
    }

    public Component get(String key, Component defaultValue) {
        final String content = config.getString(key);
        if (content == null) return defaultValue;
        return text(content);
    }

    public Component get(String key, String defaultValue) {
        return text(config.getString(key, defaultValue));
    }

    public Component get(String key) {
        if (!config.isSet(key)) return Component.text("<INVALID_LANGUAGE_KEY:" + key + ">").color(NamedTextColor.RED);
        final String string = config.getString(key);
        if (string == null) return Component.empty();
        return parser.deserialize(string);
    }
}
