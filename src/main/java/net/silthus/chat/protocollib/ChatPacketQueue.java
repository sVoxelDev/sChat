package net.silthus.chat.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.extern.java.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.silthus.chat.Chatter;
import net.silthus.chat.Constants;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import net.silthus.chat.layout.TabbedChatLayout;

import java.lang.reflect.Field;
import java.util.*;

@Log(topic = Constants.PLUGIN_NAME)
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

    public Message getQueuedMessage(UUID id) {
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

        WrapperPlayServerChat packet = new WrapperPlayServerChat(event.getPacket());
        WrappedChatComponent chat = packet.getMessage();
        Component messageText = getPacketMessageText(event, chat);

        if (messageText == null) return;

        Optional<Message> optionalMessage = getQueuedMessage(messageText);

        Chatter chatter = Chatter.of(event.getPlayer());
        if (optionalMessage.isEmpty()) {
            chatter.sendMessage(Message.message(messageText).build());
            event.setCancelled(true);
            return;
        }

        Component render = renderMessage(chatter, optionalMessage.get());
        if (chat == null) {
            setPaperMessage(event, render);
        } else {
            setLegacyMessage(event, packet, render);
        }
    }

    private Optional<Message> getQueuedMessage(Component messageText) {
        return messageText.children().stream()
                .filter(component -> component instanceof StorageNBTComponent)
                .map(component -> (StorageNBTComponent) component)
                .filter(component -> component.storage().equals(Constants.NBT_MESSAGE_ID))
                .map(NBTComponent::nbtPath)
                .map(this::removeMessage)
                .filter(Objects::nonNull)
                .findFirst();
    }

    private Component renderMessage(Chatter chatter, Message message) {
        if (chatter.getActiveChannel() == null)
            return renderSingleMessage(chatter, message);
        return renderChannelMessages(chatter, message);
    }

    private Component renderSingleMessage(Chatter chatter, Message message) {
        return TabbedChatLayout.TABBED.render(chatter, message);
    }

    private Component renderChannelMessages(Chatter chatter, Message message) {
        Component render;
        ArrayList<Message> messages = new ArrayList<>(chatter.getActiveChannel().getReceivedMessages());
        messages.add(message);
        render = TabbedChatLayout.TABBED.render(chatter, messages);
        return render;
    }

    private Component getPacketMessageText(PacketEvent event, WrappedChatComponent chat) {
        Component messageText;
        if (chat == null) {
            messageText = getPaperMessage(event);
        } else {
            messageText = getLegacyMessage(chat);
        }
        return messageText;
    }

    private void setLegacyMessage(PacketEvent event, WrapperPlayServerChat packet, Component render) {
        String json = GsonComponentSerializer.gson().serialize(render);
        packet.setMessage(WrappedChatComponent.fromJson(json));
        event.setPacket(packet.getHandle());
    }

    private void setPaperMessage(PacketEvent event, Component render) {
        try {
            Object handle = event.getPacket().getHandle();
            Class<?> packetClass = handle.getClass();
            Field field = packetClass.getDeclaredField("adventure$message");
            field.setAccessible(true);
            field.set(handle, render);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Component getLegacyMessage(WrappedChatComponent chat) {
        return GsonComponentSerializer.gson().deserialize(chat.getJson());
    }

    private Component getPaperMessage(PacketEvent event) {
        try {
            Object handle = event.getPacket().getHandle();
            Class<?> packetClass = handle.getClass();
            Field field = packetClass.getDeclaredField("adventure$message");
            field.setAccessible(true);
            return (Component) field.get(handle);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.severe("Unable to extract PaperMC message from chat packet: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
