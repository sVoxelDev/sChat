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

import net.silthus.chat.Scopes;
import net.silthus.chat.TestBase;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WorldGuardConfigTest extends TestBase {

    @Test
    void isLoaded() {
        assertDefaults(plugin.getPluginConfig().worldGuard());
    }

    @Test
    void fromEmptyConfig_loadsDefaultValues() {
        WorldGuardConfig config = WorldGuardConfig.worldGuardConfig(plugin.getPluginConfig().defaults(), new MemoryConfiguration());
        assertDefaults(config);
    }

    @Test
    void loadsRegionChannelRegions() {
        final WorldGuardConfig config = createConfig();
        assertThat(config.regionConfigs()).isNotNull()
                .containsEntry("test", ChannelConfig.builder().scope(Scopes.global()).name("Test Region").build());
    }

    @Test
    void regionConfig_returnsSpecificRegionConfig() {
        final WorldGuardConfig config = createConfig();
        assertThat(config.regionConfig("foobar")).isNotNull().isEqualTo(config.defaultChannelConfig());
        assertThat(config.regionConfig("test")).isNotNull().isEqualTo(config.defaultChannelConfig().withName("Test Region"));
    }

    @Test
    void regionConfig_usesDefaults() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("channel_configs.default.force", true);
        final WorldGuardConfig config = WorldGuardConfig.worldGuardConfig(new PluginConfig.Defaults(ChannelConfig.channelDefaults()), cfg);
        final ChannelConfig channelConfig = config.regionConfig("test");
        assertThat(channelConfig.canLeave()).isFalse();
    }

    private WorldGuardConfig createConfig() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("channel_configs.default.scope", "global");
        cfg.set("channel_configs.regions.test.name", "Test Region");
        return WorldGuardConfig.worldGuardConfig(plugin.getPluginConfig().defaults(), cfg);
    }

    private void assertDefaults(WorldGuardConfig config) {
        assertThat(config)
                .extracting(WorldGuardConfig::enabled)
                .isEqualTo(true);
    }
}