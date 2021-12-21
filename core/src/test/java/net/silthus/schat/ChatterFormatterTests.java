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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.examination.string.StringExaminer;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;

class ChatterFormatterTests {

    private Chatter chatter;

    @BeforeEach
    void setUp() {
        chatter = new Chatter();
    }

    @Test
    void givenNoChannels_printsNoAvailableChannels() {
        final Component format = chatter.formatted();
        assertThat(toText(format)).contains("No joined channels!");
    }

    @Test
    void givenTwoChannels_listsChannels() {
        chatter.join(new Channel("one"));
        chatter.join(new Channel("two"));
        final Component format = chatter.formatted();
        assertThat(toText(format)).contains("| one |", "| two |");
    }

    @Test
    void givenChannel_has_clickLink() {
        chatter.join(new Channel("test"));
        final Component format = chatter.formatted();
        assertThat(format.examine(StringExaminer.simpleEscaping())).contains("/schat channel join test");
    }

    @Test
    void givenMessages_listsMessages() {
        chatter.sendMessage(Message.message("Hi"));
        final Chatter source = new Chatter();
        source.setDisplayName(text("Player"));
        chatter.sendMessage(Message.message(source, "Hi"));
        final Component format = chatter.formatted();
        assertThat(toText(format)).containsSequence("Hi\n", "Player: Hi");
    }

    @Test
    void givenActiveChannel_underlinesChannel() {
        final Chatter chatter = new Chatter();
        chatter.setActiveChannel(new Channel("test"));
        final Component format = chatter.formatted();
        assertThat(MiniMessage.miniMessage().serialize(format)).contains("<underlined>", "</underlined>");
    }

    @NotNull
    private String toText(final Component format) {
        return PlainTextComponentSerializer.plainText().serialize(format);
    }
}
