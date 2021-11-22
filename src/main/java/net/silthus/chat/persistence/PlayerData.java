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

package net.silthus.chat.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.silthus.chat.Chatter;
import net.silthus.chat.Conversation;
import net.silthus.chat.SChat;
import net.silthus.chat.identities.PlayerChatter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

import static net.silthus.chat.Constants.Persistence.PLAYER_DATA;

@Getter
@Accessors(fluent = true)
public class PlayerData {

    @Getter
    @Accessors(fluent = true)
    private static final PlayerData.DataType type = new DataType();

    public static void save(@NonNull PlayerChatter chatter) {
        final PlayerData data = new PlayerData(chatter);
        chatter.getPlayer().ifPresent(data::saveTo);
    }

    public static void load(@NonNull final PlayerChatter chatter) {
        final SChat plugin = SChat.instance();
        final Optional<PlayerData> data = chatter.getPlayer()
                .flatMap(PlayerData::load);
        data.filter(playerData -> playerData.activeConversationId != null)
                .filter(playerData -> playerData.activeConversationName != null)
                .map(playerData -> {
                    final Conversation conversation = plugin.getConversationManager().getConversation(playerData.activeConversationId);
                    if (conversation != null) return conversation;
                    return plugin.getChannelRegistry().get(playerData.activeConversationName);
                }).ifPresent(chatter::setActiveConversation);
        data.ifPresent(playerData -> chatter.setDisplayName(GsonComponentSerializer.gson().deserialize(playerData.displayName())));
    }

    private static Optional<PlayerData> load(Player player) {
        return Optional.ofNullable(player.getPersistentDataContainer().get(PLAYER_DATA, type()));
    }

    private final UUID activeConversationId;
    private final String activeConversationName;
    private final String displayName;

    public PlayerData(Chatter chatter) {
        if (chatter.getActiveConversation() != null) {
            this.activeConversationId = chatter.getActiveConversation().getUniqueId();
            this.activeConversationName = chatter.getActiveConversation().getName();
        } else {
            this.activeConversationId = null;
            this.activeConversationName = null;
        }
        this.displayName = GsonComponentSerializer.gson().serialize(chatter.getDisplayName());
    }

    private void saveTo(@NonNull Player player) {
        player.getPersistentDataContainer().set(PLAYER_DATA, type(), this);
    }

    public static class DataType implements PersistentDataType<String, PlayerData> {

        private final Gson gson = new GsonBuilder().create();

        @Override
        public @NotNull Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public @NotNull Class<PlayerData> getComplexType() {
            return PlayerData.class;
        }

        @Override
        public @NotNull String toPrimitive(@NotNull PlayerData complex, @NotNull PersistentDataAdapterContext context) {
            return gson.toJson(complex);
        }

        @Override
        public @NotNull PlayerData fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
            return gson.fromJson(primitive, PlayerData.class);
        }
    }
}
