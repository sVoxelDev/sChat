package net.silthus.chat;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.stream.Stream;

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
}
