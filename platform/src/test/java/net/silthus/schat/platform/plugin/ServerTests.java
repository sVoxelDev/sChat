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

package net.silthus.schat.platform.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.ChatterMock;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.requireNonNull;
import static net.silthus.schat.channel.ChannelSettings.PROTECTED;
import static net.silthus.schat.platform.config.ConfigKeys.CHANNELS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

class ServerTests {

    private TestServer server;

    @BeforeEach
    void setUp() {
        server = new TestServer();
        server.load();
        server.enable();
    }

    @NotNull
    private InputStream testConfig() {
        return requireNonNull(getClass().getClassLoader().getResourceAsStream("reload-test.config.yml"));
    }

    @NotNull
    private Path configPath() {
        return server.configPath();
    }

    private void assertChannelExists(String key) {
        assertThat(server.channelRepository().contains(key)).isTrue();
    }

    private void assertChannelDoesNotExist(String key) {
        assertThat(server.channelRepository().contains(key)).isFalse();
    }

    @Test
    void channelRepository_is_not_null() {
        assertThat(server.channelRepository()).isNotNull();
    }

    @Test
    void commands_are_registered() {
        verify(TestServer.dummyCommand, atLeastOnce()).register(any(), any());
    }

    @Test
    void config_is_loaded() {
        assertThat(server.config()).isNotNull();
        assertThat(server.config().get(CHANNELS)).isNotNull();
    }

    @Test
    void channels_are_loaded() {
        assertChannelExists("global");
        assertChannelExists("test");
        assertChannelDoesNotExist("new_channel");
    }

    @Nested class reload {
        @BeforeEach
        void setUp() throws IOException {
            Files.copy(testConfig(), configPath(), REPLACE_EXISTING);
        }

        @Test
        void new_channel_is_loaded() {
            server.reload();
            assertChannelExists("new_channel");
        }

        @Test
        void old_channels_are_removed() {
            server.reload();
            assertChannelDoesNotExist("global");
        }

        @Test
        void existing_channels_are_updated() {
            server.reload();
            assertThat(server.channelRepository().get("test").isNot(PROTECTED)).isTrue();
        }

        @Test
        void removed_channel_targets_are_cleared() {
            final Channel channel = server.channelRepository().get("global");
            final ChatterMock chatter = ChatterMock.randomChatter();
            chatter.join(channel);
            server.reload();
            chatter.assertNotJoinedChannel(channel);
        }
    }
}
