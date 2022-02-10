package net.silthus.schat.messenger;

import java.lang.reflect.Type;
import lombok.NonNull;

final class EmptyMessenger implements Messenger {
    @Override
    public void registerMessageType(Type type) {

    }

    @Override
    public void sendPluginMessage(@NonNull PluginMessage pluginMessage) throws UnsupportedMessageException {

    }
}
