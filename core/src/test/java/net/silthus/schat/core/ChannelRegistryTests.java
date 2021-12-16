package net.silthus.schat.core;

import net.silthus.schat.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChannelRegistryTests {

    private static final String TEST_CHANNEL_ALIAS = "test";
    private ChannelRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ChannelRegistry();
    }

    private Channel createChannel() {
        return registry.create(TEST_CHANNEL_ALIAS);
    }

    @Test
    void all_isEmpty_byDefault() {
        assertThat(registry.all()).isEmpty();
    }

    @Test
    void create_createsNewChannel() {
        assertThat(createChannel()).isNotNull();
    }

    @Test
    void create_addsChannelToRegistry() {
        final Channel channel = createChannel();
        assertThat(registry.all()).contains(channel);
    }

    @Test
    void create_sameAlias_throws() {
        createChannel();
        assertThatExceptionOfType(ChannelRegistry.DuplicateAlias.class)
            .isThrownBy(this::createChannel);
    }

    @Test
    void create_ignoresCase() {
        createChannel();
        assertThatExceptionOfType(ChannelRegistry.DuplicateAlias.class)
            .isThrownBy(() -> registry.create(TEST_CHANNEL_ALIAS.toUpperCase()));
    }
}
