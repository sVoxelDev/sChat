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

package net.silthus.schat.message;

import net.silthus.schat.chatter.Chatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.message.MessageHelper.randomMessage;
import static net.silthus.schat.message.MessageTargetSpy.targetSpy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class TargetsTest {

    private Targets targets;
    private MessageTargetSpy target1;
    private MessageTargetSpy target2;

    @BeforeEach
    void setUp() {
        targets = new Targets();
        target1 = targetSpy();
        target2 = targetSpy();
    }

    @Nested
    @DisplayName("given initial targets")
    class GivenTargets {

        @Test
        @DisplayName("contains initial targets")
        void create_with_initial_targets() {
            targets = Targets.of(target1, target2);
            assertThat(targets).hasSize(2);
            assertThat(targets).containsExactly(target1, target2);
        }
    }

    @Nested
    @DisplayName("add(...)")
    class AddTarget {

        @Test
        @DisplayName("adds target")
        void adds_target() {
            targets.add(target1);
            assertThat(targets).containsExactly(target1);
        }

        @Test
        @DisplayName("only adds unique targets")
        void adds_only_unique() {
            targets.add(target1);
            targets.add(target1);
            assertThat(targets).hasSize(1).containsOnly(target1);
        }
    }

    @Nested
    @DisplayName("sendMessage(...)")
    class SendMessage {

        @Test
        void sends_message_to_all_targets() {
            targets = Targets.of(target1, target2);
            final Message message = randomMessage();
            targets.sendMessage(message);
            target1.assertReceivedMessage(message);
            target2.assertReceivedMessage(message);
        }
    }

    @Nested
    @DisplayName("filter(...)")
    class WithFilter {

        @Test
        void filters_targets() {
            targets.add(target1);
            assertThat(targets.filter(target -> target instanceof Chatter)).isEmpty();
            assertThat(targets.filter(target -> target instanceof MessageTargetSpy)).hasSize(1);
        }
    }

    @Nested
    @DisplayName("unmodifiable()")
    class SealTargets {

        @Test
        @SuppressWarnings("ConstantConditions")
        void makes_targets_unmodifiable() {
            targets.add(target1);
            final Targets unmodifiable = Targets.unmodifiable(targets);
            assertThat(unmodifiable).isEqualTo(targets);
            assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> unmodifiable.add(target2));
        }
    }
}
