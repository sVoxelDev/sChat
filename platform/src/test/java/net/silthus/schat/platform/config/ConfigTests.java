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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.commands.CreatePrivateChannelCommand;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.ui.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.silthus.schat.channel.ChannelHelper.channelWith;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.channel.ChannelSettings.PROTECTED;
import static net.silthus.schat.chatter.ChatterMock.chatterMock;
import static net.silthus.schat.commands.CreatePrivateChannelCommand.createPrivateChannel;
import static net.silthus.schat.identity.Identity.identity;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;
import static net.silthus.schat.platform.config.ConfigKeys.VIEW_CONFIG;
import static net.silthus.schat.platform.config.TestConfigurationAdapter.testConfigAdapter;
import static net.silthus.schat.ui.format.Format.ACTIVE_CHANNEL_FORMAT;
import static net.silthus.schat.ui.format.Format.MESSAGE_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConfigTests {

    private static final MiniMessage FORMATTER = MiniMessage.miniMessage();
    private SChatConfig config;

    @BeforeEach
    void setUp(@TempDir File temp) {
        final ConfigurationAdapter adapter = testConfigAdapter(new File(temp, "test-config.yml"));
        config = new SChatConfig(adapter);
        config.load();
        CreatePrivateChannelCommand.prototype(builder -> builder.channelRepository(createInMemoryChannelRepository()));
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
            .format(View.empty(), message("Hey").source(identity("Notch")).create());
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
                .format(View.empty(), message("Hey").source(identity("Bob")).create());
            assertThat(FORMATTER.serialize(format)).isEqualTo("<aqua><Bob></aqua>: Hey");
        }

        @Test
        void uses_default_format_if_no_config_is_set() {
            final View view = mock(View.class);
            final ChatterMock source = chatterMock(identity("Bob"));
            final ChatterMock target = chatterMock(identity("Karl"));
            when(view.chatter()).thenReturn(source);
            final Channel channel = createPrivateChannel(source, target).channel();
            source.activeChannel(channel);
            final Component format = config.get(VIEW_CONFIG).privateChatFormat().get(ACTIVE_CHANNEL_FORMAT).format(view, channel);
            assertThat(FORMATTER.serialize(format)).isEqualTo("<green><underlined>Karl");
        }
    }
}
