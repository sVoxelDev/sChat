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

package net.silthus.schat.platform.plugin.adapter;

import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.MessageHandler;
import net.silthus.schat.chatter.PermissionHandler;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.platform.TestCommandSender;
import net.silthus.schat.ui.View;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.IdentityHelper.randomIdentity;
import static net.silthus.schat.message.Message.emptyMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class SenderChatterTests {

    private Chatter chatter;
    private PermissionHandler permissionHandler;
    private MessageHandler messageHandler;

    @BeforeEach
    void setUp() {
        permissionHandler = mock(PermissionHandler.class);
        messageHandler = mock(MessageHandler.class);
        chatter = createSender(randomIdentity());
    }

    private SenderFactory.SenderChatter<TestCommandSender> createSender(Identity identity) {
        return new SenderFactory.SenderChatter<>(new TestCommandSender(), identity, permissionHandler, messageHandler);
    }

    @NotNull
    private Message sendMessage() {
        final Message message = emptyMessage();
        chatter.sendMessage(message);
        return message;
    }

    @NotNull
    private View setView(View view) {
        chatter.setView(view);
        return view;
    }

    @Test
    void two_users_given_the_same_identity_are_equal() {
        final Identity identity = randomIdentity();
        assertThat(createSender(identity)).isEqualTo(createSender(identity));
    }

    @Nested class when_sendMessage_is_called {
        private @NotNull Message message;
        private View view;

        @BeforeEach
        void setUp() {
            view = setView(spy(new View(chatter)));
            message = sendMessage();
        }

        @Test
        void then_message_handler_is_invoked() {
            verify(messageHandler).sendMessage(any());
        }

        @Test
        void then_message_is_added_to_user_message_cache() {
            assertThat(chatter.getMessages()).contains(message);
        }

        @Test
        void twice_then_message_is_cached_only_once() {
            chatter.sendMessage(message);
            assertThat(chatter.getMessages()).containsOnlyOnce(message);
        }

        @Test
        void then_render_view_is_called() {
            verify(view).render();
        }
    }

    @Test
    void given_a_new_user_getView_is_not_null() {
        assertThat(chatter.getView()).isNotNull();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void when_setView_is_given_null_an_npe_is_thrown() {
        assertNPE(() -> chatter.setView(null));
    }

    @Test
    void when_hasPermission_is_called_then_permission_handler_is_invoked() {
        chatter.hasPermission("foobar");
        verify(permissionHandler).hasPermission("foobar");
    }
}
