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

package net.silthus.schat.bukkit.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.JsonParseException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.Messenger;
import net.silthus.schat.ui.View;
import net.silthus.schat.ui.ViewProvider;
import org.bukkit.plugin.Plugin;

import static net.silthus.schat.message.Message.message;

/**
 * Handles the player chat packet flow and rewrites non sChat packets into sChat packets.
 *
 * @since next
 */
@Log(topic = "sChat")
public final class ChatPacketListener extends PacketAdapter {

    private static final GsonComponentSerializer GSON_SERIALIZER = GsonComponentSerializer.gson();

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
    private final ChatterProvider chatterProvider;
    private final ViewProvider viewProvider;
    private final Messenger messenger;

    public ChatPacketListener(final Plugin plugin, ChatterProvider chatterProvider, ViewProvider viewProvider, Messenger messenger) {
        super(plugin, PacketType.Play.Server.CHAT);
        this.chatterProvider = chatterProvider;
        this.viewProvider = viewProvider;
        this.messenger = messenger;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /**
     * Enables the ProtocolLib integration.
     *
     * @since next
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
        if (event.getPacketType() != PacketType.Play.Server.CHAT)
            return;

        final Component rawMessage = messageFromPacket(event);
        if (rawMessage == null)
            return;

        final Chatter chatter = chatterProvider.get(event.getPlayer().getUniqueId());
        final View view = viewProvider.getView(chatter);
        if (view.isRenderedView(rawMessage))
            return;

        message(rawMessage).type(Message.Type.SYSTEM).send(messenger);
        event.setCancelled(true);
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
     * @since next
     */
    public static final class HandleChatPacket extends RuntimeException {

        HandleChatPacket(final String message) {
            super(message);
        }
    }
}
