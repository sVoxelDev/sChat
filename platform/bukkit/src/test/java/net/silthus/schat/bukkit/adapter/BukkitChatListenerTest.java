package net.silthus.schat.bukkit.adapter;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.silthus.schat.bukkit.BukkitTests;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.message.Message;
import net.silthus.schat.platform.locale.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.bukkit.adapter.BukkitIdentityAdapter.identity;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.chatter.ChatterProviderStub.chatterProviderStub;
import static net.silthus.schat.message.Messenger.defaultMessenger;
import static org.assertj.core.api.Assertions.assertThat;

class BukkitChatListenerTest extends BukkitTests {

    private BukkitChatListener listener;
    private Chatter chatter;
    private PlayerMock player;
    private Component lastMessage;

    @BeforeEach
    void setUp() {
        player = server.addPlayer();
        chatter = Chatter.chatter(identity(player)).messageHandler(this::handleMessage).create();
        listener = (BukkitChatListener) new BukkitChatListener()
            .chatterProvider(chatterProviderStub(chatter))
            .messenger(defaultMessenger());
        server.getPluginManager().registerEvents(listener, mockPlugin);
    }

    private void handleMessage(Message message, Chatter.MessageHandler.Context context) {
        lastMessage = message.text();
    }

    private void chat() {
        player.chat("Hi");
    }

    @Nested class given_no_active_channel {
        @BeforeEach
        void setUp() {
            chatter.setActiveChannel(null);
        }

        @SneakyThrows
        @Test
        void when_player_chats_sends_error_to_player() {
            chat();
            Thread.sleep(100L); // workaround for async events until this PR is merged: https://github.com/MockBukkit/MockBukkit/pull/297
            assertThat(lastMessage).isEqualTo(Messages.CANNOT_CHAT_NO_ACTIVE_CHANNEL.build());
        }
    }

    @Nested class given_active_channel {
        @BeforeEach
        void setUp() {
            chatter.setActiveChannel(randomChannel());
        }
    }
}
