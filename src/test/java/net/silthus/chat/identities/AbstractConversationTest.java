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

package net.silthus.chat.identities;

import net.silthus.chat.Chatter;
import net.silthus.chat.Conversation;
import net.silthus.chat.TestBase;
import net.silthus.chat.conversations.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractConversationTest extends TestBase {

    private Conversation conversation;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        conversation = createChannel("testing");
    }

    @Test
    void subscribe_returnsSubscription() {
        conversation.addTarget(Chatter.player(server.addPlayer()));
    }

    @Test
    void sorted_byName() {
        Channel one = Channel.createChannel("Abc");
        Channel two = Channel.createChannel("def");
        Conversation three = Conversation.privateConversation(Chatter.player(server.addPlayer()), Chatter.player(server.addPlayer()));
        List<Conversation> conversations = List.of(
                three,
                two,
                one
        );

        List<Conversation> sorted = conversations.stream().sorted().collect(Collectors.toList());
        assertThat(sorted).containsExactly(
                one,
                two,
                three
        );
    }
}