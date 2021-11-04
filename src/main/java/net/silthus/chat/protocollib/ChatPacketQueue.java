package net.silthus.chat.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.silthus.chat.*;
import net.silthus.chat.layout.TabbedChatLayout;

import java.util.*;

public class ChatPacketQueue extends PacketAdapter {

    private final Map<UUID, Message> queuedMessages = Collections.synchronizedMap(new HashMap<>());

    public ChatPacketQueue(SChat plugin) {
        super(plugin, PacketType.Play.Server.CHAT);
        this.plugin = plugin;
    }

    public Collection<Message> getQueuedMessages() {
        return List.copyOf(queuedMessages.values());
    }

    public UUID queueMessage(Message message) {
        this.queuedMessages.put(message.getId(), message);
        return message.getId();
    }

    public Message getMessage(UUID id) {
        return queuedMessages.get(id);
    }

    public boolean hasMessage(String id) {
        if (id == null) return false;
        try {
            return queuedMessages.containsKey(UUID.fromString(id));
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    public Message removeMessage(String id) {
        if (!hasMessage(id)) return null;
        return queuedMessages.remove(UUID.fromString(id));
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.CHAT) return;

        PacketContainer packet = event.getPacket();
        WrappedChatComponent chat = packet.getChatComponents().read(0);
        Component messageText = GsonComponentSerializer.gson().deserialize(chat.getJson());

        Message message = messageText.children().stream()
                .filter(component -> component instanceof StorageNBTComponent)
                .map(component -> (StorageNBTComponent) component)
                .filter(component -> component.storage().equals(Constants.NBT_MESSAGE_ID))
                .map(NBTComponent::nbtPath)
                .map(this::removeMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(Message.message().text(messageText).format(Format.noFormat()).build());

        Chatter chatter = Chatter.of(event.getPlayer());

        Component render;
        if (chatter.getActiveChannel() != null) {
            ArrayList<Message> messages = new ArrayList<>(chatter.getActiveChannel().getReceivedMessages());
            messages.add(message);
            render = TabbedChatLayout.TABBED.render(chatter, messages);
        } else {
            render = TabbedChatLayout.TABBED.render(chatter, message);
        }

        String json = GsonComponentSerializer.gson().serialize(render);
        chat.setJson(json);
        packet.getChatComponents().write(0, chat);
        event.setPacket(packet);
    }
}
