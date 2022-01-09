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

package net.silthus.schat;

import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageTarget;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.IdentityHelper.randomIdentity;
import static net.silthus.schat.TestHelper.assertNPE;
import static net.silthus.schat.UserHelper.randomUser;
import static net.silthus.schat.message.Message.emptyMessage;
import static net.silthus.schat.message.Message.message;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class MessageTests {

    private void assertMessageSent(MessageTarget... channels) {
        final Message message = message().to(channels).send();
        assertThat(message).isNotNull();
        for (final MessageTarget channel : channels) {
            verify(channel).sendMessage(message);
        }
    }

    @Test
    void givenEmptyMessage_textIsNotNull() {
        final Message message = emptyMessage();
        assertThat(message.getText()).isEqualTo(Component.empty());
    }

    @Test
    void givenEmptyMessage_sourceIsNil() {
        final Message message = emptyMessage();
        assertThat(message.getSource()).isEqualTo(Identity.nil());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void givenNullSource_throws() {
        assertNPE(() -> message().source(null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void givenMessageWithNullText_throws() {
        assertNPE(() -> message().text((Component) null));
        assertNPE(() -> message().text((String) null));
    }

    @Test
    void givenMessageWithText_textIsSet() {
        final Message message = message("Hi").create();
        assertThat(message.getText()).isEqualTo(text("Hi"));
    }

    @Test
    void givenMessageWithSource_sourceIsSet() {
        final Identity source = randomIdentity();
        final Message message = message().source(source).create();
        assertThat(message.getSource()).isEqualTo(source);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void givenNullTarget_throws() {
        assertNPE(() -> message().to((MessageTarget[]) null));
        assertNPE(() -> message().to((MessageTarget) null));
    }

    @Test
    void givenMessageWithNoTarget_send_doesNothing() {
        assertThatCode(() -> emptyMessage().send()).doesNotThrowAnyException();
    }

    @Test
    void givenMessageWithChannelTarget_sendsMessageToTarget() {
        assertMessageSent(mock(Channel.class));
    }

    @Test
    void givenMessageWithTwoChannelTargets_sendsMessageToBoth() {
        assertMessageSent(mock(Channel.class), mock(Channel.class));
    }

    @Test
    void givenMessageWithOneChannelAndOneUserTarget_sendsMessageToBoth() {
        assertMessageSent(spy(randomUser()), mock(Channel.class));
    }

    @Test
    void givenMessageWithoutType_usesSystemType() {
        assertThat(emptyMessage().getType()).isEqualTo(Message.Type.SYSTEM);
    }
}
