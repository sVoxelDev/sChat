package net.silthus.schat.bukkit;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.MockCommandMap;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.NotNull;

import static org.mockito.Mockito.mock;

public final class CustomServerMock extends ServerMock {

    @Override
    public @NotNull Messenger getMessenger() {
        return mock(Messenger.class);
    }

    @Override
    public @NotNull MockCommandMap getCommandMap() {
        return super.getCommandMap();
    }
}
