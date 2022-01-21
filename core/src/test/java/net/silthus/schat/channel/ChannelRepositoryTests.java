/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.channel;

import net.silthus.schat.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ChannelRepositoryTests {

    @Nested class given_an_empty_channel_repository {
        private ChannelRepository repository;

        @BeforeEach
        void setUp() {
            repository = ChannelRepository.createInMemoryChannelRepository();
        }

        @Test
        void then_all_returns_empty_list() {
            assertThat(repository.all()).isEmpty();
        }

        @Test
        void then_get_throws_ChannelNotFound() {
            assertThatExceptionOfType(Repository.NotFound.class).isThrownBy(() -> repository.get("foobar"));
        }

        @Nested class given_one_channel_is_added {
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

            @Test
            void then_get_returns_the_channel_by_key() {
                assertThat(repository.get(channel.getKey())).isNotNull();
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
