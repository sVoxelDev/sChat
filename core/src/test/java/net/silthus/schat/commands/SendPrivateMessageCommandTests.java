package net.silthus.schat.commands;

import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterMock;
import net.silthus.schat.message.Message;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.GLOBAL;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.message.Message.message;
import static org.assertj.core.api.Assertions.assertThat;

@Nested
class SendPrivateMessageCommandTests {
    private ChannelRepository repository;
    private @NotNull ChatterMock source;
    private @NotNull ChatterMock target;

    @BeforeEach
    void setUp() {
        repository = createInMemoryChannelRepository();
        SendPrivateMessageCommand.prototype(builder -> builder.channelRepository(repository));
        source = randomChatter();
        target = randomChatter();
    }

    private Message send(Message.Draft draft) {
        return draft.send();
    }

    private Message sendPrivateMessage() {
        return sendPrivateMessageFrom(source);
    }

    private Message sendPrivateMessageFrom(Chatter source) {
        return send(message().source(source).to(target));
    }

    private String sourceId() {
        return idOf(source);
    }

    private String idOf(Chatter chatter) {
        return chatter.uniqueId().toString();
    }

    private String targetId() {
        return target.uniqueId().toString();
    }

    private Component sourceName() {
        return source.displayName();
    }

    private Component targetName() {
        return target.displayName();
    }

    private Channel targetChannel() {
        return target.channel(sourceId()).orElseThrow();
    }

    private Channel sourceChannel() {
        return source.channel(targetId()).orElseThrow();
    }

    @Test
    void creates_channel_with_partner_name() {
        sendPrivateMessage();
        source.assertJoinedChannel(targetId(), targetName());
        target.assertJoinedChannel(sourceId(), sourceName());
    }

    @Test
    void private_channels_are_linked() {
        sendPrivateMessage();
        assertThat(sourceChannel().targets()).contains(targetChannel());
        assertThat(targetChannel().targets()).contains(sourceChannel());
    }

    @Test
    void private_channels_are_global() {
        sendPrivateMessage();
        assertThat(sourceChannel().is(GLOBAL)).isTrue();
    }

    @Test
    void private_channels_have_private_setting() {
        sendPrivateMessage();
        assertThat(sourceChannel().is(Channel.PRIVATE)).isTrue();
    }

    @Test
    void target_receives_message() {
        final Message message = sendPrivateMessage();
        target.assertReceivedMessage(message);
    }

    @Test
    void private_channels_are_added_to_repository() {
        sendPrivateMessage();
        Assertions.assertThat(repository.all()).contains(sourceChannel(), targetChannel());
    }

    @Nested
    class given_private_channel_exists {
        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = Channel.channel(targetId()).create();
            repository.add(channel);
        }

        @Test
        void channel_is_reused() {
            final Message message = sendPrivateMessage();
            assertThat(channel).isSameAs(sourceChannel());
            assertThat(channel.messages()).contains(message);
        }
    }

    @Nested
    class given_two_different_source_chatters {
        private @NotNull ChatterMock source2;

        @BeforeEach
        void setUp() {
            source2 = randomChatter();
        }

        @Test
        void target_receives_message_in_separate_channels() {
            sendPrivateMessageFrom(source);
            sendPrivateMessageFrom(source2);
            assertThat(target.channel(idOf(source))).isPresent();
            assertThat(target.channel(idOf(source2))).isPresent();
        }
    }
}
