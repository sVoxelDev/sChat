package net.silthus.schat.velocity.protocolize;

import com.velocitypowered.proxy.protocol.packet.Chat;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Messenger;
import net.silthus.schat.ui.View;
import net.silthus.schat.ui.ViewProvider;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.message.Message.message;

public final class ChatPacketListener extends AbstractPacketListener<Chat> {

    private static final @NotNull GsonComponentSerializer SERIALIZER = GsonComponentSerializer.gson();

    private final ChatterProvider chatterProvider;
    private final ViewProvider viewProvider;
    private final Messenger messenger;

    public ChatPacketListener(ChatterProvider chatterProvider, ViewProvider viewProvider, Messenger messenger) {
        super(Chat.class, Direction.DOWNSTREAM, 0);
        this.chatterProvider = chatterProvider;
        this.viewProvider = viewProvider;
        this.messenger = messenger;
    }

    // packet from server to proxy
    @Override
    public void packetReceive(PacketReceiveEvent<Chat> event) {
        final String message = event.packet().getMessage();
        if (message == null)
            return;

        final Chatter chatter = chatterProvider.get(event.player().uniqueId());
        final View view = viewProvider.getView(chatter);
        final Component packet = SERIALIZER.deserialize(message);
        if (view.isRenderedView(packet))
            return;

        message(packet).type(Message.Type.SYSTEM).send(messenger);
        event.cancelled(true);
    }

    // packet from proxy to server
    @Override
    public void packetSend(PacketSendEvent<Chat> packetSendEvent) {
    }
}
