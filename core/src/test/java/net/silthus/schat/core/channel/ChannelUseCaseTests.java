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

package net.silthus.schat.core.channel;

import net.bytebuddy.utility.RandomString;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.Channels;
import net.silthus.schat.core.DummyTarget;
import net.silthus.schat.core.TestBase;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ChannelUseCaseTests extends TestBase {

    private static final String TEST_CHANNEL_ALIAS = "test";

    private Channels channels;

    @BeforeEach
    void setUp() {
        channels = new ChannelInteractor(new InMemoryChannelRepository());
    }

    private Channel createChannel() {
        return channels.create(RandomString.make().toLowerCase());
    }

    @NotNull
    private Channel createChannel(String alias) {
        return channels.create(alias);
    }

    @NotNull
    private Channel createChannel(Component name) {
        return channels.create(RandomString.make().toLowerCase(), name);
    }

    @Test
    void all_isEmpty_byDefault() {
        assertThat(channels.all()).isEmpty();
    }

    @Test
    void create_createsNewChannel() {
        assertThat(createChannel()).isNotNull();
    }

    @Test
    void create_addsChannelToRegistry() {
        final Channel channel = createChannel(TEST_CHANNEL_ALIAS);
        assertThat(channels.all()).contains(channel);
        assertThat(channels.contains(TEST_CHANNEL_ALIAS)).isTrue();
    }

    @Test
    void create_sameAlias_throws() {
        createChannel(TEST_CHANNEL_ALIAS);
        assertThatExceptionOfType(Channels.DuplicateIdentifier.class)
            .isThrownBy(() -> createChannel(TEST_CHANNEL_ALIAS));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "a a b c", "ABnsda?", "  ", ""})
    void create_givenInvalidAlias_throws(String alias) {
        assertThatExceptionOfType(Channel.InvalidKey.class)
            .isThrownBy(() -> channels.create(alias));
    }

    @Test
    void get_getsChannelByAlias() {
        final Channel channel = createChannel(TEST_CHANNEL_ALIAS);
        assertThat(channels.get(TEST_CHANNEL_ALIAS))
            .isPresent().get()
            .isEqualTo(channel);
    }

    @Test
    void createChannel_withDisplayName() {
        final TextComponent displayName = text("Foobar");
        final Channel channel = channels.create(TEST_CHANNEL_ALIAS, displayName);
        assertThat(channel.getDisplayName()).isEqualTo(displayName);
    }

    @Test
    void createChannel_withAlias() {
        final Channel channel = createChannel(TEST_CHANNEL_ALIAS);
        assertThat(channel.getKey()).isEqualTo(TEST_CHANNEL_ALIAS);
    }

    @Test
    void createChannel_withoutDisplayName_usesAlias() {
        final Channel channel = createChannel(TEST_CHANNEL_ALIAS);
        assertThat(channel.getDisplayName()).isEqualTo(text(TEST_CHANNEL_ALIAS));
    }

    @Test
    void createChannel_withDisplayName_usesName() {
        final TextComponent name = text("Bar");
        final Channel channel = createChannel(name);
        assertThat(channel.getDisplayName()).isEqualTo(name);
    }

    @Test
    void createChannel_withEmptyAlias_throws() {
        assertThatExceptionOfType(Channel.InvalidKey.class)
            .isThrownBy(() -> createChannel("   "));
    }

    @Nested
    class ChannelTests {

        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = createChannel(TEST_CHANNEL_ALIAS);
        }

        @Test
        void getTargets_isEmpty() {
            assertThat(channel.getTargets()).isEmpty();
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        void getTargets_isImmutable() {
            assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> channel.getTargets().add(new DummyTarget()));
        }

        @Test
        void addTarget_addsTarget() {
            final MessageTarget target = addTarget(new DummyTarget());
            assertThat(this.channel.getTargets()).contains(target);
        }

        @Test
        void sendMessage_forwardsMessage_toAllTargets() {
            final MessageTarget spy = addTarget(spy(MessageTarget.class));
            final Message message = randomMessage();
            channel.sendMessage(message);
            verify(spy).sendMessage(message);
        }

        @Test
        void setDisplayName_setsProp() {
            final TextComponent displayName = text("foo bar");
            channel.setDisplayName(displayName);
            assertThat(channel.getDisplayName()).isEqualTo(displayName);
        }

        @NotNull
        private MessageTarget addTarget(MessageTarget target) {
            this.channel.addTarget(target);
            return target;
        }
    }
}
