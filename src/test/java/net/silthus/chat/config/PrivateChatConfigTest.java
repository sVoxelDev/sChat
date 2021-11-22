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

import net.silthus.chat.TestBase;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.chat.Constants.Formatting.NO_FORMAT;
import static net.silthus.chat.Formats.defaultFormat;
import static org.assertj.core.api.Assertions.assertThat;

class PrivateChatConfigTest extends TestBase {

    @Test
    void isLoadedInPluginConfig() {
        assertDefaults(plugin.getPluginConfig().privateChat());
    }

    @Test
    void fromEmptyConfig_loadsDefaultValues() {
        PrivateChatConfig config = PrivateChatConfig.privateChatConfig(new MemoryConfiguration());
        assertDefaults(config);
    }

    @Test
    void fromConfig_setsValues() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("global", false);
        cfg.set("format", NO_FORMAT);
        cfg.set("name", "<sender_name><-><partner_name>");
        final PrivateChatConfig playerConfig = PrivateChatConfig.privateChatConfig(cfg);
        assertThat(playerConfig)
                .extracting(
                        PrivateChatConfig::global,
                        PrivateChatConfig::format,
                        PrivateChatConfig::name
                ).contains(
                        false,
                        defaultFormat(NO_FORMAT),
                        text("<sender_name><-><partner_name>")
                );
    }

    private void assertDefaults(PrivateChatConfig config) {
        assertThat(config).isEqualTo(PrivateChatConfig.privateChatDefaults());
    }
}