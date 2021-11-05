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

package net.silthus.chat;

import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.config.PluginConfig;
import net.silthus.chat.targets.Channel;
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
    @SuppressWarnings("ConstantConditions")
    void getChannels_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> registry.getChannels().add(ChatTarget.channel("test")));
    }

    @Test
    void add_addsChannelToRegistry() {
        Channel channel = Channel.channel("foo", ChannelConfig.defaults()
                .name("Test")
                .format(Format.defaultFormat()));
        registry.add(channel);

        assertThat(registry.getChannels())
                .contains(channel)
                .extracting(
                        Channel::getIdentifier,
                        c -> toText(c.getName())
                ).contains(tuple(
                        "foo",
                        "Test"
                ));
    }

    @Test
    void add_addsChannelWithLowercase() {
        addChannel("FOO");
        assertThat(registry.getChannels())
                .first()
                .extracting(Channel::getIdentifier)
                .isEqualTo("foo");
    }

    @Test
    void remove_removesChannelFromRegistry() {
        Channel channel = ChatTarget.channel("test");
        registry.add(channel);
        assertThat(registry.size()).isOne();

        assertThat(registry.remove(channel)).isTrue();
        assertThat(registry.size()).isZero();
    }

    @Test
    void remove_returnsFalse_ifChannelNotRegistered() {
        assertThat(registry.remove(ChatTarget.channel("test"))).isFalse();
    }

    @Test
    void remove_withIdentifier_returnsChannel() {
        Channel test = ChatTarget.channel("test");
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
    void remove_withLowerCase_Identifier() {
        addChannel("test");
        registry.remove("TEST");
        assertThat(registry.getChannels()).isEmpty();

        addChannel("test");
        registry.remove(ChatTarget.channel("TesT"));
        assertThat(registry.getChannels()).isEmpty();
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
        Channel channel = ChatTarget.channel("test");
        assertThat(registry.contains(channel)).isFalse();
        registry.add(channel);
        assertThat(registry.contains(channel)).isTrue();
    }

    @Test
    void size_returnsChannelRegistrySize() {
        assertThat(registry.size()).isZero();
        addChannel("test");
        assertThat(registry.size()).isOne();
    }

    @Test
    void iterator_iteratesOverAllChannels() {
        Iterator<Channel> iterator = registry.iterator();
        assertThat(iterator).isNotNull();
        assertThat(iterator.hasNext()).isFalse();
        addChannel("test");
        assertThat(iterator.hasNext()).isFalse();

        assertThat(registry.iterator().hasNext()).isTrue();
    }

    @Test
    void getChannel_returnsEmpty() {
        assertThat(registry.get("foo")).isEmpty();
    }

    @Test
    void getChannel_findsChannelByIdentifier() {
        addChannel("foo");
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

    @Test
    void getChannel_withUpperCase_findsChannel() {

        addChannel("foo");
        assertThat(registry.get("FoO"))
                .isPresent().get()
                .extracting(Channel::getIdentifier)
                .isEqualTo("foo");
    }

    @Test
    void getChannel_withNullIdentifier_returnsEmpty() {
        assertThat(registry.get(null))
                .isEmpty();
    }

    private void addChannel(String identifier) {
        registry.add(ChatTarget.channel(identifier));
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
            PluginConfig config = PluginConfig.fromConfig(cfg);

            registry.load(config);
        }

        private void loadTwoChannels() {
            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("channels.test1.name", "Test 1");
            cfg.set("channels.test2.name", "Test 2");
            registry.load(PluginConfig.fromConfig(cfg));
        }

        private void loadFromEmptyChannelConfig() {
            registry.load(PluginConfig.fromConfig(new MemoryConfiguration()));
        }
    }
}
