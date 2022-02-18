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
package net.silthus.schat.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import java.util.function.Supplier;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BukkitTests {
    protected static ServerMock server;
    protected static MockPlugin mockPlugin;
    protected static BukkitAudiences audiences;

    protected BukkitLoader plugin;

    @BeforeAll
    static void beforeAll() {
        server = MockBukkit.mock(new CustomServerMock());
        mockPlugin = MockBukkit.createMockPlugin();
    }

    @AfterAll
    static void afterAll() {
        MockBukkit.unmock();
    }

    @BeforeEach
    void setup() {
        audiences = BukkitAudiences.create(plugin != null ? plugin : mockPlugin);
    }

    @AfterEach
    void teardown() {
        audiences.close();
    }

    protected void loadSChatPlugin() {
        plugin = MockBukkit.load(BukkitLoader.class);
    }

    protected SChatBukkitServer sChat() {
        return plugin.bootstrap().plugin();
    }

    @SuppressWarnings("SameParameterValue")
    protected void assertLastMessageIs(ConsoleCommandSenderMock mock, String message) {
        assertTrimmedEquals(getLastMessage(mock::nextMessage), message);
    }

    @SuppressWarnings("SameParameterValue")
    protected void assertLastMessageIs(PlayerMock player, String message) {
        assertTrimmedEquals(getLastMessage(player::nextMessage), message);
    }

    protected void assertLastMessageContains(PlayerMock player, String text) {
        assertTrimmedContains(getLastMessage(player::nextMessage), text);
    }

    @Nullable
    private String getLastMessage(Supplier<String> nextMessageSupplier) {
        String nextMessage;
        String lastMessage = null;
        while ((nextMessage = nextMessageSupplier.get()) != null) {
            lastMessage = nextMessage;
        }
        return lastMessage;
    }

    private void assertTrimmedEquals(String lastMessage, String message) {
        if (isEmpty(lastMessage, message))
            return;
        assertThat(lastMessage.trim()).isEqualTo(message.trim());
    }

    private void assertTrimmedContains(String lastMessage, String text) {
        if (isEmpty(lastMessage, text))
            return;
        assertThat(lastMessage.trim()).contains(text.trim());
    }

    private boolean isEmpty(String lastMessage, String message) {
        if (lastMessage == null && message == null)
            return true;
        assertThat(lastMessage).isNotNull();
        return false;
    }
}
