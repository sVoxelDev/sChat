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

package net.silthus.schat.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.sender.Sender;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.message.Message.message;
import static net.silthus.schat.ui.Renderer.TABBED_CHANNELS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ViewTests {

    private static final String CHANNEL_KEY = "test";
    private static final String MESSAGE = "Hi";
    private static final String SOURCE = "Player";

    private Chatter chatter;
    private View view;

    @BeforeEach
    void setUp() {
        chatter = Chatter.createChatter();
        view = View.chatterView(mock(Sender.class), chatter, TABBED_CHANNELS);
    }

    private void addChannel(String channel) {
        chatter.join(createChannel(channel));
    }

    private void setActiveChannel() {
        chatter.setActiveChannel(createChannel(CHANNEL_KEY));
    }

    private void sendMessage() {
        sendMessage(MESSAGE);
    }

    private void sendMessage(String text) {
        chatter.sendMessage(message(text));
    }

    private void sendMessageWithSource() {
        final Chatter chatterSource = Chatter.chatter(Identity.identity(SOURCE)).create();
        this.chatter.sendMessage(message(chatterSource, MESSAGE));
    }

    private Component format() {
        return view.render();
    }

    @NotNull
    private String serialize(final Component format) {
        return MiniMessage.miniMessage().serialize(format);
    }

    private void assertRenderContains(String... expected) {
        assertThat(serialize(format())).contains(expected);
    }

    private void assertRenderDoesNotContain(String... expected) {
        assertThat(serialize(format())).doesNotContain(expected);
    }

    @Test
    void givenNoChannels_printsNoAvailableChannels() {
        assertRenderContains("No joined channels!");
    }

    @Test
    void givenTwoChannels_listsChannels() {
        addChannel("one");
        addChannel("two");

        assertRenderContains("one", "two");
    }

    @Test
    void givenChannel_has_clickLink() {
        addChannel("test");

        assertRenderContains("/channel join test");
    }

    @Test
    void givenMessages_listsMessages() {
        sendMessage();
        sendMessageWithSource();

        assertRenderContains("Hi\n", "Player: Hi");
    }

    @Test
    void givenActiveChannel_underlinesChannel() {
        setActiveChannel();

        assertRenderContains("<underlined>", "</underlined>");
    }

    @Test
    void renders_blank_lines() {
        assertRenderContains("\n\n\n\n\n\n\n\n\n\n\n");
    }

    @Test
    void given_more_then_100_messages_renders_last_100() throws InterruptedException {
        sendMessage("one");
        Thread.sleep(1L);
        for (int i = 0; i < 100; i++) {
            sendMessage();
        }
        assertRenderDoesNotContain("one");
    }

    @Test
    void render_contains_marker() {
        final Component render = view.render();
        assertThat(render.contains(View.MESSAGE_MARKER)).isTrue();
    }

    @Test
    @Disabled
    void given_multiple_channels_only_displays_messages_from_active() {
        final Channel passive = createChannel("one");
        chatter.join(passive);
        final Channel active = createChannel("active");
        chatter.setActiveChannel(active);

        passive.sendMessage(message("Hidden"));
        active.sendMessage(message("Visible"));

        assertRenderContains("Visible");
        assertRenderDoesNotContain("Hidden");
    }
}
