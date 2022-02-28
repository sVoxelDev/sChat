/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.silthus.schat.platform.config;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.commands.CreatePrivateChannelCommand;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.ui.view.View;
import net.silthus.schat.ui.views.tabbed.Tab;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.channel.ChannelSettings.PROTECTED;
import static net.silthus.schat.chatter.ChatterMock.chatterMock;
import static net.silthus.schat.commands.CreatePrivateChannelCommand.createPrivateChannel;
import static net.silthus.schat.identity.Identity.identity;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.message.MessageSource.of;
import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;
import static net.silthus.schat.platform.config.ConfigKeys.VIEW_CONFIG;
import static net.silthus.schat.platform.config.TestConfigurationAdapter.testConfigAdapter;
import static net.silthus.schat.ui.format.Format.ACTIVE_TAB_FORMAT;
import static net.silthus.schat.ui.format.Format.MESSAGE_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConfigTests {

    private SChatConfig config;

    @BeforeEach
    void setUp(@TempDir File temp) {
        final ConfigurationAdapter adapter = testConfigAdapter(new File(temp, "test-config.yml"));
        config = new SChatConfig(adapter);
        config.load();
        CreatePrivateChannelCommand.prototype(builder -> builder.channelRepository(createInMemoryChannelRepository(EventBus.empty())));
    }

    private Channel getTestChannelConfig() {
        return config.get(CHANNELS).values().stream()
            .filter(channel -> channel.key().equals("test"))
            .findFirst().orElseThrow()
            .toChannel();
    }

    @Test
    void loads_parsed_channel_name() {
        assertThat(getTestChannelConfig().displayName()).isEqualTo(text("Test"));
    }

    @Test
    void loads_defined_channel_settings() {
        assertThat(getTestChannelConfig().settings().get(PROTECTED)).isTrue();
    }

    @Test
    void loads_message_format() {
        final Component format = getTestChannelConfig().settings()
            .get(MESSAGE_FORMAT)
            .format(View.empty(), message("Hey").source(of(identity("Notch"))).create());
        assertThat(format).isEqualTo(text("Notch", YELLOW).append(text(": Hey", GRAY)));
    }

    @Test
    void set_values_writes_and_loads_when_reloaded() {
        final TextComponent name = text("Test Name");
        final ChannelConfig channel = ChannelConfig.fromChannel(channelWith("test", builder -> builder.name(name)));
        final Map<String, ChannelConfig> configs = config.get(CHANNELS);
        configs.put(channel.key(), channel);

        config.set(CHANNELS, configs);
        config.save();
        config.reload();

        assertThat(getTestChannelConfig().displayName()).isEqualTo(name);
    }

    @Nested class view_config {

        @Test
        void loads_custom_private_chat_message_format() {
            final Component format = config.get(VIEW_CONFIG).format().get(MESSAGE_FORMAT)
                .format(View.empty(), message("Hey").source(of(identity("Bob"))).create());
            assertThat(format).isEqualTo(text().append(text("[Bob]", AQUA)).append(text(": Hey")).build());
        }

        @Test
        void uses_default_format_if_no_config_is_set() {
            final View view = mock(View.class);
            final ChatterMock source = chatterMock(identity("Bob"));
            final ChatterMock target = chatterMock(identity("Karl"));
            when(view.chatter()).thenReturn(source);
            final Channel channel = createPrivateChannel(source, target).channel();
            source.activeChannel(channel);
            final Tab tab = mock(Tab.class);
            when(tab.get(Tab.CHANNEL)).thenReturn(Optional.of(channel));
            final Component format = config.get(VIEW_CONFIG).privateChatFormat().get(ACTIVE_TAB_FORMAT).format(view, tab);
            assertThat(format).isEqualTo(text("Karl", GREEN, UNDERLINED));
        }
    }
}
