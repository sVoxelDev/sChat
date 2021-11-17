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

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.chat.Chat;
import net.silthus.chat.ChatSource;
import net.silthus.chat.Chatter;
import net.silthus.chat.TestBase;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("deprecation")
class VaultProviderTest extends TestBase {

    private VaultProvider provider;
    private Chat chat;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        chat = mock(Chat.class);
        provider = new VaultProvider(chat);
    }

    @Test
    void getPrefix_returnsLegacyParsedPlayerPrefix() {
        PlayerMock player = server.addPlayer();
        when(chat.getPlayerPrefix(player)).thenReturn("&7[ADMIN]");

        Component prefix = provider.getPrefix(Chatter.player(player));
        assertThat(prefix).isEqualTo(Component.text("[ADMIN]").color(NamedTextColor.GRAY));
    }

    @Test
    void getPrefix_nullReturns_emptyComponent() {
        Component prefix = provider.getPrefix(Chatter.player(server.addPlayer()));
        assertThat(prefix).isEqualTo(Component.empty());
    }

    @Test
    void getPrefix_noPlayer_returnsEmpty() {
        assertThat(provider.getPrefix(ChatSource.nil())).isEqualTo(Component.empty());
    }

    @Test
    void getPrefix_noVault_returnsEmpty() {
        provider = new VaultProvider();
        assertThat(provider.getPrefix(Chatter.player(server.addPlayer()))).isEqualTo(Component.empty());
    }

    @Test
    void getSuffix_returnsLegacyParsedPlayerPrefix() {
        PlayerMock player = server.addPlayer();
        when(chat.getPlayerSuffix(player)).thenReturn("&7");

        Component prefix = provider.getSuffix(Chatter.player(player));
        assertThat(prefix).isEqualTo(Component.text().color(NamedTextColor.GRAY).build());
    }

    @Test
    void getSuffix_nullReturns_emptyComponent() {
        Component prefix = provider.getSuffix(Chatter.player(server.addPlayer()));
        assertThat(prefix).isEqualTo(Component.empty());
    }

    @Test
    void getSuffix_noPlayer_returnsEmpty() {
        assertThat(provider.getSuffix(ChatSource.nil())).isEqualTo(Component.empty());
    }

    @Test
    void getSuffix_noVault_returnsEmpty() {
        provider = new VaultProvider();
        assertThat(provider.getSuffix(Chatter.player(server.addPlayer()))).isEqualTo(Component.empty());
    }

    @Test
    void offline_usesOfflinePlayerToGetPrefix() {
        when(chat.getPlayerPrefix(anyString(), anyString())).thenReturn("[PLAYER]");
        Component prefix = provider.getPrefix(ChatSource.player(Bukkit.getOfflinePlayer(UUID.randomUUID())));
        assertThat(toText(prefix)).isEqualTo("[PLAYER]");
    }

    @Test
    void offline_usesOfflinePlayerToGetSuffix() {
        when(chat.getPlayerSuffix(anyString(), anyString())).thenReturn("[!]");
        Component suffix = provider.getSuffix(ChatSource.player(Bukkit.getOfflinePlayer(UUID.randomUUID())));
        assertThat(toText(suffix)).isEqualTo("[!]");
    }
}