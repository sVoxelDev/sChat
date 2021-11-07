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

import net.kyori.adventure.text.Component;
import net.silthus.chat.ChatSource;
import net.silthus.chat.Format;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import net.silthus.chat.conversations.Channel;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelConfigTest extends TestBase {

    @Test
    void load_fromConfig() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("name", "Test");
        cfg.set("format", "<message>");
        cfg.set("protect", true);
        cfg.set("console", false);
        cfg.set("auto_join", false);
        cfg.set("global", false);

        ChannelConfig expected = ChannelConfig.builder()
                .name("Test")
                .format(Format.format("<message>"))
                .protect(true)
                .sendToConsole(false)
                .autoJoin(false)
                .global(false)
                .build();
        ChannelConfig config = ChannelConfig.of(cfg);
        assertThat(config).isEqualTo(expected);
    }

    @Test
    void toChannel_createsChannelWithConfig() {

        Channel channel = ChannelConfig.defaults()
                .name("Test 1")
                .toChannel("test");
        assertThat(channel)
                .extracting(
                        Channel::getName,
                        c -> toText(c.getDisplayName())
                ).contains(
                        "test",
                        "Test 1"
                );
    }

    @Test
    void nullFormat_usesDefaultFormat() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        ChannelConfig config = ChannelConfig.of(cfg);

        Component component = Message.message(ChatSource.named("source"), "test")
                .format(config.format())
                .to(Channel.channel("test"))
                .build()
                .formatted();

        assertThat(config.format()).isNotNull();
        assertThat(toText(component)).isEqualTo("&6[&atest&6]&esource&7: test");
    }
}