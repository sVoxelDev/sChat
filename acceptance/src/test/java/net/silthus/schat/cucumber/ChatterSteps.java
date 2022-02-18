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
package net.silthus.schat.cucumber;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javax.inject.Inject;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;

import static net.silthus.schat.message.MessageHelper.randomText;
import static org.assertj.core.api.Assertions.assertThat;

public class ChatterSteps {

    private final Context context;

    @Inject
    public ChatterSteps(Context context) {
        this.context = context;
    }

    public Chatter me() {
        return context.user("I").chatter();
    }

    @ParameterType("I|[a-zA-Z0-9]+")
    public Chatter chatter(String name) {
        return context.user(name).chatter();
    }

    @When("{chatter} send(s) a message")
    public void sendMessage(Chatter chatter) {
        chatter.activeChannel().ifPresent(channel -> context.lastMessage(chatter.message(randomText()).to(channel).send()));
    }

    @Then("{chatter} receives the message")
    public void receiveMessage(Chatter chatter) {
        assertThat(chatter.lastMessage()).isPresent()
            .get().extracting(Message::text)
            .isEqualTo(context.lastMessageText());
    }

    @Then("{chatter} does not receive a message")
    public void playerDoesNotReceiveAMessage(Chatter chatter) {
        assertThat(chatter.lastMessage()).isEmpty();
    }

    @Then("{chatter} am/is a member of the {channel} channel")
    public void checkMemberOf(Chatter chatter, Channel channel) {
        assertThat(chatter.isJoined(channel)).isTrue();
    }

    @Then("{chatter} am/is not a member of the {channel} channel")
    public void iAmNotAMemberOfTheProtectedChannel(Chatter chatter, Channel channel) {
        assertThat(chatter.isJoined(channel)).isFalse();
    }

    @Then("the {channel} channel is active")
    public void theGlobalChannelIsActive(Channel channel) {
        assertThat(me().isActiveChannel(channel)).isTrue();
    }
}
