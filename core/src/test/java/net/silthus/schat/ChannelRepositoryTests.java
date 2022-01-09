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

package net.silthus.schat;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.ChannelHelper.randomChannel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChannelRepositoryTests {

    private ChannelRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ChannelRepository();
    }

    @Nested class given_new_repository {
        @Test
        void it_is_empty() {
            assertThat(repository.getAll()).isEmpty();
        }
    }

    @Nested class given_one_added_channel {

        private Channel channel;

        @BeforeEach
        void setUp() {
            channel = randomChannel();
            repository.add(channel);
        }

        @Test
        void the_channel_is_stored_in_the_repository() {
            assertThat(repository.getAll()).contains(channel);
            assertThat(repository.contains(channel.getKey())).isTrue();
        }

        @Nested class and_channel_with_same_key_is_added {

            private Channel channel2;

            @BeforeEach
            void setUp() {
                channel2 = Channel.createChannel(channel.getKey());
            }

            @Test
            void add_throws_duplicate_channel_exception() {
                assertThatExceptionOfType(ChannelRepository.DuplicateChannel.class)
                    .isThrownBy(() -> repository.add(channel2));
            }
        }
    }
}
