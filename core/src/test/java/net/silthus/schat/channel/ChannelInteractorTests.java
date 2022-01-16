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

package net.silthus.schat.channel;

import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
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
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static net.silthus.schat.policies.FailedCanJoinStub.stubCanJoinFailure;
import static net.silthus.schat.policies.SuccessfulCanJoinStub.stubCanJoinSuccess;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ChannelInteractorTests {
    private final ChannelRepository channelRepository = createInMemoryChannelRepository();
    private final ChatterRepository chatterRepository = createInMemoryChatterRepository();
    private final JoinChannel.Presenter joinChannelPresenter = mock(JoinChannel.Presenter.class);

    private ChannelInteractor interactor;
    private Chatter chatter;
    private Channel channel;

    @BeforeEach
    void setUp() {
        interactor = new ChannelInteractor()
            .chatterRepository(chatterRepository)
            .channelRepository(channelRepository)
            .canJoinChannel(stubCanJoinSuccess())
            .joinChannelPresenter(joinChannelPresenter);

        chatter = addChatter(randomChatter());
        channel = addChannel(randomChannel());
    }

    private Chatter addChatter(@NotNull Chatter chatter) {
        chatterRepository.add(chatter);
        return chatter;
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

    @Nested class joinChannel {

        @Test
        @SuppressWarnings("ConstantConditions")
        void given_null_chatter_id_throws_npe() {
            final ChannelInteractor channelInteractor = new ChannelInteractor();
            assertNPE(() -> channelInteractor.joinChannel(null, null));
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
                    verify(joinChannelPresenter).joinedChannel(new JoinChannel.Result(chatter.getIdentity(), channel.getKey(), channel.getDisplayName()));
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

                private void assertJoinChannelError() {
                    assertThatExceptionOfType(JoinChannel.Error.class)
                        .isThrownBy(ChannelInteractorTests.this::joinChannel);
                }

                @Test
                void then_throws_access_defined_exception() {
                    assertJoinChannelError();
                }

                @Nested class given_already_joined {
                    @BeforeEach
                    void setUp() {
                        chatter.addChannel(channel);
                        channel.addTarget(chatter);
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
}
