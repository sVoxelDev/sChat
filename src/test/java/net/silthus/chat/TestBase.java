package net.silthus.chat;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.protocollib.ChatPacketQueue;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class TestBase {

    protected ServerMock server;
    protected SChat plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(SChat.class);
        plugin.setChatPacketQueue(new ChatPacketQueue(plugin));
    }

    @AfterEach
    @SneakyThrows
    public void tearDown() {
        Bukkit.getScheduler().cancelTasks(plugin);
        MockBukkit.unmock();
    }

    protected Stream<Listener> getRegisteredListeners() {
        return HandlerList.getRegisteredListeners(plugin).stream()
                .map(RegisteredListener::getListener);
    }

    protected void assertReceivedNoMessage(PlayerMock player) {
        assertThat(player.nextMessage()).isNull();
    }

    protected void assertReceivedMessage(PlayerMock player, String message) {
        assertThat(player.nextMessage()).isEqualTo(message);
    }

    protected Channel createChannel(Function<ChannelConfig, ChannelConfig> cfg) {
        return createChannel("test", cfg);
    }

    protected Channel createChannel(String identifier, Function<ChannelConfig, ChannelConfig> cfg) {
        return cfg.apply(ChannelConfig.defaults()).toChannel(identifier);
    }

    protected String toText(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    protected String toText(Message message) {
        return toText(message.formatted());
    }

    protected Message randomMessage() {
        return Message.message(RandomString.make() + "-" + Instant.now()).build();
    }

    @SneakyThrows
    protected Collection<Message> randomMessages(int count) {
        ArrayList<Message> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(randomMessage());
            Thread.sleep(10L);
        }
        Collections.shuffle(list);
        return list;
    }

}
