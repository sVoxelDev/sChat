/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.chat.integrations.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.extern.java.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.silthus.chat.Constants;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import net.silthus.chat.identities.Chatter;

import java.lang.reflect.Field;

@Log(topic = Constants.PLUGIN_NAME)
public class ChatPacketQueue extends PacketAdapter {

    public ChatPacketQueue(SChat plugin) {
        super(plugin, PacketType.Play.Server.CHAT);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.CHAT) return;

        WrapperPlayServerChat packet = new WrapperPlayServerChat(event.getPacket());
        WrappedChatComponent chat = packet.getMessage();
        Component messageText = getPacketMessageText(event, chat);

        if (isIgnoredMessage(messageText)) return;

        Chatter chatter = Chatter.of(event.getPlayer());
        Message.message(messageText).to(chatter).send();
    }

    private boolean isIgnoredMessage(Component messageText) {
        if (messageText == null) return true;
        return messageText.children().stream()
                .filter(component -> component instanceof StorageNBTComponent)
                .map(component -> (StorageNBTComponent) component)
                .anyMatch(component -> component.storage().equals(Constants.NBT_IS_SCHAT_MESSAGE));
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
