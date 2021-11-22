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
import com.google.gson.JsonParseException;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.silthus.chat.Chatter;
import net.silthus.chat.Constants;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Log(topic = Constants.PLUGIN_NAME)
public class ChatPacketQueue extends PacketAdapter {

    private static Object PAPER_GSON_SERIALIZER;
    private static Method PAPER_SERIALIZE;

    static {
        try {
            PAPER_GSON_SERIALIZER = Class.forName("io.papermc.paper.text.PaperComponents").getDeclaredMethod("gsonSerializer").invoke(null);
            PAPER_SERIALIZE = Arrays.stream(PAPER_GSON_SERIALIZER.getClass().getDeclaredMethods())
                    .filter(method -> method.getName().equals("serialize"))
                    .findFirst()
                    .orElse(null);
            if (PAPER_SERIALIZE != null)
                PAPER_SERIALIZE.setAccessible(true);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            log.warning("Unable to find PaperMC chat components. You can ignore this if you are not running PaperMC or a fork.");
        }
    }

    public ChatPacketQueue(SChat plugin) {
        super(plugin, PacketType.Play.Server.CHAT);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.CHAT) return;

        Component message = getMessageFromPacket(event);
        if (isIgnoredMessage(message)) return;

        Chatter chatter = Chatter.player(event.getPlayer());
        Message.message(message).to(chatter).send();
        event.setCancelled(true);
    }

    private boolean isIgnoredMessage(Component messageText) {
        if (messageText == null) return true;
        return messageText.children().stream()
                .filter(component -> component instanceof StorageNBTComponent)
                .map(component -> (StorageNBTComponent) component)
                .anyMatch(component -> component.storage().equals(Constants.NBT_IS_SCHAT_MESSAGE));
    }

    @SneakyThrows
    private Component getMessageFromPacket(PacketEvent event) {
        WrapperPlayServerChat wrapper = new WrapperPlayServerChat(event.getPacket());
        if (wrapper.getMessage() != null)
            return getLegacyMessage(wrapper.getMessage());
        Object packet = wrapper.getHandle().getHandle();
        if (containsField(packet, "adventure$message"))
            return getPaperMessage(packet);
        if (containsField(packet, "components"))
            return getBungeeMessage(packet);
        throw new HandleChatPacketException("Unable to read message from chat packet.");
    }

    private Component getLegacyMessage(WrappedChatComponent chat) {
        return GsonComponentSerializer.gson().deserialize(chat.getJson());
    }

    private Component getPaperMessage(Object packet) {
        if (PAPER_SERIALIZE == null) return null;
        try {
            Class<?> packetClass = packet.getClass();
            Field field = packetClass.getDeclaredField("adventure$message");
            field.setAccessible(true);
            Object data = field.get(packet);
            if (data == null) return null;
            String json = (String) PAPER_SERIALIZE.invoke(PAPER_GSON_SERIALIZER, data);
            return GsonComponentSerializer.gson().deserialize(json);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | JsonParseException e) {
            log.severe("Unable to extract PaperMC message from chat packet: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Component getBungeeMessage(Object packet) {
        try {
            Field field = packet.getClass().getDeclaredField("components");
            field.setAccessible(true);
            BaseComponent[] baseComponents = (BaseComponent[]) field.get(packet);
            if (baseComponents == null) return null;
            return BungeeComponentSerializer.get().deserialize(baseComponents);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.severe("Unable to extract Bungee BaseComponents from chat packet: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean containsField(Object object, String fieldName) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .anyMatch(f -> f.getName().equals(fieldName));
    }
}
