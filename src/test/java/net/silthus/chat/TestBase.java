package net.silthus.chat;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class TestBase {

    protected ServerMock server;
    protected SChat plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(SChat.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    protected Stream<Listener> getRegisteredListeners() {
        return HandlerList.getRegisteredListeners(plugin).stream()
                .map(RegisteredListener::getListener);
    }

    protected void assertReceivedNoMessage(PlayerMock player) {
        assertThat(player.nextMessage()).isNull();
    }

    protected void assertReceivedMessage(PlayerMock player, String message) {
        assertThat(player.nextMessage()).isEqualTo(message);
    }
}
