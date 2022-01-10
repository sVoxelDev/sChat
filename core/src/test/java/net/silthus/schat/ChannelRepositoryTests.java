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

    @Nested class given_a_new_channel_repository {
        private ChannelRepository repository;

        @BeforeEach
        void setUp() {
            repository = ChannelRepository.createInMemoryChannelRepository();
        }

        @Test
        void then_it_is_empty() {
            assertThat(repository.all()).isEmpty();
        }

        @Nested class when_one_channel_is_added {
            private Channel channel;

            @BeforeEach
            void setUp() {
                channel = randomChannel();
                repository.add(channel);
            }

            @Test
            void then_the_channel_is_stored_in_the_repository() {
                assertThat(repository.all()).contains(channel);
                assertThat(repository.contains(channel.getKey())).isTrue();
            }

            @Nested class when_a_channel_with_the_same_key_is_added {

                private Channel channel2;

                @BeforeEach
                void setUp() {
                    channel2 = Channel.createChannel(channel.getKey());
                }

                @Test
                void then_add_throws_duplicate_channel_exception() {
                    assertThatExceptionOfType(ChannelRepository.DuplicateChannel.class)
                        .isThrownBy(() -> repository.add(channel2));
                }
            }
        }
    }
}
