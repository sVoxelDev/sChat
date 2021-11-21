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

package net.silthus.chat.conversations;

import net.silthus.chat.Chatter;
import net.silthus.chat.Formats;
import net.silthus.chat.TestBase;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.config.PluginConfig;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.*;

public class ChannelRegistryTests extends TestBase {

    private ChannelRegistry registry;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        registry = plugin.getChannelRegistry();
    }

    @Test
    void isRegisteredAndLoaded_onEnable() {

        assertThat(plugin.getChannelRegistry().getChannels())
                .isNotEmpty();
    }

    @Test
    void create_emptyChannels() {
        assertThat(registry).isNotNull();
        assertThat(registry.getChannels()).isNotEmpty();
    }

    @Test
    void getChannels_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> registry.getChannels().add(Channel.createChannel("test")));
    }

    @Test
    void add_addsChannelToRegistry() {
        Channel channel = createChannel("foo", config -> config
                .name("Test")
                .format(Formats.defaultFormat()));
        registry.register(channel);

        assertThat(registry.getChannels())
                .contains(channel)
                .extracting(
                        Channel::getName,
                        c -> toText(c.getDisplayName())
                ).contains(tuple(
                        "foo",
                        "Test"
                ));
    }

    @Test
    void add_addsChannelWithLowercase() {
        registry.clear();
        addChannel("FOO");
        assertThat(registry.getChannels())
                .first()
                .extracting(Channel::getName)
                .isEqualTo("foo");
    }

    @Test
    void remove_removesChannelFromRegistry() {
        Channel channel = Channel.createChannel("test").register();
        assertThat(registry.getChannels()).contains(channel);

        registry.remove(channel);
        assertThat(registry.getChannels()).doesNotContain(channel);
    }

    @Test
    void remove_withIdentifier_returnsChannel() {
        Channel test = Channel.createChannel("test");
        registry.register(test);
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
    void remove_withLowerCase_Identifier() {
        Channel channel = addChannel("test");
        registry.remove("TEST");
        assertThat(registry.getChannels()).doesNotContain(channel);

        channel = addChannel("test");
        registry.remove(createChannel("TesT"));
        assertThat(registry.getChannels()).doesNotContain(channel);
    }

    @Test
    void remove_null_doesNotThrow() {

        assertThat(registry.remove((String) null)).isNull();
    }

    @Test
    void contains_returnsTrueIfChannelExists() {

        addChannel("test");

        assertThat(registry.contains("test")).isTrue();
    }

    @Test
    void contains_withLowerCase_returnsTrue() {

        addChannel("test");

        assertThat(registry.contains("tEsT")).isTrue();
    }

    @Test
    void contains_returnsFalseIfChannelIsNotPresent() {
        assertThat(registry.contains("foobar")).isFalse();
    }

    @Test
    void contains_null_returnsFalse() {
        assertThat(registry.contains((String) null)).isFalse();
    }

    @Test
    void contains_withChannel_returnsTrue() {
        Channel channel = Channel.createChannel("test");
        assertThat(registry.contains(channel)).isFalse();
        registry.register(channel);
        assertThat(registry.contains(channel)).isTrue();
    }

    @Test
    void size_returnsChannelRegistrySize() {
        registry.clear();
        assertThat(registry.size()).isZero();
        addChannel("test");
        assertThat(registry.size()).isOne();
    }

    @Test
    void iterator_iteratesOverAllChannels() {
        registry.clear();
        Iterator<Channel> iterator = registry.iterator();
        assertThat(iterator).isNotNull();
        assertThat(iterator.hasNext()).isFalse();
        addChannel("test");
        assertThat(iterator.hasNext()).isFalse();

        assertThat(registry.iterator().hasNext()).isTrue();
    }

    @Test
    void getChannel_returnsEmpty() {
        assertThat(registry.find("foo")).isEmpty();
    }

    @Test
    void getChannel_findsChannelByIdentifier() {
        addChannel("foo");
        assertThat(registry.find("foo"))
                .isPresent().get()
                .extracting(
                        Channel::getName,
                        Channel::getDisplayName
                ).contains(
                        "foo",
                        "foo"
                );
    }

    @Test
    void getChannel_withUpperCase_findsChannel() {

        addChannel("foo");
        assertThat(registry.find("FoO"))
                .isPresent().get()
                .extracting(Channel::getName)
                .isEqualTo("foo");
    }

    @Test
    void getChannel_withName_findsChannel() {
        Channel channel = createChannel("test", config -> config.name("MyChannel"));
        registry.register(channel);

        assertThat(registry.find("MyChannel"))
                .isPresent().get()
                .isEqualTo(channel);
    }

    @Test
    void getChannel_withNullIdentifier_returnsEmpty() {
        assertThat(registry.find(null))
                .isEmpty();
    }

    @Test
    void findChannel_withId_returnsChannel() {
        Channel channel = addChannel("test123");
        assertThat(registry.find(channel.getUniqueId().toString()))
                .isPresent().get()
                .isEqualTo(channel);
    }

    @Test
    void getChannel_byId_returnsNull_ifNotFound() {
        Channel channel = registry.get(UUID.randomUUID());
        assertThat(channel).isNull();
    }

    @Test
    void getChannel_byId_returnsChannel() {
        Channel channel = addChannel("test");
        assertThat(registry.get(channel.getUniqueId()))
                .isNotNull()
                .isEqualTo(channel);
    }

    private Channel addChannel(String identifier) {
        return createChannel(identifier);
    }

    @Test
    void clear_unsubscribesAllFromChannel() {
        final Channel foobar = createChannel("foobar");
        registry.register(foobar);
        final Chatter chatter = Chatter.player(server.addPlayer());
        chatter.subscribe(foobar);

        assertThat(chatter.getConversations()).contains(foobar);
        registry.clear();
        assertThat(chatter.getConversations()).doesNotContain(foobar);
    }

    @Test
    void remove_closesChannel() {
        final Channel channel = createChannel("abc");
        registry.register(channel);
        final Chatter chatter = Chatter.player(server.addPlayer());
        channel.subscribe(channel);

        registry.remove(channel);
        assertThat(chatter.getConversations()).doesNotContain(channel);
        assertThat(channel.getTargets()).doesNotContain(chatter);
    }

    @Test
    void isNotRegistered_byDefault() {
        final Channel channel = Channel.createChannel("test");
        assertThat(registry.getChannels()).doesNotContain(channel);
    }

    @Test
    void register_registersChannelInRegistry() {
        final Channel channel = Channel.createChannel("foobar");
        channel.register();
        assertThat(registry.getChannels()).contains(channel);
        assertThat(channel).isSameAs(registry.get("foobar"));
    }

    @Test
    void register_onlyReplacesConfig_ifExisting() {
        final Channel channel = Channel.createChannel("test");
        channel.register();
        final Channel newChannel = Channel.createChannel("test", ChannelConfig.builder().name("Foo").build()).register();
        assertThat(registry.getChannels()).contains(newChannel);
        assertThat(newChannel).isSameAs(channel);
        assertThat(newChannel.getDisplayName()).isEqualTo(text("Foo"));
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

        @Test
        void load_registersSystemChannel_ifConfigured() {
            MemoryConfiguration cfg = new MemoryConfiguration();
            PluginConfig config = PluginConfig.config(cfg);

            registry.load(config);
        }

        @Test
        void load_reloadsConfigOfExistingChannels() {
            registry.load(channelConfigBefore());
            assertThat(registry.contains("test1")).isTrue();
            assertThat(registry.contains("test2")).isTrue();

            registry.load(channelConfigAfter());
            assertThat(registry.get("test1"))
                    .extracting(Channel::getConfig)
                    .extracting(
                            ChannelConfig::name,
                            ChannelConfig::autoJoin
                    ).contains(
                            "Foobar",
                            false
                    );
            assertThat(registry.contains("test2")).isFalse();
        }

        @Test
        void load_doesNotCreateNewChannel_ifChannelExists() {
            registry.load(channelConfigBefore());
            Channel channel = registry.get("test1");
            Chatter chatter = Chatter.player(server.addPlayer());
            channel.addTarget(chatter);
            assertThat(channel.getTargets()).contains(chatter);

            registry.load(channelConfigAfter());
            channel = registry.get("test1");
            assertThat(channel.getTargets()).contains(chatter);
        }

        private void loadTwoChannels() {
            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("channels.test1.name", "Test 1");
            cfg.set("channels.test2.name", "Test 2");
            registry.load(PluginConfig.config(cfg));
        }

        private void loadFromEmptyChannelConfig() {
            registry.load(PluginConfig.config(new MemoryConfiguration()));
        }

        private PluginConfig channelConfigBefore() {
            return PluginConfig.builder()
                    .channel("test1", ChannelConfig.builder().name("Test 1").autoJoin(true).build())
                    .channel("test2", ChannelConfig.builder().name("Test 2").format(Formats.noFormat()).build())
                    .build();
        }

        private PluginConfig channelConfigAfter() {
            return PluginConfig.builder()
                    .channel("test1", ChannelConfig.builder().name("Foobar").autoJoin(false).build())
                    .build();
        }
    }
}
