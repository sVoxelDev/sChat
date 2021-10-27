package net.silthus.chat.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silthus.chat.Channel;
import net.silthus.chat.SChat;

import java.util.Optional;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

public class ChatPacketListener extends PacketAdapter {

    private final SChat plugin;

    public ChatPacketListener(SChat plugin) {
        super(plugin, PacketType.Play.Server.CHAT);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.CHAT) return;
        PacketContainer packet = event.getPacket();
        WrappedChatComponent chat = packet.getChatComponents().read(0);
        String json = chat.getJson();
        TextComponent.Builder text = text();
        for (int i = 0; i < 100; i++) {
            text.append(newline());
        }
        GsonComponentSerializer gson = GsonComponentSerializer.gson();
        Component originalMessage = gson.deserialize(json);
        for (Component child : originalMessage.children()) {
            if (child instanceof StorageNBTComponent storage) {
                String data = storage.nbtPath();
                Key key = storage.storage();
                if (key.asString().equals("schat:channel")) {
                    Optional<Channel> channel = plugin.getChannelManager().getChannel(data);
                    channel.ifPresent(c -> {
                        text.append(c.getReceivedMessages().stream().map(message -> {
                            TextComponent deserialize = LegacyComponentSerializer.legacySection().deserialize(message.formattedMessage());
                            return Component.text().append(deserialize).append(newline()).build();
                        }).collect(Collectors.toList()));
                    });
                }
            }
        }
        text.append(originalMessage);
        text.append(newline()).append(text("| ").color(DARK_AQUA));
        for (Channel channel : plugin.getChannelManager().getChannels()) {
            text.append(text(channel.getName()).color(GREEN).clickEvent(clickEvent(ClickEvent.Action.RUN_COMMAND, "/schat join " + channel.getAlias())))
                    .append(text(" | ").color(DARK_AQUA));
        }
        TextComponent message = text.build();
        chat.setJson(gson.serialize(message));
        packet.getChatComponents().write(0, chat);
        event.setPacket(packet);
    }
}
