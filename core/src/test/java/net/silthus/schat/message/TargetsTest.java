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
