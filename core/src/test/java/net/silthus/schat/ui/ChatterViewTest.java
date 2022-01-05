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

package net.silthus.schat.ui;

import net.kyori.adventure.text.Component;
import net.silthus.schat.sender.Sender;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.IdentityHelper.randomIdentity;
import static net.silthus.schat.chatter.Chatter.createChatter;
import static net.silthus.schat.ui.Renderer.TABBED_CHANNELS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ChatterViewTest {

    @Test
    void render_contains_marker() {
        final ChatterView view = new ChatterView(mock(Sender.class), createChatter(randomIdentity()), TABBED_CHANNELS);
        final Component render = view.render();
        assertThat(render.contains(View.MESSAGE_MARKER)).isTrue();
    }
}
