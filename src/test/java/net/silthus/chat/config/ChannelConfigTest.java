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
import net.silthus.chat.*;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.scopes.GlobalScope;
import net.silthus.chat.scopes.LocalScope;
import net.silthus.chat.scopes.WorldScope;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelConfigTest extends TestBase {

    @Test
    void load_fromConfig() {
        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("name", "Test");
        cfg.set("format", "TEST: <message>");
        cfg.set("protect", true);
        cfg.set("console", false);
        cfg.set("auto_join", false);
        cfg.set("force", true);
        cfg.set("scope", "global");
        cfg.set("footer.enabled", false);

        ChannelConfig expected = ChannelConfig.builder()
                .name("Test")
                .format(Formats.miniMessage("TEST: <message>"))
                .protect(true)
                .sendToConsole(false)
                .autoJoin(false)
                .canLeave(false)
                .scope(new GlobalScope())
                .footer(FooterConfig.builder().enabled(false).build())
                .build();
        ChannelConfig config = ChannelConfig.channelConfig(cfg);
        assertThat(config).isEqualTo(expected);
    }

    @Test
    void toChannel_createsChannelWithConfig() {
        Channel channel = ChannelConfig.channelDefaults()
                .withName("Test 1")
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
        ChannelConfig config = ChannelConfig.channelConfig(cfg);

        Component component = Message.message(ChatSource.named("source"), "test")
                .format(config.format())
                .to(Channel.channel("test"))
                .build()
                .formatted();

        assertThat(config.format()).isNotNull();
        assertThat(toText(component)).isEqualTo("&6[&atest&6]&esource&7: test");
    }

    @Test
    void loadWorldScope_withWorldsConfig() {
        List<String> worlds = List.of("world", "world_nether");
        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("scope", "world");
        cfg.set("worlds", worlds);
        ChannelConfig config = ChannelConfig.channelConfig(cfg);

        assertThat(config.scope())
                .isNotNull().isInstanceOf(WorldScope.class)
                .extracting("worlds")
                .isEqualTo(worlds);
    }

    @Test
    void loadLocalScope_setsRange() {
        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("scope", "local");
        cfg.set("range", 20);
        ChannelConfig config = ChannelConfig.channelConfig(cfg);

        assertThat(config.scope())
                .isInstanceOf(LocalScope.class)
                .extracting("range")
                .isEqualTo(20);
    }

    @Test
    void withTemplateFormat() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("format", "none");
        final ChannelConfig config = ChannelConfig.channelConfig(cfg);
        assertThat(config.format())
                .extracting("format")
                .isEqualTo(Constants.Formatting.NO_FORMAT_FORMAT);
    }

    @Test
    void withFormatConfigSection() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("format.type", "mini-message");
        cfg.set("format.format", "TESTING: <message>");
        final ChannelConfig config = ChannelConfig.channelConfig(cfg);
        assertThat(config.format())
                .extracting("format")
                .isEqualTo("TESTING: <message>");
    }
}