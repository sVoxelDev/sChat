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
import net.silthus.chat.Constants;
import net.silthus.chat.identities.Chatter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
public class PlayerData {

    @Getter
    @Accessors(fluent = true)
    private static final PlayerData.DataType type = new DataType();

    private UUID activeConversation;

    public PlayerData(Chatter chatter) {
        this.activeConversation = chatter.getActiveConversation().getUniqueId();
    }

    public void saveTo(@NonNull Player player) {
        player.getPersistentDataContainer().set(Constants.Persistence.PLAYER_DATA, PlayerData.type(), this);
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
