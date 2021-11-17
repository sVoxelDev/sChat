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

package net.silthus.chat.integrations.vault;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.chat.Chat;
import net.silthus.chat.Identity;
import net.silthus.chat.identities.PlayerChatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"deprecation", "ConstantConditions"})
public class VaultProvider {

    private final Chat chat;

    public VaultProvider(Chat chat) {
        this.chat = chat;
    }

    public VaultProvider() {
        this.chat = null;
    }

    public Component getPrefix(Identity identity) {
        if (isInvalidIdentity(identity))
            return Component.empty();

        Player player = Bukkit.getPlayer(identity.getUniqueId());
        if (player == null)
            return validateAndDeserialize(chat.getPlayerPrefix("world", identity.getName()));

        String prefix = chat.getPlayerPrefix(player);
        return validateAndDeserialize(prefix);
    }

    public Component getSuffix(Identity identity) {
        if (isInvalidIdentity(identity))
            return Component.empty();

        Player player = Bukkit.getPlayer(identity.getUniqueId());
        if (player == null)
            return validateAndDeserialize(chat.getPlayerSuffix("world", identity.getName()));

        String suffix = chat.getPlayerSuffix(player);
        return validateAndDeserialize(suffix);
    }

    @NotNull
    private Component validateAndDeserialize(String prefix) {
        if (prefix == null || prefix.isBlank())
            return Component.empty();

        return LegacyComponentSerializer.legacyAmpersand().deserialize(prefix);
    }

    private boolean isInvalidIdentity(Identity identity) {
        return chat == null || !(identity instanceof PlayerChatter);
    }
}
