/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.silthus.schat.bukkit.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.JsonParseException;
import java.io.Serial;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.message.Message;
import org.bukkit.plugin.Plugin;

import static net.silthus.schat.message.Message.message;

/**
 * Handles the player chat packet flow and rewrites non sChat packets into sChat packets.
 *
 * @since 1.0.0
 */
@Log(topic = "sChat")
public final class ChatPacketListener extends PacketAdapter {

    private static final Key MESSAGE_MARKER_KEY = Key.key("schat", "message");
    public static final Component MESSAGE_MARKER = Component.storageNBT(MESSAGE_MARKER_KEY.asString(), MESSAGE_MARKER_KEY);

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

    private final ProtocolManager protocolManager;
    private final ChatterRepository chatterRepository;

    public ChatPacketListener(final Plugin plugin, ChatterRepository chatterRepository) {
        super(plugin, PacketType.Play.Server.CHAT);
        this.chatterRepository = chatterRepository;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /**
     * Enables the ProtocolLib integration.
     *
     * @since 1.0.0
     */
    public void enable() {
        try {
            this.protocolManager.addPacketListener(this);
            log.info("Enabled ProtocolLib Integration.");
        } catch (Exception e) {
            log.severe("Failed to enable ProtocolLib Integration!");
            e.printStackTrace();
        }
    }

    @Override
    public void onPacketSending(final PacketEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.CHAT)) {
            return;
        }

        final Component rawMessage = messageFromPacket(event);
        if (rawMessage == null)
            return;

        final Chatter chatter = chatterRepository.get(event.getPlayer().getUniqueId());
        if (isMarkedMessage(rawMessage))
            return;

        message(rawMessage).to(chatter).type(Message.Type.SYSTEM).send();
        event.setCancelled(true);
    }

    private boolean isMarkedMessage(Component message) {
        return message.contains(MESSAGE_MARKER) || message.children().contains(MESSAGE_MARKER);
    }

    @SneakyThrows
    private Component messageFromPacket(final @NonNull PacketEvent event) {
        final WrapperPlayServerChat wrapper = new WrapperPlayServerChat(event.getPacket());

        return switch (MessageType.fromChatPacket(wrapper)) {
            case LEGACY -> fromLegacyMessage(wrapper);
            case BUNGEE -> fromBungeeMessagePacket(wrapper);
            case PAPERMC -> fromAdventureMessagePacket(wrapper);
            default -> throw new HandleChatPacket("Unable to read message from chat packet.");
        };
    }

    private Component fromLegacyMessage(final WrapperPlayServerChat packet) {
        return GsonComponentSerializer.gson().deserialize(packet.message().getJson());
    }

    private Component fromBungeeMessagePacket(final WrapperPlayServerChat packet) {
        try {
            final Object handle = packet.handle().getHandle();
            final Field field = handle.getClass().getDeclaredField("components");
            field.setAccessible(true);
            final BaseComponent[] baseComponents = (BaseComponent[]) field.get(handle);
            if (baseComponents == null)
                return null;
            return BungeeComponentSerializer.get().deserialize(baseComponents);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.severe("Unable to extract Bungee BaseComponents from chat packet: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Component fromAdventureMessagePacket(final WrapperPlayServerChat packet) {
        if (PAPER_SERIALIZE == null)
            return null;
        try {
            final Object handle = packet.handle().getHandle();
            final Class<?> packetClass = handle.getClass();
            final Field field = packetClass.getDeclaredField("adventure$message");
            field.setAccessible(true);
            final Object data = field.get(handle);
            if (data == null)
                return null;
            final String json = (String) PAPER_SERIALIZE.invoke(PAPER_GSON_SERIALIZER, data);
            return GsonComponentSerializer.gson().deserialize(json);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | JsonParseException e) {
            log.severe("Unable to extract PaperMC message from chat packet: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private enum MessageType {
        LEGACY,
        BUNGEE,
        PAPERMC,
        UNKNOWN;

        static MessageType fromChatPacket(final @NonNull WrapperPlayServerChat packet) {
            if (isLegacyMessage(packet))
                return LEGACY;
            if (isBungeeMessage(packet.handle().getHandle()))
                return BUNGEE;
            if (isAdventureMessage(packet.handle().getHandle()))
                return PAPERMC;
            return UNKNOWN;
        }

        private static boolean isLegacyMessage(final @NonNull WrapperPlayServerChat wrapper) {
            return wrapper.message() != null;
        }

        private static boolean isBungeeMessage(final @NonNull Object packet) {
            return containsField(packet, "components");
        }

        private static boolean isAdventureMessage(final @NonNull Object packet) {
            return containsField(packet, "adventure$message");
        }

        private static boolean containsField(final Object object, final String fieldName) {
            for (final Field field : object.getClass().getDeclaredFields()) {
                if (field.getName().equals(fieldName))
                    return true;
            }
            return false;
        }
    }

    /**
     * The exception is thrown if the handling of a chat packet failed.
     *
     * @since 1.0.0
     */
    public static final class HandleChatPacket extends RuntimeException {
        @Serial private static final long serialVersionUID = -8061095814474497204L;

        HandleChatPacket(final String message) {
            super(message);
        }
    }
}
