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

import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import net.silthus.schat.ui.View;
import net.silthus.schat.user.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.IdentityHelper.randomIdentity;
import static net.silthus.schat.TestHelper.assertNPE;
import static net.silthus.schat.message.Message.emptyMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class UserTests {

    private User user;

    @BeforeEach
    void setUp() {
        user = spy(new User(randomIdentity()));
    }

    @NotNull
    private Message sendMessage() {
        final Message message = emptyMessage();
        user.sendMessage(message);
        return message;
    }

    @NotNull
    private View setView(View view) {
        user.setView(view);
        return view;
    }

    @Test
    void givenSameIdentity_isEqual() {
        final Identity identity = randomIdentity();
        assertThat(new User(identity)).isEqualTo(new User(identity));
    }

    @Test
    void sendMessage_sendsRawMessage() {
        sendMessage();
        verify(user).sendRawMessage(Component.empty());
    }

    @Test
    void sendMessage_addsMessageToCache() {
        final Message message = sendMessage();
        assertThat(user.getMessages()).contains(message);
    }

    @Test
    void sendSameMessageTwice_cachesOnlyOnce() {
        final Message message = sendMessage();
        user.sendMessage(message);
        assertThat(user.getMessages()).containsOnlyOnce(message);
    }

    @Test
    void givenFreshUser_viewIsNotNull() {
        assertThat(user.getView()).isNotNull();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void setView_givenNullView_throwsNPE() {
        assertNPE(() -> user.setView(null));
    }

    @Test
    void sendMessage_rendersView() {
        final View view = setView(spy(new View(user)));
        sendMessage();
        verify(view).render();
    }
}
