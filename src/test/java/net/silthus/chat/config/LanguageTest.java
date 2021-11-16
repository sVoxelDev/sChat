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

import net.kyori.adventure.text.format.NamedTextColor;
import net.silthus.chat.TestBase;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;

class LanguageTest extends TestBase {

    @Test
    void onEnable_loadsLanguageConfig() {
        assertThat(plugin.language()).isNotNull()
                .extracting(Language::locale)
                .isEqualTo(Locale.ENGLISH);
    }

    @Test
    void get_parsesLanguageAsMiniMessageFormat() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("test", "<gray>Hi there!");
        final Language language = new Language(cfg, Locale.ENGLISH);
        assertThat(language.get("test")).isEqualTo(text("Hi there!").color(NamedTextColor.GRAY));
    }
}