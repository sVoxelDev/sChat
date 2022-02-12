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

package net.silthus.schat.platform.commands;

import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.commands.CreatePrivateChannelCommand;
import net.silthus.schat.platform.locale.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelSettings.PRIVATE;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static org.assertj.core.api.Assertions.assertThat;

class PrivateMessageCommandsTests extends CommandTest {

    public static final String TEXT = "Hi you there!";
    private ChatterMock target;

    @BeforeEach
    void setUp() {
        commands.register(new PrivateMessageCommands());
        target = addChatter(randomChatter());
        CreatePrivateChannelCommand.prototype(builder -> builder.channelRepository(channelRepository));
    }

    @DisplayName("/tell <player>")
    @Nested class sendPrivateMessage {

        private void sendPM() {
            cmd("/tell " + target.name() + " " + TEXT);
        }

        private void assertPrivateChannelIsActive() {
            assertThat(chatter.activeChannel()).isPresent().get()
                .matches(channel -> channel.is(PRIVATE));
        }

        @Test
        void sends_private_message() {
            sendPM();
            target.assertReceivedMessage(Component.text(TEXT));
        }

        @Test
        void given_no_text_sets_channel_active() {
            cmd("/tell " + target.name());
            assertPrivateChannelIsActive();
        }

        @Test
        void sets_private_channel_as_active() {
            sendPM();
            assertPrivateChannelIsActive();
        }

        @Test
        void given_target_and_source_are_same_prints_error_message() {
            cmd("/tell " + chatter.name() + " hey");
            chatter.assertReceivedMessage(Messages.CANNOT_SEND_PM_TO_SELF.build());
        }
    }
}
