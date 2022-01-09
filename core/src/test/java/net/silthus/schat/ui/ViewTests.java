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

import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.user.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.silthus.schat.ChannelHelper.channelWith;
import static net.silthus.schat.MessageHelper.randomMessage;
import static net.silthus.schat.TestHelper.assertNPE;
import static net.silthus.schat.UserHelper.randomUser;
import static net.silthus.schat.channel.Channel.PRIORITY;
import static net.silthus.schat.channel.Channel.createChannel;
import static net.silthus.schat.ui.ViewConfig.viewConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ViewTests {

    private static final @NotNull MiniMessage COMPONENT_SERIALIZER = MiniMessage.miniMessage();
    private User user;
    private View view;

    @BeforeEach
    void setUp() {
        user = randomUser();
        view = new View(user);
    }

    @NotNull
    private String text(Message message) {
        return COMPONENT_SERIALIZER.serialize(message.getText());
    }

    @SneakyThrows
    @NotNull
    private Message addMessage(Message message) {
        user.sendMessage(message);
        Thread.sleep(1L); // required to order messages by time
        return message;
    }

    private void addMessage(String text) {
        addMessage(Message.message(text).create());
    }

    private void addMessageWithSource(String source, String text) {
        addMessage(Message.message(text).source(Identity.identity(source)).create());
    }

    private void assertViewRenders(String expected) {
        assertEquals(expected, COMPONENT_SERIALIZER.serialize(view.render()));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void createGivenNullChatter_throws() {
        assertNPE(() -> new View(null));
    }

    @Test
    void givenNoMessagesAndNoChannels_renders_empty() {
        assertViewRenders("");
    }

    @Test
    void givenSingleMessage_rendersMessage() {
        final Message message = addMessage(randomMessage());
        assertViewRenders(text(message));
    }

    @Test
    void givenSingleMessageWithSource_rendersSourceAndText() {
        addMessageWithSource("Bob", "Hi");
        assertViewRenders("Bob: Hi");
    }

    @Test
    void givenTwoMessages_rendersBoth() {
        addMessage("Hey");
        addMessageWithSource("Silthus", "Yo");
        assertViewRenders("""
            Hey
            Silthus: Yo"""
        );
    }

    @Test
    void givenSingleChannel_rendersChannel() {
        user.addChannel(createChannel("test"));
        assertViewRenders("| test |");
    }

    @Test
    void givenTwoChannels_rendersChannel() {
        user.addChannel(createChannel("one"));
        user.addChannel(createChannel("two"));
        assertViewRenders("| one | two |");
    }

    @Test
    void givenTwoChannelsWithDifferentPriority_rendersHigherPriorityChannelFirst() {
        user.addChannel(channelWith("zzz", PRIORITY, 1));
        user.addChannel(createChannel("test"));
        assertViewRenders("| zzz | test |");
    }

    @Test
    void givenActiveChannel_underlinesChannel() {
        user.setActiveChannel(createChannel("test"));
        assertViewRenders("| <underlined>test</underlined> |");
    }

    @Test
    void givenDifferentActiveChannelFormat_usesFormat() {
        view = new View(user, viewConfig().activeChannelFormat(component -> ViewConfig.DEFAULT_ACTIVE_CHANNEL_FORMAT.format(component).color(GREEN)).create());
        user.setActiveChannel(createChannel("test"));
        assertViewRenders("| <green><underlined>test</underlined></green> |");
    }

    @Test
    void givenDifferentMessageSourceFormat_usesFormat() {
        view = new View(user, viewConfig().messageSourceFormat(component -> Component.text("<").append(component).append(Component.text("> "))).create());
        addMessageWithSource("Bob", "hey");
        assertViewRenders("<Bob> hey");
    }

    @Test
    void givenDifferentChannelJoinConfig_usesConfig() {
        view = new View(user, viewConfig().channelJoinConfig(JoinConfiguration.builder().separator(Component.text(" - ")).build()).create());
        user.addChannel(createChannel("foo"));
        user.addChannel(createChannel("bar"));
        assertViewRenders("bar - foo");
    }
}
