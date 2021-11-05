package net.silthus.chat.integrations.vault;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.chat.Chat;
import net.silthus.chat.ChatSource;
import net.silthus.chat.TestBase;
import net.silthus.chat.targets.Chatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        Component prefix = provider.getPrefix(Chatter.of(player));
        assertThat(prefix).isEqualTo(Component.text("[ADMIN]").color(NamedTextColor.GRAY));
    }

    @Test
    void getPrefix_nullReturns_emptyComponent() {
        Component prefix = provider.getPrefix(Chatter.of(server.addPlayer()));
        assertThat(prefix).isEqualTo(Component.empty());
    }

    @Test
    void getPrefix_noPlayer_returnsEmpty() {
        assertThat(provider.getPrefix(ChatSource.nil())).isEqualTo(Component.empty());
    }

    @Test
    void getPrefix_noVault_returnsEmpty() {
        provider = new VaultProvider();
        assertThat(provider.getPrefix(Chatter.of(server.addPlayer()))).isEqualTo(Component.empty());
    }

    @Test
    void getSuffix_returnsLegacyParsedPlayerPrefix() {
        PlayerMock player = server.addPlayer();
        when(chat.getPlayerSuffix(player)).thenReturn("&7");

        Component prefix = provider.getSuffix(Chatter.of(player));
        assertThat(prefix).isEqualTo(Component.text().color(NamedTextColor.GRAY).build());
    }

    @Test
    void getSuffix_nullReturns_emptyComponent() {
        Component prefix = provider.getSuffix(Chatter.of(server.addPlayer()));
        assertThat(prefix).isEqualTo(Component.empty());
    }

    @Test
    void getSuffix_noPlayer_returnsEmpty() {
        assertThat(provider.getSuffix(ChatSource.nil())).isEqualTo(Component.empty());
    }

    @Test
    void getSuffix_noVault_returnsEmpty() {
        provider = new VaultProvider();
        assertThat(provider.getSuffix(Chatter.of(server.addPlayer()))).isEqualTo(Component.empty());
    }
}