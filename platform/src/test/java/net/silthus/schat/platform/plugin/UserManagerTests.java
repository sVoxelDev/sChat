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

package net.silthus.schat.platform.plugin;

import java.util.function.Function;
import net.bytebuddy.utility.RandomString;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.channel.ChannelPermissionProvider;
import net.silthus.schat.channel.ChannelRepository;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterRepository;
import net.silthus.schat.handler.types.JoinChannelHandler;
import net.silthus.schat.handler.types.PermissionHandler;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.settings.Setting;
import net.silthus.schat.user.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.channel.Channel.AUTO_JOIN;
import static net.silthus.schat.channel.Channel.JOIN_PERMISSION;
import static net.silthus.schat.channel.Channel.REQUIRES_JOIN_PERMISSION;
import static net.silthus.schat.channel.ChannelRepository.createInMemoryChannelRepository;
import static net.silthus.schat.chatter.ChatterRepository.createInMemoryChatterRepository;
import static net.silthus.schat.handler.types.UserJoinHandler.createUserJoinHandler;
import static net.silthus.schat.user.UserRepository.createInMemoryUserRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserManagerTests {

    private UserManager userManager;
    private User user;
    private ChatterRepository chatterRepository;
    private PermissionHandler permission;
    private Chatter chatter;
    private ChannelRepository channels;

    @BeforeEach
    void setUp() {
        chatterRepository = createInMemoryChatterRepository();
        channels = createInMemoryChannelRepository();
        userManager = new UserManager(createInMemoryUserRepository(),
            createUserJoinHandler(chatterRepository,
                channels,
                ChannelPermissionProvider.DEFAULT
            )
        );
        permission = mock(PermissionHandler.class);
        user = joinRandomUser();
        chatter = getChatter(user);
    }

    @NotNull
    private Chatter getChatter(User user) {
        return chatterRepository.get(user.getUniqueId());
    }

    private void mockNoPermission() {
        when(permission.hasPermission(anyString())).thenReturn(false);
    }

    private void mockHasPermission(String permission) {
        when(this.permission.hasPermission(permission)).thenReturn(true);
    }

    private <V> Channel channelWith(Setting<V> setting, V value) {
        return createChannelWith(builder -> builder.setting(setting, value));
    }

    private Channel createChannelWith(Function<Channel.Builder, Channel.Builder> config) {
        final Channel channel = config.apply(Channel.channel("test")).create();
        channels.add(channel);
        return channel;
    }

    private void assertJoinError(Chatter chatter, Channel channel) {
        assertThatExceptionOfType(JoinChannelHandler.AccessDenied.class)
            .isThrownBy(() -> chatter.join(channel));
    }

    private void assertJoinChannel(Chatter chatter, Channel channel) {
        chatter.join(channel);
        assertThat(chatter.getChannels()).contains(channel);
    }

    @NotNull
    private User joinRandomUser() {
        final User user = new User(Identity.identity(RandomString.make()), permission);
        userManager.join(user);
        return user;
    }

    @Test
    void join_stores_user_in_repository() {
        assertThat(userManager.contains(user.getUniqueId())).isTrue();
    }

    @Test
    void join_twice_addsUserOnce() {
        userManager.join(new User(Identity.identity(user.getUniqueId()), permission -> true));

        assertThat(userManager.all()).hasSize(1);
        assertThat(userManager.get(user.getUniqueId())).isSameAs(user);
    }

    @Test
    void join_creates_user_chatter() {
        assertThat(chatterRepository.contains(user.getUniqueId())).isTrue();
    }

    @Test
    void given_channel_with_restricted_access_join_fails() {
        mockNoPermission();
        final Channel channel = channelWith(REQUIRES_JOIN_PERMISSION, true);

        assertJoinError(chatter, channel);
    }

    @Test
    void given_user_with_permission_can_join() {
        mockHasPermission("schat.channel.test.join");
        assertJoinChannel(chatter, channelWith(REQUIRES_JOIN_PERMISSION, true));
    }

    @Test
    void given_user_with_different_permission_cannot_join() {
        mockHasPermission("schat.channel.test.join");
        final Channel channel = createChannelWith(builder -> builder
            .setting(REQUIRES_JOIN_PERMISSION, true)
            .setting(JOIN_PERMISSION, "foobar"));

        assertJoinError(chatter, channel);
        mockHasPermission("foobar");
        assertJoinChannel(chatter, channel);
    }

    @Test
    void given_unprotected_channel_all_users_can_join() {
        mockNoPermission();
        assertJoinChannel(chatter, channelWith(REQUIRES_JOIN_PERMISSION, false));
    }

    @Test
    void given_channel_with_autoJoin_userJoins() {
        final Channel channel = channelWith(AUTO_JOIN, true);
        final User user = joinRandomUser();
        assertThat(getChatter(user).getChannels()).contains(channel);
    }
}
