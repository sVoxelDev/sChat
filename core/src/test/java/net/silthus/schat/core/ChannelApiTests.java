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

package net.silthus.schat.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.silthus.schat.Channels;
import net.silthus.schat.Message;
import net.silthus.schat.Target;
import net.silthus.schat.core.channel.Channel;
import net.silthus.schat.core.channel.ChannelRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ChannelApiTests extends TestBase {

    private static final String CHANNEL_ALIAS = "test";

    private Channels repository;
    private net.silthus.schat.Channel channel;

    @BeforeEach
    void setUp() {
        repository = new ChannelRepository().getApiProxy();
        channel = createChannel(CHANNEL_ALIAS);
    }

    @NotNull
    private net.silthus.schat.Channel createChannel(String alias) {
        return repository.create(alias);
    }

    @NotNull
    private Channel createChannel(Component name) {
        return new Channel(ChannelApiTests.CHANNEL_ALIAS, name);
    }

    @NotNull
    private Target addTarget(Target target) {
        this.channel.addTarget(target);
        return target;
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
        final Target target = addTarget(new DummyTarget());
        assertThat(this.channel.getTargets()).contains(target);
    }

    @Test
    void sendMessage_forwardsMessage_toAllTargets() {
        final Target spy = addTarget(spy(Target.class));
        final Message message = randomMessage();
        channel.sendMessage(message);
        verify(spy).sendMessage(message);
    }

    @Test
    void createChannel_withAlias() {
        assertThat(channel.getAlias()).isEqualTo(CHANNEL_ALIAS);
    }

    @Test
    void createChannel_withoutDisplayName_usesAlias() {
        assertThat(channel.getDisplayName()).isEqualTo(text(CHANNEL_ALIAS));
    }

    @Test
    void createChannel_withDisplayName_usesName() {
        final TextComponent name = text("Bar");
        final Channel channel = createChannel(name);
        assertThat(channel.getDisplayName()).isEqualTo(name);
    }

    @Test
    void createChannel_withEmptyAlias_throws() {
        assertThatExceptionOfType(net.silthus.schat.Channel.InvalidAlias.class)
            .isThrownBy(() -> createChannel("   "));
    }

    @Test
    void setDisplayName_setsProp() {
        final TextComponent displayName = text("foo bar");
        channel.setDisplayName(displayName);
        assertThat(channel.getDisplayName()).isEqualTo(displayName);
    }
}
