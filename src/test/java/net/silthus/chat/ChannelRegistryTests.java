package net.silthus.chat;

import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.config.PluginConfig;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.*;

public class ChannelRegistryTests extends TestBase {

    private ChannelRegistry registry;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        registry = new ChannelRegistry(plugin);
    }

    @Test
    void isRegisteredAndLoaded_onEnable() {

        assertThat(plugin.getChannelRegistry().getChannels())
                .isNotEmpty();
    }

    @Test
    void create_emptyChannels() {
        assertThat(registry)
                .extracting("plugin")
                .isNotNull();
        assertThat(registry.getChannels()).isEmpty();
    }

    @Test
    void getChannels_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> registry.getChannels().add(new Channel("test")));
    }

    @Test
    void add_addsChannelToRegistry() {
        Channel channel = new Channel("foo", ChannelConfig.builder()
                .name("Test")
                .format(Format.defaultFormat()).build());
        registry.add(channel);

        assertThat(registry.getChannels())
                .contains(channel)
                .extracting(
                        Channel::getIdentifier,
                        Channel::getName
                ).contains(tuple(
                        "foo",
                        "Test"
                ));
    }

    @Test
    void remove_removesChannelFromRegistry() {
        Channel channel = new Channel("test");
        registry.add(channel);
        assertThat(registry.size()).isOne();

        assertThat(registry.remove(channel)).isTrue();
        assertThat(registry.size()).isZero();
    }

    @Test
    void remove_returnsFalse_ifChannelNotRegistered() {
        assertThat(registry.remove(new Channel("test"))).isFalse();
    }

    @Test
    void remove_withIdentifier_returnsChannel() {
        Channel test = new Channel("test");
        registry.add(test);
        Channel channel = registry.remove("test");
        assertThat(channel)
                .isNotNull()
                .isEqualTo(test);
    }

    @Test
    void remove_withIdentifier_noChannel_returnsNull() {
        Channel channel = registry.remove("test");
        assertThat(channel).isNull();
    }

    @Test
    void contains_returnsTrueIfChannelExists() {

        registry.add(new Channel("test"));

        assertThat(registry.contains("test")).isTrue();
    }

    @Test
    void contains_returnsFalseIfChannelIsNotPresent() {
        assertThat(registry.contains("foobar")).isFalse();
    }

    @Test
    void contains_withChannel_returnsTrue() {
        Channel channel = new Channel("test");
        assertThat(registry.contains(channel)).isFalse();
        registry.add(channel);
        assertThat(registry.contains(channel)).isTrue();
    }

    @Test
    void size_returnsChannelRegistrySize() {
        assertThat(registry.size()).isZero();
        registry.add(new Channel("test"));
        assertThat(registry.size()).isOne();
    }

    @Test
    void iterator_iteratesOverAllChannels() {
        Iterator<Channel> iterator = registry.iterator();
        assertThat(iterator).isNotNull();
        assertThat(iterator.hasNext()).isFalse();
        registry.add(new Channel("test"));
        assertThat(iterator.hasNext()).isFalse();

        assertThat(registry.iterator().hasNext()).isTrue();
    }

    @Test
    void getChannel_returnsEmpty() {
        assertThat(registry.get("foo")).isEmpty();
    }

    @Test
    void getChannel_findsChannelByIdentifier() {
        registry.add(new Channel("foo"));
        assertThat(registry.get("foo"))
                .isPresent().get()
                .extracting(
                        Channel::getIdentifier,
                        Channel::getName
                ).contains(
                        "foo",
                        "foo"
                );
    }

    @Nested
    class Load {

        @Test
        void load_loadsChannelsFromConfig() {

            loadTwoChannels();

            assertThat(registry.getChannels())
                    .hasSize(2);
        }

        @Test
        void load_doesNotError_ifConfigSectionIsEmpty() {
            assertThatCode(this::loadFromEmptyChannelConfig)
                    .doesNotThrowAnyException();
            assertThat(registry.getChannels()).isEmpty();
        }

        @Test
        void load_clearsPreviousChannels() {
            loadTwoChannels();
            assertThat(registry.getChannels()).hasSize(2);

            loadFromEmptyChannelConfig();
            assertThat(registry.getChannels()).isEmpty();
        }

        private void loadTwoChannels() {
            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("channels.test1.name", "Test 1");
            cfg.set("channels.test2.name", "Test 2");
            registry.load(new PluginConfig(cfg));
        }

        private void loadFromEmptyChannelConfig() {
            registry.load(new PluginConfig(new MemoryConfiguration()));
        }
    }
}
