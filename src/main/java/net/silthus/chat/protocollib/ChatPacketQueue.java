package net.silthus.chat.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.silthus.chat.*;
import net.silthus.chat.layout.TabbedChatLayout;

import java.lang.reflect.Field;
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

    // TODO: cleanup
    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.CHAT) return;

        WrapperPlayServerChat packet = new WrapperPlayServerChat(event.getPacket());
        WrappedChatComponent chat = packet.getMessage();
        Component messageText;
        if (chat == null) {
            try {
                Object handle = event.getPacket().getHandle();
                Class<?> packetClass = handle.getClass();
                Field field = packetClass.getDeclaredField("adventure$message");
                field.setAccessible(true);
                messageText = (Component) field.get(handle);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
        } else {
            messageText = GsonComponentSerializer.gson().deserialize(chat.getJson());
        }

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

        if (chat == null) {
            try {
                Object handle = event.getPacket().getHandle();
                Class<?> packetClass = handle.getClass();
                Field field = packetClass.getDeclaredField("adventure$message");
                field.setAccessible(true);
                field.set(handle, render);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            String json = GsonComponentSerializer.gson().serialize(render);
            chat.setJson(json);
            packet.setMessage(WrappedChatComponent.fromJson(json));
            event.setPacket(packet.getHandle());
        }
    }
}
