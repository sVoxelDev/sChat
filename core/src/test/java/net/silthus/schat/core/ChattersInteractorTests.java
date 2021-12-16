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

package net.silthus.schat.core;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class ChattersInteractorTests {

    private User user;
    private ChattersInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new ChattersInteractor(userAdapter());
    }

    private UserAdapter userAdapter() {
        final UserAdapter userAdapter = mock(UserAdapter.class);
        mockUser(userAdapter);
        return userAdapter;
    }

    private void mockUser(final UserAdapter userAdapter) {
        final UUID playerId = UUID.randomUUID();
        user = new User(playerId, "test", text("Player"));
        doReturn(user).when(userAdapter).getUser(playerId);
    }

    @Test
    void create() {
        assertThat(interactor.getPlayerChatter(user.id())).extracting(
            Chatter::getId,
            Chatter::getName,
            Chatter::getDisplayName
        ).contains(
            user.id(),
            user.name(),
            user.displayName()
        );
    }
}
