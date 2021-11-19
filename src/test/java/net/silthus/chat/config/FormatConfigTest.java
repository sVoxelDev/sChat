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

import net.silthus.chat.Format;
import net.silthus.chat.Formats;
import net.silthus.chat.TestBase;
import net.silthus.chat.formats.MiniMessageFormat;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormatConfigTest extends TestBase {

    @Test
    void create_defaultsToMiniMessageFormat() {
        final FormatConfig config = FormatConfig.formatDefaults();
        assertThat(config.type()).isEqualTo("mini-message");
        assertThat(config.config()).isInstanceOf(MemoryConfiguration.class);
    }

    @Test
    void create_fromConfig_setsProperties() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("type", "test");
        final FormatConfig config = FormatConfig.formatConfig(cfg);
        assertThat(config)
                .extracting(
                        FormatConfig::type,
                        FormatConfig::config
                ).contains(
                        "test",
                        cfg
                );
    }

    @Test
    void toFormat_createsFormatInstanceWithConfig() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("format", "<message>");
        Format format = FormatConfig.formatConfig(cfg).toFormat();
        assertThat(format).isNotNull()
                .isInstanceOf(MiniMessageFormat.class)
                .extracting("format")
                .isEqualTo("<message>");
    }

    @Test
    void registerAsTemplate_registersFormat() {
        FormatConfig.builder().build().registerAsTemplate("foobar");
        assertThat(Formats.formatFromTemplate("foobar")).isPresent();
    }
}