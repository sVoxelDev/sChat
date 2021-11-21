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

import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import net.silthus.chat.*;
import net.silthus.chat.config.ConsoleConfig;
import org.bukkit.ChatColor;
import org.bukkit.event.server.ServerCommandEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.chat.config.ConsoleConfig.consoleDefaults;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ConsoleTests {

    @AfterEach
    void tearDown() {
        Console.instance = null;
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void instance_beforeOnEnable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(Console::console);
    }

    @Nested
    class AfterOnEnable extends TestBase {

        private Console console;

        @Override
        @BeforeEach
        public void setUp() {
            super.setUp();

            console = Console.console();
        }

        @Test
        void init_twice_throws() {
            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> Console.init(consoleDefaults()));
        }

        @Test
        void create() {
            ChatTarget target = ChatTarget.console();
            assertThat(target).isNotNull()
                    .isInstanceOf(ChatTarget.class)
                    .isInstanceOf(ChatSource.class)
                    .extracting(ChatTarget::getName)
                    .isEqualTo(Constants.Targets.CONSOLE);

            assertThat(Console.console()).isSameAs(target);
        }

        @Test
        void onEnable_registersConsoleListener() {
            assertThat(getRegisteredListeners()).contains(console);
        }

        @Test
        void sendMessage_sendsMessageToConsole() {
            Message message = Message.message("Hi").to(console).send();

            assertThat(cleaned(((ConsoleCommandSenderMock) server.getConsoleSender()).nextMessage()))
                    .isNotNull()
                    .isEqualTo("Hi");
            assertThat(console.getLastReceivedMessage())
                    .isNotNull()
                    .isEqualTo(message);
        }

        @Test
        void onChat_sendsMessageToDefaultTarget() {
            console.onConsoleChat(new ServerCommandEvent(server.getConsoleSender(), "Hi there!"));
            assertThat(console.getLastReceivedMessage())
                    .isNotNull()
                    .extracting(Message::getText)
                    .isEqualTo(text("Hi there!"));
            assertThat(ChatColor.stripColor(((ConsoleCommandSenderMock) server.getConsoleSender()).nextMessage()))
                    .isNotNull()
                    .contains("[Global]Console: Hi there!");
            assertThat(createChannel("global").getLastReceivedMessage())
                    .isNotNull()
                    .extracting(Message::getSource)
                    .isEqualTo(Console.console());
        }

        @Test
        void onChat_doesNotSendCommandsAsMessage() {
            console.onConsoleChat(new ServerCommandEvent(server.getConsoleSender(), "/tell hi"));

            assertThat(console.getLastReceivedMessage())
                    .isNull();
            assertThat(((ConsoleCommandSenderMock) server.getConsoleSender()).nextMessage())
                    .isNull();
        }

        @Test
        void setConfig_changesTheName() {
            assertThat(console.getDisplayName()).isEqualTo(text("Console"));

            console.setConfig(ConsoleConfig.builder().name(text("Foobar")).build());
            assertThat(console.getDisplayName()).isEqualTo(text("Foobar"));
        }

        @Test
        void setConfig_changesDefaultChannel() {
            console.setConfig(ConsoleConfig.builder().defaultChannel("none").build());
            console.onConsoleChat(new ServerCommandEvent(server.getConsoleSender(), "Hi!"));

            assertThat(console.getLastReceivedMessage()).isNull();
            assertThat(ChatColor.stripColor(((ConsoleCommandSenderMock) server.getConsoleSender()).nextMessage()))
                    .isNull();
        }
    }
}
