package net.silthus.chat.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.silthus.chat.Chatter;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import net.silthus.chat.layout.TabbedChatLayout;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatPacketListener extends PacketAdapter {

    private final SChat plugin;
    @Getter
    private final Map<UUID, Message> queuedMessages = Collections.synchronizedMap(new HashMap<>());

    public ChatPacketListener(SChat plugin) {
        super(plugin, PacketType.Play.Server.CHAT);
        this.plugin = plugin;
    }

    public UUID addMessage(Message message) {
        UUID id = UUID.randomUUID();
        this.queuedMessages.put(id, message);
        return id;
    }

    public Message getMessage(UUID id) {
        return queuedMessages.get(id);
    }

    public boolean hasMessage(String id) {
        return queuedMessages.containsKey(UUID.fromString(id));
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.CHAT) return;

        Chatter chatter = Chatter.of(event.getPlayer());
        if (chatter.getActiveChannel() == null) return;

        PacketContainer packet = event.getPacket();
        WrappedChatComponent chat = packet.getChatComponents().read(0);

        Component render = TabbedChatLayout.TABBED.render(chatter, chatter.getActiveChannel().getReceivedMessages());
        String json = GsonComponentSerializer.gson().serialize(render);

        chat.setJson(json);
        packet.getChatComponents().write(0, chat);
        event.setPacket(packet);
    }
}
