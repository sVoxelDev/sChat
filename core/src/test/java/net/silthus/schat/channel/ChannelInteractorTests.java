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

import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterProvider;
import net.silthus.schat.repository.Repository;
import net.silthus.schat.usecases.JoinChannel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.channel.ChannelAssertions.assertChannelHasNoTargets;
import static net.silthus.schat.channel.ChannelAssertions.assertChannelHasOnlyTarget;
import static net.silthus.schat.channel.ChannelAssertions.assertChannelHasTarget;
import static net.silthus.schat.channel.ChannelHelper.randomChannel;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterAssertions.assertChatterHasChannel;
import static net.silthus.schat.chatter.ChatterAssertions.assertChatterHasNoChannels;
import static net.silthus.schat.chatter.ChatterAssertions.assertChatterHasOnlyChannel;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.chatter.ChatterProviderStub.chatterProviderStub;
import static net.silthus.schat.policies.FailedCanJoinStub.stubCanJoinFailure;
import static net.silthus.schat.policies.SuccessfulCanJoinStub.stubCanJoinSuccess;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ChannelInteractorTests {
    private final ChannelRepository channelRepository = createInMemoryChannelRepository();
    private final JoinChannel.Out joinChannelOut = mock(JoinChannel.Out.class);

    private ChatterProvider chatterProvider;
    private ChannelInteractorImpl interactor;
    private Chatter chatter;
    private Channel channel;

    @BeforeEach
    void setUp() {
        chatter = randomChatter();
        channel = addChannel(randomChannel());
        chatterProvider = chatterProviderStub(chatter);

        interactor = new ChannelInteractorImpl()
            .chatterProvider(chatterProvider)
            .channelRepository(channelRepository)
            .canJoinChannel(stubCanJoinSuccess())
            .joinChannelOut(joinChannelOut);
    }

    private Channel addChannel(@NotNull Channel channel) {
        channelRepository.add(channel);
        return channel;
    }

    private void joinChannel() {
        joinChannel(channel.getKey());
    }

    private void joinChannel(String channelId) {
        interactor.joinChannel(chatter.getKey(), channelId);
    }

    private void canJoin(boolean canJoin) {
        if (canJoin)
            interactor.canJoinChannel(stubCanJoinSuccess());
        else
            interactor.canJoinChannel(stubCanJoinFailure());
    }

    private void assertJoinChannelError() {
        assertThatExceptionOfType(JoinChannel.Error.class)
            .isThrownBy(ChannelInteractorTests.this::joinChannel);
    }

    @Test
    void given_interactor_without_presenter_then_presenter_is_not_null() {
        assertThat(new ChannelInteractorImpl().joinChannelOut()).isNotNull();
    }

    @Test
    void given_interactor_without_can_join_check_then_can_join_is_always_true() {
        final ChannelInteractorImpl interactor = new ChannelInteractorImpl();
        assertThat(interactor.canJoinChannel()).isNotNull();
        assertThat(interactor.canJoinChannel().canJoinChannel(chatter, channel)).isTrue();
    }

    @Nested class joinChannel {

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_chatter_id_throws_npe() {
            assertNPE(() -> interactor.joinChannel((UUID) null, null));
        }

        @Test
        void given_null_channel_throws_npe() {
            assertNPE(() -> joinChannel(null));
        }

        @Test
        void given_unknown_channel_throws_not_found() {
            assertThatExceptionOfType(Repository.NotFound.class)
                .isThrownBy(() -> joinChannel("test"));
        }

        @Nested class given_valid_chatter_and_channel {

            @Nested class given_successful_can_join_check {
                @BeforeEach
                void setUp() {
                    canJoin(true);
                }

                @Test
                void then_chatter_is_added_as_target_to_channel() {
                    joinChannel();
                    assertChannelHasTarget(channel, chatter);
                }

                @Test
                void then_channel_is_added_to_chatter() {
                    joinChannel();
                    assertChatterHasChannel(chatter, channel);
                }

                @Test
                void then_presenter_is_called() {
                    joinChannel();
                    verify(joinChannelOut).joinedChannel(new JoinChannel.Result(chatter, channel));
                }

                @Nested class given_already_joined {
                    @BeforeEach
                    void setUp() {
                        joinChannel();
                    }

                    @Test
                    void then_only_joins_once() {
                        joinChannel();
                        assertChannelHasOnlyTarget(channel, chatter);
                        assertChatterHasOnlyChannel(chatter, channel);
                    }
                }
            }

            @Nested class given_failed_can_join_check {
                @BeforeEach
                void setUp() {
                    canJoin(false);
                }

                @Test
                void then_throws_access_defined_exception() {
                    assertJoinChannelError();
                }

                @Nested class given_already_joined {
                    @BeforeEach
                    void setUp() {
                        chatter.join(channel);
                    }

                    @Test
                    void then_removes_chatter_as_channel_target() {
                        assertJoinChannelError();
                        assertChannelHasNoTargets(channel);
                    }

                    @Test
                    void then_removes_channel_from_chatter() {
                        assertJoinChannelError();
                        assertChatterHasNoChannels(chatter);
                    }
                }
            }
        }
    }

    @Nested class setActiveChannel {
        private void setActiveChannel(String channelId) {
            interactor.setActiveChannel(chatter.getKey(), channelId);
        }

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_chatter_throws_npe() {
            assertNPE(() -> interactor.setActiveChannel((UUID) null, null));
        }

        @Test
        void given_null_channel_throws_npe() {
            assertNPE(() -> setActiveChannel(null));
        }

        @Test
        void given_unknown_channel_throws_not_found() {
            assertThatExceptionOfType(Repository.NotFound.class)
                .isThrownBy(() -> setActiveChannel("test"));
        }

        @Nested class given_valid_channel {
            private void setActiveChannel() {
                interactor.setActiveChannel(chatter.getKey(), channel.getKey());
            }

            @Test
            void then_chatter_has_active_channel() {
                setActiveChannel();
                assertThat(chatter.isActiveChannel(channel)).isTrue();
                assertThat(chatter.getActiveChannel()).isPresent().get().isEqualTo(channel);
            }

            @Nested class given_chatter_has_not_joined_channel {
                @BeforeEach
                void setUp() {
                    interactor = new SpyingChannelInteractor()
                        .chatterProvider(chatterProvider)
                        .channelRepository(channelRepository);
                }

                @Test
                void then_joins_chatter() {
                    setActiveChannel();
                    assertThat(((SpyingChannelInteractor) interactor).isJoinChannelCalled()).isTrue();
                }
            }

            @Nested class given_join_fails {
                @BeforeEach
                void setUp() {
                    canJoin(false);
                }

                @Test
                void then_channel_is_not_set_active() {
                    assertJoinChannelError();
                    assertThat(chatter.getActiveChannel()).isNotPresent();
                }
            }
        }
    }
}
