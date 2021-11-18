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

import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerConfigTest {

    @Test
    void fromEmptyConfig_loadsDefaultValues() {
        PlayerConfig config = PlayerConfig.playerConfig(new MemoryConfiguration());
        assertThat(config).isEqualTo(PlayerConfig.playerDefaults());
        assertThat(config)
                .extracting(
                        playerConfig -> playerConfig.nickNamePattern().pattern(),
                        PlayerConfig::blockedNickNames
                ).contains(
                        "[a-zA-Z0-9_]{3,16}",
                        List.of("Notch")
                );
    }

    @Test
    void fromConfig_setsValues() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("nickname_pattern", "[a-z]+");
        cfg.set("blocked_nicknames", List.of("abc", "def"));
        final PlayerConfig playerConfig = PlayerConfig.playerConfig(cfg);
        assertThat(playerConfig)
                .extracting(
                        config -> config.nickNamePattern().pattern(),
                        PlayerConfig::blockedNickNames
                ).contains(
                        "[a-z]+",
                        List.of("abc", "def")
                );
    }
}