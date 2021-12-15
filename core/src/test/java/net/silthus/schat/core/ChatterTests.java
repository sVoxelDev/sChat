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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChatterTests extends MessageTargetTests<Chatter> {

    @Override
    @BeforeEach
    void setUp() {
        target = new Chatter();
    }

    @Test
    void getActiveChannel_isEmpty() {
        assertThat(target.getActiveChannel()).isEmpty();
    }

    @Test
    void setActiveChannel_returnsActiveChannel() {
        final Channel channel = new Channel();
        target.setActiveChannel(channel);
        assertThat(target.getActiveChannel())
            .isPresent().get()
            .isEqualTo(channel);
    }

    @Test
    void setActiveChannel_addsChannel() {
        final Channel channel = new Channel();
        target.setActiveChannel(channel);
        assertThat(target.getChannels()).contains(channel);
    }

    @Test
    void clearActiveChannel_unsetsActiveChannel() {
        target.setActiveChannel(new Channel());
        target.clearActiveChannel();
        assertThat(target.getActiveChannel()).isEmpty();
    }

    @Test
    void getChannels_isEmpty() {
        assertThat(target.getChannels()).isEmpty();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getChannels_isUnmodifiable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> target.getChannels().add(new Channel()));
    }

    @Test
    void addChannel_addsChannel() {
        final Channel channel = new Channel();
        target.addChannel(channel);
        assertThat(target.getChannels()).contains(channel);
    }

    @Test
    void removeChannel_removesChannel() {
        final Channel channel = new Channel();
        target.addChannel(channel);
        target.removeChannel(channel);
        assertThat(target.getChannels()).doesNotContain(channel);
    }

    @Test
    void removeChannel_removesActiveTarget_ifSame() {
        final Channel channel = new Channel();
        target.setActiveChannel(channel);
        target.removeChannel(channel);
        assertThat(target.getActiveChannel()).isEmpty();
    }
}
