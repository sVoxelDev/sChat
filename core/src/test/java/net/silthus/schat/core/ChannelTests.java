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

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;

class ChannelTests extends MessageTargetTests<Channel> {

    @BeforeEach
    void setUp() {
        target = new Channel();
    }

    @NotNull
    private MessageTarget addTarget(MessageTarget target) {
        this.target.addTarget(target);
        return target;
    }

    @Test
    void getTargets_isEmpty() {
        assertThat(target.getTargets()).isEmpty();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void getTargets_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> target.getTargets().add(new DummyTarget()));
    }

    @Test
    void addTarget_addsTarget() {
        final MessageTarget target = addTarget(new DummyTarget());
        assertThat(this.target.getTargets()).contains(target);
    }

    @Test
    void sendMessage_forwardsMessage_toAllTargets() {
        final MessageTarget spy = addTarget(Mockito.spy(MessageTarget.class));
        final Message message = randomMessage();
        target.sendMessage(message);
        verify(spy).sendMessage(message);
    }
}
